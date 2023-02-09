import org.json.JSONObject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
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

        // Iniciar app JavaFX   
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {

        final int windowWidth = 800;
        final int windowHeight = 600;

        UtilsViews.parentContainer.setStyle("-fx-font: 14 arial;");
        UtilsViews.addView(getClass(), "ViewPost", "./assets/viewPost.fxml");
        UtilsViews.addView(getClass(), "ViewSockets", "./assets/viewSockets.fxml");
        UtilsViews.addView(getClass(), "ViewUpload", "./assets/viewUpload.fxml");

        Scene scene = new Scene(UtilsViews.parentContainer);
        
        stage.setScene(scene);
        stage.onCloseRequestProperty(); // Call close method when closing window
        stage.setTitle("JavaFX - NodeJS");
        stage.setMinWidth(windowWidth);
        stage.setMinHeight(windowHeight);
        stage.show();

        // Add icon only if not Mac
        if (!System.getProperty("os.name").contains("Mac")) {
            Image icon = new Image("file:./assets/icon.png");
            stage.getIcons().add(icon);
        }

        // Iniciar WebSockets
        socketClient = UtilsWS.getSharedInstance(protocolWS + "://" + host + ":" + port);
        socketClient.onMessage((response) -> {
            
            // JavaFX necessita que els canvis es facin des de el thread principal
            Platform.runLater(()->{ 
                // Fer aquí els canvis a la interficie
                JSONObject msgObj = new JSONObject(response);
                CtrlSockets ctrl = (CtrlSockets) UtilsViews.getController("ViewSockets");
                ctrl.receiveMessage(msgObj);
            });
        });
    }

    @Override
    public void stop() { 
        socketClient.close();
        System.exit(1); // Kill all executor services
    }
}
