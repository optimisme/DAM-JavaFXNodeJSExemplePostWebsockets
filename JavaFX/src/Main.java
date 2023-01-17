import java.io.FileInputStream;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

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
    public void start(Stage primaryStage) {

        // Definir mida finestra
        int windowHeight = 500;
        int windowWidth = 750;

        // Construir interficie
        VBox root = buildInterface(primaryStage);

        // Definir escena
        Scene  scene = new Scene(root);

        // Iniciar finestra d'app
        primaryStage.setTitle("NodeJS i JavaFX");
        primaryStage.onCloseRequestProperty();
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(true);
        primaryStage.setHeight(windowHeight);
        primaryStage.setWidth(windowWidth);
        primaryStage.setMinHeight(windowHeight);
        primaryStage.setMinWidth(windowWidth);
        primaryStage.heightProperty().addListener((observable, oldValue, newvalue) -> {
            double titleHeight = primaryStage.getHeight() - scene.getHeight();
            double rootHeight = primaryStage.getHeight() - titleHeight;
            root.setPrefHeight(rootHeight);
        });

        // Definir icona d'app
        Image icon = new Image("file:./assets/icon.png");
        primaryStage.getIcons().add(icon);
    }

    @Override
    public void stop() { }

    public VBox buildInterface(Stage primaryStage) {

        // Definir la divisió vertical
        VBox vbox = new VBox(0);
        vbox.setAlignment(Pos.TOP_LEFT);

        // Add button that calls "http://localhost:3000/dades" with POST of type "consola" and name "GameCube"
        Button buttonGC = new Button("GameCube");
        buttonGC.setOnAction(e -> {
            MsgPost msg = new MsgPost("consola", "GameCube");
            UtilsHTTP.sendPOST("http://localhost:3000/dades", UtilsJSON.stringify(msg), (response) -> {
                setConsola(response);
            });
        });

        // Add button that calls "http://localhost:3000/dades" with POST of type "consola" and name "Xbox One"
        Button buttonXO = new Button("Xbox One");
        buttonXO.setOnAction(e -> {
            MsgPost msg = new MsgPost("consola", "Xbox One");
            UtilsHTTP.sendPOST("http://localhost:3000/dades", UtilsJSON.stringify(msg), (response) -> {
                setConsola(response);
            });
        });

        // Add button that calls "http://localhost:3000/dades" with POST of type "consola" and name "Playstatoin 3"
        Button buttonP3 = new Button("Playstation 3");
        buttonP3.setOnAction(e -> {
            MsgPost msg = new MsgPost("consola", "Playstation 3");
            UtilsHTTP.sendPOST("http://localhost:3000/dades", UtilsJSON.stringify(msg), (response) -> {
                setConsola(response);
            });
        });

        // Set console information properties
        Label titol0 = new Label("Dades de la consola:");
        titol0.setPadding(new Insets(0, 0, 20, 0));
        consoleName.setStyle("-fx-font-weight: bold;-fx-font-size: 25px;");

        // Add vertical separators
        Separator vS0 = new Separator();
        vS0.setPrefHeight(25);

        vbox.getChildren().add(buttonGC);
        vbox.getChildren().add(buttonXO);
        vbox.getChildren().add(buttonP3);
        vbox.getChildren().add(vS0);
        vbox.getChildren().add(titol0);
        vbox.getChildren().add(consoleName);
        vbox.getChildren().add(consoleDate);
        vbox.getChildren().add(consoleBrand);
        vbox.getChildren().add(imageView);

        return vbox;
    }

    void setConsola (String response) {
        MsgResponse msg = (MsgResponse) UtilsJSON.parse(response, MsgResponse.class);
        MsgConsola consola = msg.result;
        System.out.println(response);
        System.out.println(consola.name);
        consoleName.setText(consola.name);
        consoleDate.setText(consola.date);
        consoleBrand.setText(consola.brand);

        try{
            Image image = new Image("http://localhost:3000/" + consola.image); 
            imageView.setImage(image); 
            imageView.setFitWidth(200);
            imageView.setPreserveRatio(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
