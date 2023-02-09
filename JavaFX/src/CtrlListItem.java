import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.shape.Polygon;

public class CtrlListItem {
    
    @FXML
    private Label title, subtitle;

    @FXML
    private Polygon coloredShape;

    @FXML
    private void handleMenuAction() {
        CtrlPost c0 = (CtrlPost) UtilsViews.getController("ViewPost");
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
