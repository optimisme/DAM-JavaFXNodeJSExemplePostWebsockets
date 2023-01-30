import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class Controller0 implements Initializable {

    @FXML
    private Label txtName, txtDate, txtBrand, txtSelected, txtError;

    @FXML
    private ImageView imgConsole;

    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private ProgressIndicator loading;
    private int loadingCounter = 0;

    @FXML
    private VBox yPane = new VBox();

    private ArrayList<String> brandsArr = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Start choiceBox
        choiceBox.setOnAction((event) -> {
            loadBrandList(choiceBox.getSelectionModel().getSelectedItem());
        });
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

    @FXML
    private void setViewWS() {
        UtilsViews.setViewAnimating("View1");
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
                JSONArray JSONlist = objResponse.getJSONArray("result");
                brandsArr.clear();
                for (int i = 0; i < JSONlist.length(); i++) {
                    brandsArr.add(JSONlist.getString(i));
                }

                choiceBox.getItems().clear();
                choiceBox.getItems().addAll(brandsArr);
                choiceBox.setValue(brandsArr.get(0));

                loadBrandList(brandsArr.get(0));
            } else {
                showError();
            }
            hideLoading();
        });
    }

    public void loadConsoleInfo (String consoleName) {
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
        } else {
            showError();
        }
    }

    @FXML
    private void loadBrandList (String brand) {

        // Set selected brand in label
        txtSelected.setText(choiceBox.getValue());
        yPane.getChildren().clear();

        // Load list of consoles for this brand
        showLoading();
        JSONObject obj = new JSONObject("{}");
        obj.put("type", "marca");
        obj.put("name", brand);

        // Ask for data
        UtilsHTTP.sendPOST(Main.protocol + "://" + Main.host + ":" + Main.port + "/dades", obj.toString(), (response) -> {
            JSONObject objResponse = new JSONObject(response);
            if (objResponse.getString("status").equals("OK")) {
                JSONArray JSONlist = objResponse.getJSONArray("result");
                URL resource = this.getClass().getResource("./assets/listItem.fxml");

                yPane.getChildren().clear();
                for (int i = 0; i < JSONlist.length(); i++) {
                    JSONObject console = JSONlist.getJSONObject(i);
                    FXMLLoader loader = new FXMLLoader(resource);
                    try {
                        Parent itemTemplate = loader.load();
                        ControllerItem itemController = loader.getController();
                    
                        itemController.setTitle(console.getString("name"));
                        itemController.setSubtitle(console.getString("processor"));
                        itemController.setColor("white");
                        
                        yPane.getChildren().add(itemTemplate);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                showError();
            }
            hideLoading();
        });
    }

    private void showError () {
        
        // Show the error
        txtError.setVisible(true);

        // Hide the error after 3 seconds
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), ae -> txtError.setVisible(false)));
        timeline.play();
    }
}