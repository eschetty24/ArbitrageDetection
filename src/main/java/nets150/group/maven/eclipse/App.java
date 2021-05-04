
package nets150.group.maven.eclipse;

import java.net.URI;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

public class App {

    private static List<Map<String, Object>> rates = new ArrayList<>();
    private static Graph g;

    private static String[] altKeys = { "c2889aaad3i8rjpavj20", "c283sd2ad3i8rjpap4cg" };
    private static String webSocketApiKey = "c28qk5qad3if6b4c2p3g";
    private static int swapNum = 0;
    private static Map<String, Integer> currencyMap;
    
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

    private static void swapApiKeys() {
        try {
            String key = altKeys[swapNum];
            swapNum++;
            Unirest.config().defaultBaseUrl("https://finnhub.io/api/v1")
                    .setDefaultHeader("X-Finnhub-Token", key);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out
                    .println("API keys need to cool down. Please try again in a couple of minutes");
            System.exit(0);
        }

    }

    private static void getForexRatesForBase(String base) {
        HttpResponse<JsonNode> raw = null;
        try {

            raw = Unirest.get("/forex/rates").queryString("base", base).asJson();

            rates.add(raw.getBody().getObject().getJSONObject("quote").toMap());
        } catch (kong.unirest.json.JSONException e) {
            System.out.println("Current API key too hot... swapping for new one.");
            System.out.println("\n");
            swapApiKeys();
            getForexRatesForBase(base);
        }

    }

    private static double calcWeight(double weight) {

        return -1 * Math.log(weight);
    }

    private static void buildGraph() {
        System.out.println("Please wait...");
        System.out.println("\n");
        System.out.println("Building Graph...");
        System.out.println("\n");
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
        System.out.println("Total edges added: " + Graph.totalEdges);
        System.out.println("\n");
    }

    private static double getProfit(List<Integer> path) {
        double profit = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            profit += -1 * (g.getWeight(path.get(i), path.get(i + 1)));
        }
        profit = Math.abs((1 - profit)) * 100;
        return profit;
    }

    private static void getCharts(WebSocketClientEndpoint ws, List<String> curr) {
        ws.connect();
        while (!(ws.isOpen())) {
            ; // hang
        }
        for (String c : curr) {
            String r = "{\"type\":\"subscribe\",\"symbol\":" + "\"" + c + "\"" + "}";
            System.out.println(r);
            ws.send(r);
        }
    }

    private static void buildCurrencyMap() {
        currencyMap = new HashMap<String, Integer>();
        for (int i = 0; i < currencies.length; i++) {
            currencyMap.put(currencies[i], i);
        }
    }

    private static void addCustomExchanges(String[] data) {
        buildCurrencyMap();
        int u = currencyMap.get(data[0]);
        int v = currencyMap.get(data[1]);
        double weight = calcWeight(Double.parseDouble(data[2]));
        g.addEdge(u, v, weight, false);
    }

    public static void main(String[] args) throws URISyntaxException {
        
        String title = "\n"
                + "  ___       _     _ _                         ______     _            _             \n"
                + " / _ \\     | |   (_) |                        |  _  \\   | |          | |            \n"
                + "/ /_\\ \\_ __| |__  _| |_ _ __ __ _  __ _  ___  | | | |___| |_ ___  ___| |_ ___  _ __ \n"
                + "|  _  | '__| '_ \\| | __| '__/ _` |/ _` |/ _ \\ | | | / _ \\ __/ _ \\/ __| __/ _ \\| '__|\n"
                + "| | | | |  | |_) | | |_| | | (_| | (_| |  __/ | |/ /  __/ ||  __/ (__| || (_) | |   \n"
                + "\\_| |_/_|  |_.__/|_|\\__|_|  \\__,_|\\__, |\\___| |___/ \\___|\\__\\___|\\___|\\__\\___/|_|   \n"
                + "                                   __/ |                                            \n"
                + "                                  |___/                                             \n"
                + "                                                                                    \n"                                                                                               
                + "Written by: Nolan Biscaro, Elyse Schetty, and Baptiste Audenaert \n";
        System.out.println(title + "\n");
        

        Unirest.config().defaultBaseUrl("https://finnhub.io/api/v1")
                .setDefaultHeader("X-Finnhub-Token", webSocketApiKey);

        WebSocketClientEndpoint ws = new WebSocketClientEndpoint(
                new URI("wss://ws.finnhub.io?token=c20ra02ad3iec96dij7g"));

        g = new Graph(currencies.length);
        buildGraph();
        Scanner s = new Scanner(System.in);
        System.out.println(
                "Please enter a threshold. The threshold is defined as the minimum percentage increase for an arbitrage opportunity to be recognized. (Recommended 1% where input 1 = 1%)");
        Double threshold = Double.parseDouble(s.nextLine());
        BellmanFord bf = new BellmanFord(g, threshold);

        System.out.println("Do you wish to enter any custom exchange rates?(y/n)");
        if (s.nextLine().equals("y")) {
            System.out.println("How many new exchange rates do you wish to add?");
            int length = Integer.parseInt(s.nextLine());
            System.out.println(
                    "Ex. enter the input as USD,AUD,0.57 to change the exchange rate between USD/AUD to 0.57. Any other format will be rejected");
            for (int i = 0; i < length; i++) {
                String[] raw = s.nextLine().split(",");
                addCustomExchanges(raw);
            }
        }

        bf.run(0);

        List<Integer> path = bf.getNegativeCycles();
        if (path == null) {
            System.out.println("No current arbitrage \n");
            String curr1 = currencies[bf.closestV1];
            String curr2 = currencies[bf.closestV2];
            System.out.println("Best opportunity is " + curr1 + "->" + curr2
                    + " if exchange rate drops " + bf.closestDiff + "\n");

            String yes = "Do you wish to get realtime trades for " + curr1 + " and " + curr2
                    + "? (y/n)";
            System.out.println(yes);
            if (s.nextLine().equals("y")) {
                List<String> currencies = new ArrayList<String>();
                currencies.add(curr1);
                getCharts(ws, currencies);
                System.out.println(
                        "**Note that if the currency selected is not often traded, or markets are not currently open, there may be no active trades.**");
            } else {
                String other = "Do you wish to get realtime trades for any other foreign currencies/crypto/stocks? (y/n)";
                System.out.println(other);
                if (s.nextLine().equals("y")) {
                    System.out.println(
                            "**Note that if the currency selected is not often traded, or markets are not currently open, there may be no active trades.**");
                    System.out.println(
                            "Please enter a stock symbol ex. (AAPL), crypto symbol ex. (BINANCE:BTCUSDT), or currency symbol (USD)");
                    List<String> currencies = new ArrayList<String>();
                    currencies.add(s.nextLine());
                    getCharts(ws, currencies);
                    System.out.println("Enter \"q\" to quit");
                    while (!(s.nextLine().equals("q"))) {
                        ;
                    }
                    ws.close();
                    System.exit(0);

                }
            }

        } else {
            System.out.println("ARBITRAGE FOUND");
            System.out.println("\n");
            for (int i = 0; i < path.size() - 1; i++) {
                System.out.println(currencies[path.get(i)] + "->" + currencies[path.get(i + 1)]
                        + " rates:" + rates.get(path.get(i)).get(currencies[path.get(i + 1)]));
            }
            System.out.println("profit: " + getProfit(path) + "%");
        }

    }

}
