import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Path;
import javafx.util.Duration;

public class Controller0 implements Initializable {

    @FXML
    private Label txtName, txtDate, txtBrand;

    @FXML
    private ImageView imgConsole;

    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private Path loading;
    private int loadingCounter = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Start choiceBox
        choiceBox.setOnAction((event) -> {
            System.out.println("Selected brand" + choiceBox.getValue());
        });

        // Start loading animation
        KeyValue kvRight = new KeyValue(loading.rotateProperty(), 360, Interpolator.LINEAR);
        KeyFrame kfRight = new KeyFrame(Duration.seconds(1), kvRight);
        Timeline timelineRight = new Timeline();
        timelineRight.setCycleCount(Animation.INDEFINITE);
        timelineRight.getKeyFrames().add(kfRight);
        timelineRight.play();
    }

    @FXML
    public void loadGameCube() {
        loadConsoleInfo("GameCube");
    }

    @FXML
    public void loadXbox() {
        loadConsoleInfo("Xbox One");
    }

    @FXML
    public void loadPlaystation3() {
        loadConsoleInfo("Playstation 3");
    }

    private void showLoading () {
        loadingCounter++;
        loading.setVisible(true);
    }

    private void hideLoading () {
        loadingCounter--;
        if (loadingCounter <= 0) {
            loadingCounter = 0;
            loading.setVisible(false);
        }
    }

    @FXML
    public void listBrands() {
        showLoading();
        MsgPostLlista msg = new MsgPostLlista("marques");
        UtilsHTTP.sendPOST(Main.protocol + "://" + Main.host + ":" + Main.port + "/dades", UtilsJSON.stringify(msg), (response) -> {
            MsgResStringArr brands = (MsgResStringArr) UtilsJSON.parse(response, MsgResStringArr.class);
            choiceBox.getItems().clear();
            choiceBox.getItems().addAll(brands.result);
            choiceBox.setValue(brands.result.get(0));
            hideLoading();
        });
    }

    private void loadConsoleInfo (String consoleName) {
        showLoading();
        MsgPost msg = new MsgPost("consola", consoleName);
        UtilsHTTP.sendPOST(Main.protocol + "://" + Main.host + ":" + Main.port + "/dades", UtilsJSON.stringify(msg), (response) -> {
            setConsoleInfo(response);
            hideLoading();
        });
    }

    private void setConsoleInfo (String response) {
        MsgResponse msg = (MsgResponse) UtilsJSON.parse(response, MsgResponse.class);
        MsgConsola console = msg.result;
        txtName.setText(console.name);
        txtDate.setText(console.date);
        txtBrand.setText(console.brand);

        try{
            Image image = new Image(Main.protocol + "://" + Main.host + ":" + Main.port + "/" + console.image); 
            imgConsole.setImage(image); 
            imgConsole.setFitWidth(200);
            imgConsole.setPreserveRatio(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}