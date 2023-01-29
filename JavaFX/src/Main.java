import org.json.JSONObject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class Main extends Application {

    public static UtilsWS socketClient;

    public static int port = 3000;
    public static String protocol = "http";
    public static String host = "localhost";
    public static String protocolWS = "ws";

    // Exemple de configuració per Railway
    // public static int port = 443;
    // public static String protocol = "https";
    // public static String host = "server-production-46ef.up.railway.app";
    // public static String protocolWS = "wss";

    // Camps JavaFX a modificar
    public static Label consoleName = new Label();
    public static Label consoleDate = new Label();
    public static Label consoleBrand = new Label();
    public static ImageView imageView = new ImageView(); 

    public static void main(String[] args) {

        // Iniciar WebSockets
        socketClient = UtilsWS.getSharedInstance(protocolWS + "://" + host + ":" + port);
        socketClient.onMessage((response) -> {
            // JavaFX necessita que els canvis es facin des de el thread principal
            Platform.runLater(()->{ 
                // Fer aquí els canvis a la interficie
                JSONObject msgObj = new JSONObject(response);
                Controller1 ctrl = (Controller1) UtilsViews.getController("View1");
                ctrl.receiveMessage(msgObj);
            });
        });

        // Iniciar app JavaFX   
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {

        final int windowWidth = 800;
        final int windowHeight = 600;

        UtilsViews.stage = stage;
        UtilsViews.parentContainer.setStyle("-fx-font: 14 arial;");
        UtilsViews.addView(getClass(), "View0", "./assets/view0.fxml");
        UtilsViews.addView(getClass(), "View1", "./assets/view1.fxml");

        Scene scene = new Scene(UtilsViews.parentContainer);
        
        stage.setScene(scene);
        stage.onCloseRequestProperty(); // Call close method when closing window
        stage.setTitle("JavaFX - NodeJS");
        stage.setMinWidth(windowWidth);
        stage.setMinHeight(windowHeight);
        stage.show();

        // Image icon = new Image("file:./assets/icon.png");
        // stage.getIcons().add(icon);
    }

    @Override
    public void stop() { 
        socketClient.close();
        System.exit(1); // Kill all executor services
    }
}
