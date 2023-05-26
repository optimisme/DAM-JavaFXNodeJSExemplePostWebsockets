import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

public class UtilsWS  extends WebSocketClient {

    public static UtilsWS sharedInstance = null;
    private Consumer<String> onMessageCallBack = null;
    private String location = "";

    private UtilsWS (String location, Draft draft) throws URISyntaxException {
        super (new URI(location), draft);
        this.location = location;
    }

    static public UtilsWS getSharedInstance (String location) {

        if (sharedInstance == null) {
            try {
                sharedInstance = new UtilsWS(location, (Draft) new Draft_6455());
                sharedInstance.connect();
            } catch (URISyntaxException e) { 
                e.printStackTrace(); 
                System.out.println("Error: " + location + " no és una direcció URI de WebSocket vàlida");
            }
        }

        return sharedInstance;
    }

    public void onMessage (Consumer<String> callBack) {
        this.onMessageCallBack = callBack;
    }

    @Override
    public void onMessage(String message) {
        if (onMessageCallBack != null) {
            onMessageCallBack.accept(message);
        }
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("WS connected to: " + getURI());
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("WS closed connection from: " + getURI());

        if (remote) {
            reconnect();
        }
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("WS connectio error: " + ex.getMessage());
        if (ex.getMessage().contains("Connection refused") || ex.getMessage().contains("Connection reset")) {
            reconnect();
        }
    }

    public void safeSend(String text) {
        try {
            sharedInstance.send(text);
        } catch (Exception ex) {
            System.out.println("Error al enviar el missatge");
            System.out.println(ex);
        }
    }

    public void reconnect () {
        System.out.println("WS reconnecting to: " + this.location);

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) { e.printStackTrace(); }

        Consumer<String> oldCallBack = this.onMessageCallBack;
        String oldLocation = this.location;
        sharedInstance.close();
        sharedInstance = null;
        getSharedInstance(oldLocation);
        sharedInstance.onMessage(oldCallBack);
    }
}
