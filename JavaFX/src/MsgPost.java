public class MsgPost {
    String type = "consola";
    String name = "";

    MsgPost (String type, String name) {
        this.type = type;
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }
}
