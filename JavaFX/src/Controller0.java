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

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Start choiceBox setting onaction event
        choiceBox.setOnAction((event) -> {
            loadBrandConsoles(choiceBox.getSelectionModel().getSelectedItem());
        });
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

    private void showError () {
        // Show the error
        txtError.setVisible(true);

        // Hide the error after 3 seconds
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), ae -> txtError.setVisible(false)));
        timeline.play();
    }

    @FXML
    public void listBrands() {
        JSONObject obj = new JSONObject("{}");
        obj.put("type", "marques");

        showLoading();
        UtilsHTTP.sendPOST(Main.protocol + "://" + Main.host + ":" + Main.port + "/dades", obj.toString(), (response) -> {
            listBrandsCallback(response);
            hideLoading();
        });
    }

    private void listBrandsCallback (String response) {

        JSONObject objResponse = new JSONObject(response);

        if (objResponse.getString("status").equals("OK")) {

            JSONArray JSONlist = objResponse.getJSONArray("result");
            ArrayList<String> brandsArr = new ArrayList<>();

            // Create arraylist with brands from JSON
            for (int i = 0; i < JSONlist.length(); i++) {
                brandsArr.add(JSONlist.getString(i));
            }

            // Set choicebox items with brands from arraylist
            choiceBox.getItems().clear();
            choiceBox.getItems().addAll(brandsArr);
            choiceBox.setValue(brandsArr.get(0));

            // Load consoles for the first brand
            loadBrandConsoles(brandsArr.get(0));
        } else {
            showError();
        }
    }

    @FXML
    private void loadBrandConsoles (String brand) {

        // Set selected brand in label
        txtSelected.setText(choiceBox.getValue());
        yPane.getChildren().clear();

        // Load list of consoles for this brand
        JSONObject obj = new JSONObject("{}");
        obj.put("type", "marca");
        obj.put("name", brand);

        // Ask for data
        showLoading();
        UtilsHTTP.sendPOST(Main.protocol + "://" + Main.host + ":" + Main.port + "/dades", obj.toString(), (response) -> {
            loadBrandConsolesCallback(response);
            hideLoading();
        });
    }

    private void loadBrandConsolesCallback (String response) {

        JSONObject objResponse = new JSONObject(response);

        if (objResponse.getString("status").equals("OK")) {

            JSONArray JSONlist = objResponse.getJSONArray("result");
            URL resource = this.getClass().getResource("./assets/listItem.fxml");

            // Clear the list of consoles
            yPane.getChildren().clear();

            // Add received consoles from the JSON to the list
            for (int i = 0; i < JSONlist.length(); i++) {

                // Get console information
                JSONObject console = JSONlist.getJSONObject(i);

                try {
                    // Load template and set controller
                    FXMLLoader loader = new FXMLLoader(resource);
                    Parent itemTemplate = loader.load();
                    ControllerItem itemController = loader.getController();
                
                    // Fill template with console information
                    itemController.setTitle(console.getString("name"));
                    itemController.setSubtitle(console.getString("processor"));
                    itemController.setColor("white");
                    
                    // Add template to the list
                    yPane.getChildren().add(itemTemplate);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            showError();
        }
    }

    public void loadConsoleInfo (String consoleName) {

        JSONObject obj = new JSONObject("{}");
        obj.put("type", "consola");
        obj.put("name", consoleName);

        showLoading();
        UtilsHTTP.sendPOST(Main.protocol + "://" + Main.host + ":" + Main.port + "/dades", obj.toString(), (response) -> {
            loadConsoleInfoCallback(response);
            hideLoading();
        });
    }

    private void loadConsoleInfoCallback (String response) {

        JSONObject objResponse = new JSONObject(response);
        
        if (objResponse.getString("status").equals("OK")) {

            JSONObject console = objResponse.getJSONObject("result");

            // Fill console info with the received data
            txtName.setText(console.getString("name"));
            txtDate.setText(console.getString("date"));
            txtBrand.setText(console.getString("brand"));
    
            try{
                // Load console image
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
}