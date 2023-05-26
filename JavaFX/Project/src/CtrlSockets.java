import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class CtrlSockets implements Initializable {

    @FXML
    private Label txtId;

    @FXML
    private TextField txtField;

    @FXML
    private TextArea txtArea;

    @FXML
    private ChoiceBox<String> choiceType, choiceUser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Start choiceBox
        choiceType.getItems().clear();
        choiceType.getItems().addAll("broadcast", "bounce", "private");
        choiceType.setValue(choiceType.getItems().get(0));
        choiceType.setOnAction((event) -> {
            if (choiceType.getValue().equals("private")) {
                choiceUser.setDisable(false);
            } else {
                choiceUser.setDisable(true);
            }
        });
        choiceUser.setDisable(true);

        // Send message when pressing enter
        txtField.setOnAction((event) -> {
            sendMessage();
        });
    }

    @FXML
    private void setViewPost() {
        UtilsViews.setViewAnimating("ViewPost");
    }

    @FXML
    private void setViewUpload() {
        UtilsViews.setViewAnimating("ViewUpload");
    }

    @FXML
    private void sendMessage () {
        String txt = txtField.getText();

        JSONObject obj = new JSONObject("{}");
        String type = choiceType.getValue().toLowerCase();
        obj.put("type", type);
        obj.put("message", txt);

        if (type.equals("private")) {
            String destination = choiceUser.getValue();
            if (destination == null) {
                destination = "";
            }
            obj.put("destination", destination);
        }

        Main.socketClient.safeSend(obj.toString());
        System.out.println("Send WebSocket: " + obj.toString());
    }

    // Main socketClient calls this method when receiving a message
    public void receiveMessage (JSONObject messageObj) {
        System.out.println("Receive WebSocket: " + messageObj.toString());
        String type = messageObj.getString("type");

        // Update clients choiceBox list
        if (type.equals("clients")) {

            JSONArray JSONlist = messageObj.getJSONArray("list");
            ArrayList<String> list = new ArrayList<>();
            String id = messageObj.getString("id");

            for (int i = 0; i < JSONlist.length(); i++) {
                String value = JSONlist.getString(i);
                if (!value.endsWith(id)) {
                    list.add(JSONlist.getString(i));
                }
            }

            txtId.setText(id);
            if (list.size() > 0) {
                choiceUser.getItems().clear();
                choiceUser.getItems().addAll(list);
                choiceUser.setValue(list.get(0));
            }
            
        } else if (type.equals("bounce")) {
            
            txtArea.appendText("\n\nBounce: " + messageObj.getString("message"));

        } else if (type.equals("broadcast")) {
            
            txtArea.appendText("\n\nBroadcast: " + messageObj.getString("message"));
            txtArea.appendText("\n(from: " + messageObj.getString("origin") + ")");

        } else if (type.equals("private")) {
            
            txtArea.appendText("\n\nPrivate: " + messageObj.getString("message"));
            txtArea.appendText("\n(from: " + messageObj.getString("origin") + ")");
        }
    }
}