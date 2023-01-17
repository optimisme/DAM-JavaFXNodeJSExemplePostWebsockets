
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UtilsJSON {

    public static Object parse (String json, Class<?> cls) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, cls);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String stringify(Object obj) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(obj).replaceAll("\n", "");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
