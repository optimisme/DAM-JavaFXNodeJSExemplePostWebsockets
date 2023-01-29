import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;

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
            System.out.println("Selected brand: " + choiceBox.getValue());
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
        JSONObject obj = new JSONObject("{}");
        obj.put("type", "marques");
        UtilsHTTP.sendPOST(Main.protocol + "://" + Main.host + ":" + Main.port + "/dades", obj.toString(), (response) -> {
            JSONObject objResponse = new JSONObject(response);
            if (objResponse.getString("status").equals("OK")) {
                JSONArray brandsObj = objResponse.getJSONArray("result");
                ArrayList<String> brandsArr = new ArrayList<>();
                for (int i = 0; i < brandsObj.length(); i++) {
                    brandsArr.add(brandsObj.getString(i));
                }
                choiceBox.getItems().clear();
                choiceBox.getItems().addAll(brandsArr);
                choiceBox.setValue(brandsArr.get(0));
            }
            hideLoading();
        });
    }

    private void loadConsoleInfo (String consoleName) {
        showLoading();
        JSONObject obj = new JSONObject("{}");
        obj.put("type", "consola");
        obj.put("name", consoleName);
        UtilsHTTP.sendPOST(Main.protocol + "://" + Main.host + ":" + Main.port + "/dades", obj.toString(), (response) -> {
            setConsoleInfo(response);
            hideLoading();
        });
    }

    private void setConsoleInfo (String response) {
        JSONObject objResponse = new JSONObject(response);
        if (objResponse.getString("status").equals("OK")) {
            JSONObject console = objResponse.getJSONObject("result");

            txtName.setText(console.getString("name"));
            txtDate.setText(console.getString("date"));
            txtBrand.setText(console.getString("brand"));
    
            try{
                Image image = new Image(Main.protocol + "://" + Main.host + ":" + Main.port + "/" + console.getString("image")); 
                imgConsole.setImage(image); 
                imgConsole.setFitWidth(200);
                imgConsole.setPreserveRatio(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}