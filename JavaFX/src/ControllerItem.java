import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;

public class ControllerItem {
    
    @FXML
    private Label title, subtitle;

    @FXML
    private Rectangle coloredShape;

    @FXML
    private void handleMenuAction() {
        Controller0 c0 = (Controller0) UtilsViews.getController("View0");
        c0.loadConsoleInfo(title.getText());
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setSubtitle(String subtitle) {
        this.subtitle.setText(subtitle);
    }

    public void setColor(String color) {
        coloredShape.setStyle("-fx-fill: " + color);
    }
}
