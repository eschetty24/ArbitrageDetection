
package nets150.group.maven.eclipse;

import java.io.Console;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;

import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;

/**
 * This example demonstrates how to create a websocket connection to a server.
 * Only the most important callbacks are overloaded.
 */
public class App {

    private static List<Map<String, Object>> rates = new ArrayList<>();
    private static Graph g;

    private static String[] altKeys = { "c2889aaad3i8rjpavj20", "c283sd2ad3i8rjpap4cg" };
    private static int swapNum = 0;

    // Storing valid currencies to limit number of calls to API, as there is a
    // limit.
    private static String[] currencies = ("AED\n" + "AFN\n" + "ALL\n" + "AMD\n" + "ANG\n" + "AOA\n"
            + "ARS\n" + "AUD\n" + "AWG\n" + "AZN\n" + "BAM\n" + "BBD\n" + "BDT\n" + "BGN\n"
            + "BHD\n" + "BIF\n" + "BMD\n" + "BND\n" + "BOB\n" + "BRL\n" + "BSD\n" + "BTC\n"
            + "BTN\n" + "BWP\n" + "BYN\n" + "BYR\n" + "BZD\n" + "CAD\n" + "CDF\n" + "CHF\n"
            + "CLF\n" + "CLP\n" + "CNY\n" + "COP\n" + "CRC\n" + "CUC\n" + "CUP\n" + "CVE\n"
            + "CZK\n" + "DJF\n" + "DKK\n" + "DOP\n" + "DZD\n" + "EGP\n" + "ERN\n" + "ETB\n"
            + "EUR\n" + "FJD\n" + "FKP\n" + "GBP\n" + "GEL\n" + "GGP\n" + "GHS\n" + "GIP\n"
            + "GMD\n" + "GNF\n" + "GTQ\n" + "GYD\n" + "HKD\n" + "HNL\n" + "HRK\n" + "HTG\n"
            + "HUF\n" + "IDR\n" + "ILS\n" + "IMP\n" + "INR\n" + "IQD\n" + "IRR\n" + "ISK\n"
            + "JEP\n" + "JMD\n" + "JOD\n" + "JPY\n" + "KES\n" + "KGS\n" + "KHR\n" + "KMF\n"
            + "KPW\n" + "KRW\n" + "KWD\n" + "KYD\n" + "KZT\n" + "LAK\n" + "LBP\n" + "LKR\n"
            + "LRD\n" + "LSL\n" + "LTL\n" + "LVL\n" + "LYD\n" + "MAD\n" + "MDL\n" + "MGA\n"
            + "MKD\n" + "MMK\n" + "MNT\n" + "MOP\n" + "MRO\n" + "MUR\n" + "MVR\n" + "MWK\n"
            + "MXN\n" + "MYR\n" + "MZN\n" + "NAD\n" + "NGN\n" + "NIO\n" + "NOK\n" + "NPR\n"
            + "NZD\n" + "OMR\n" + "PAB\n" + "PEN\n" + "PGK\n" + "PHP\n" + "PKR\n" + "PLN\n"
            + "PYG\n" + "QAR\n" + "RON\n" + "RSD\n" + "RUB\n" + "RWF\n" + "SAR\n" + "SBD\n"
            + "SCR\n" + "SDG\n" + "SEK\n" + "SGD\n" + "SHP\n" + "SLL\n" + "SOS\n" + "SRD\n"
            + "STD\n" + "SVC\n" + "SYP\n" + "SZL\n" + "THB\n" + "TJS\n" + "TMT\n" + "TND\n"
            + "TOP\n" + "TRY\n" + "TTD\n" + "TWD\n" + "TZS\n" + "UAH\n" + "UGX\n" + "USD\n"
            + "UYU\n" + "UZS\n" + "VEF\n" + "VND\n" + "VUV\n" + "WST\n" + "XAF\n" + "XAG\n"
            + "XAU\n" + "XCD\n" + "XDR\n" + "XOF\n" + "XPF\n" + "YER\n" + "ZAR\n" + "ZMK\n"
            + "ZMW\n" + "ZWL").split("\n");

    /*
     * Finnhub API limits 60 calls per second. Signed up for second account to get
     * multiple API keys lol :).
     */
    private static void swapApiKeys() {
        try {
            String key = altKeys[swapNum];
            swapNum++;
            Unirest.config().defaultBaseUrl("https://finnhub.io/api/v1")
                    .setDefaultHeader("X-Finnhub-Token", key);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out
                    .println("API keys need to cool down. Please try again in a couple of minutes");
        }

    }

    private static void getForexRatesForBase(String base) {
        HttpResponse<JsonNode> raw = null;
        try {

            raw = Unirest.get("/forex/rates").queryString("base", base).asJson();

            rates.add(raw.getBody().getObject().getJSONObject("quote").toMap());
        } catch (kong.unirest.json.JSONException e) {
            System.out.println("HAD TO SWAP");
            swapApiKeys();
            getForexRatesForBase(base);
        }

    }

    private static double calcWeight(double weight) {

        return -1 * Math.log(weight);
    }

    private static void buildGraph() {
        for (int i = 0; i < currencies.length; i++) {
            getForexRatesForBase(currencies[i]);
            Object[] exchanges = rates.get(i).values().toArray();
            for (int j = 0; j < exchanges.length; j++) {
                if (i != j) {
                    String d = exchanges[j].toString();
                    double weight = calcWeight(Double.valueOf(d).doubleValue());
                    g.addEdge(i, j, weight, false);

                }

            }

        }
        System.out.println("total edges " + Graph.totalEdges);
    }

    private static double getProfit(List<Integer> path) {
        double profit = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            System.out.println((-1 * (g.getWeight(path.get(i), path.get(i + 1))) + "+"));
            profit += -1 * (g.getWeight(path.get(i), path.get(i + 1)));
        }
        System.out.println("profit: " + profit);
        return profit;
    }

    private static void getCharts(WebSocketClientEndpoint ws, String curr1, String curr2) {
        ws.connect();
        while (!(ws.isOpen())) {
            ; //hang 
        }
        String msg = "{\"type\":\"subscribe\",\"symbol\":\"USD\"}";
        ws.send(msg);
    }

    public static void main(String[] args) throws URISyntaxException {

        Unirest.config().defaultBaseUrl("https://finnhub.io/api/v1")
                .setDefaultHeader("X-Finnhub-Token", "c20ra02ad3iec96dij7g");

        WebSocketClientEndpoint ws = new WebSocketClientEndpoint(
                new URI("wss://ws.finnhub.io?token=c20ra02ad3iec96dij7g"));
         
        g = new Graph(currencies.length);
        buildGraph();
        BellmanFord bf = new BellmanFord(g);
        bf.run(0);

        List<Integer> path = bf.getNegativeCycles();
        if (path == null) {
            System.out.println("No current arbitrage");
            String curr1 = currencies[bf.closestV1];
            String curr2 = currencies[bf.closestV2];
            System.out.println("Best opportunity is " + curr1 + "->" + curr2
                    + " if exchange rate drops " + bf.closestDiff);

            Scanner s = new Scanner(System.in);
            String yes = "Do you wish to get realtime quotes for " + curr1 + " and "
                    + curr2 + "? (y/n)";
            System.out.println(yes);
            if (s.nextLine().equals("y")) {
                getCharts(ws, curr1, curr2);
            }

        } else {
            for (int i = 0; i < path.size() - 1; i++) {
                System.out.println(currencies[path.get(i)] + "->" + currencies[path.get(i + 1)]
                        + " rates:" + rates.get(path.get(i)).get(currencies[path.get(i + 1)]));
            }
            System.out.println("profit: " + getProfit(path));
        }

    }

}
