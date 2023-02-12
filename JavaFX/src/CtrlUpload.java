import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
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

            getPrivateFile(objResponse.getString("name"));
        }
    }

    private void getPrivateFile (String name) {

        JSONObject obj = new JSONObject("{}");
        obj.put("type", "getPrivateFile");
        obj.put("name", name);

        showLoading();
        UtilsHTTP.sendPOST(Main.protocol + "://" + Main.host + ":" + Main.port + "/dades", obj.toString(), (response) -> {
            getPrivateFileCallback(response);
            hideLoading();
        });
    }

    private void getPrivateFileCallback (String response) {

        JSONObject objResponse = new JSONObject(response);

        if (objResponse.getString("status").equals("OK")) {

            String base64 = objResponse.getString("base64");
            byte[] decodedBytes = Base64.getDecoder().decode(base64);
            
            Image img = new Image(new java.io.ByteArrayInputStream(decodedBytes));
            image.setImage(img);
        }
    }
}