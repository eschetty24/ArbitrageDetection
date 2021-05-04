package nets150.group.maven.eclipse; 

import java.net.URI;


import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import kong.unirest.json.JSONObject;

/**
 * This example demonstrates how to create a websocket connection to a server.
 * Only the most important callbacks are overloaded.
 */
public class WebSocketClientEndpoint extends WebSocketClient {

    public WebSocketClientEndpoint(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public WebSocketClientEndpoint(URI serverURI) {
        super(serverURI);
    }

    public WebSocketClientEndpoint(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        JSONObject json = new JSONObject();

        
        System.out.println("opened connection");
        // if you plan to refuse connection based on ip or httpfields overload:
        // onWebsocketHandshakeReceivedAsClient
    }

    @Override
    public void onMessage(String message) {
        System.out.println("received: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        System.out.println("Connection closed by " + (remote ? "remote peer" : "us") + " Code: "
                + code + " Reason: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        // if the error is fatal then onClose will be called additionally
    }
}