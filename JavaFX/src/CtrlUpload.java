import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;

public class CtrlUpload {

    @FXML
    private ProgressIndicator loading;
    private int loadingCounter = 0;

    @FXML
    private ImageView image;

    @FXML
    private void setViewSockets() {
        UtilsViews.setViewAnimating("ViewSockets");
    }

    @FXML
    private void chooseFile () {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        
        Window window = UtilsViews.parentContainer.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(window);

        if (selectedFile != null) {
            uploadFile(selectedFile);
        }
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

    private void uploadFile (File selectedFile) {

        try {
            // Read image from File and convert to Base64
            byte[] fileContent = Files.readAllBytes(selectedFile.toPath());
            String name = selectedFile.getName();
            String base64 = Base64.getEncoder().encodeToString(fileContent);

            JSONObject obj = new JSONObject("{}");
            obj.put("type", "uploadFile");
            obj.put("name", name);
            obj.put("base64", base64);

            showLoading();
            UtilsHTTP.sendPOST(Main.protocol + "://" + Main.host + ":" + Main.port + "/dades", obj.toString(), (response) -> {
                uploadFileCallback(response);
                hideLoading();
            });

        } catch (IOException e) { e.printStackTrace(); }
    }

    private void uploadFileCallback (String response) {

        JSONObject objResponse = new JSONObject(response);
        
        if (objResponse.getString("status").equals("OK")) {

            getBinaryFile(objResponse.getString("name"));
        }
    }

    private void getBinaryFile (String name) {

        JSONObject obj = new JSONObject("{}");
        obj.put("type", "getBinaryFile");
        obj.put("name", name);

        showLoading();
        UtilsHTTP.sendPOST(Main.protocol + "://" + Main.host + ":" + Main.port + "/dades", obj.toString(), (response) -> {
            getBinaryFileCallback(response);
            hideLoading();
        });
    }

    private void getBinaryFileCallback (String response) {

        JSONObject objResponse = new JSONObject(response);

        if (objResponse.getString("status").equals("OK")) {

            String base64 = objResponse.getString("base64");
            byte[] decodedBytes = Base64.getDecoder().decode(base64);
            
            Image img = new Image(new java.io.ByteArrayInputStream(decodedBytes));
            image.setImage(img);
        }
    }
}