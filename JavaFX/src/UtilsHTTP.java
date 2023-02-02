import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javafx.concurrent.Task;

public class UtilsHTTP {

    public static void sendGET(String url, Consumer<String> callBack) {
        send("GET", url, "", callBack);
	}

    public static void sendPOST(String url, String post_params, Consumer<String> callBack) {
        send("POST", url, post_params, callBack);
	}

    private static void send(String type, String url, String post_params, Consumer<String> callBack) {

        // Create a new thread to send the request
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Task<String> task = new Task<>() {
            @Override 
            protected String call() {
                try {
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod(type);
                    con.setRequestProperty("User-Agent", "Mozilla/5.0");
            
                    if (type.equals("POST")) {
                        con.setDoOutput(true);
                        OutputStream os = con.getOutputStream();
                        os.write(post_params.getBytes());
                        os.flush();
                        os.close();
                    }
            
                    int responseCode = con.getResponseCode();
            
                    if (responseCode == HttpURLConnection.HTTP_OK) { //success
                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();
            
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        return response.toString();
                    } else {
                        System.out.println(type + " request did not work.");
                    }
                } catch (Exception e) {
                    System.out.println(type + " request error.");
                }
                return "{ \"status\": \"KO\", \"result\": \"Error on " + type + " request\" }";
            }
        };

        task.setOnSucceeded(event -> {
            callBack.accept(task.getValue());
            executorService.shutdownNow();
        });
        
        executorService.execute(task); 
	}
}
