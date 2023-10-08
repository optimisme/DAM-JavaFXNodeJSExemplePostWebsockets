package com.project;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javafx.application.Platform;

public class UtilsHTTP {

    public static void sendGET(String url, Consumer<String> callBack) {
        send("GET", url, "", "", callBack);
	}

    public static void sendGET(String url, String cookie, Consumer<String> callBack) {
        send("GET", url, cookie, "", callBack);
	}

    public static void sendPOST(String url, String post_params, Consumer<String> callBack) {
        send("POST", url, "", post_params, callBack);
	}

    public static void sendPOST(String url, String cookie, String post_params, Consumer<String> callBack) {
        send("POST", url, cookie, post_params, callBack);
	}

    private static void send(String type, String url, String cookie, String post_params, Consumer<String> callBack) {

        // Create a new thread to send the request
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String errorStr = "{ \"status\": \"KO\", \"result\": \"Error on " + type + " request\" }";
                try {
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod(type);
                    con.setRequestProperty("User-Agent", "Mozilla/5.0");
                    if (!cookie.equals("")) {
                        con.setRequestProperty("Cookie", cookie);
                    }

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
                        callBackHandler(response.toString(), callBack);

                    } else {
                        System.out.println(type + " request did not work.");
                        callBackHandler(errorStr, callBack);
                    }
                } catch (Exception e) {
                    System.out.println(type + " request error.");
                    callBackHandler(errorStr, callBack);
                }
            }
        });
	}

    static private void callBackHandler(String result, Consumer<String> callBack) {

        // Code for Java
        // callBack.accept(result);

        // Code for JavaFX
        Platform.runLater(()->{
            callBack.accept(result);
        });

        // Code for Android
        /* 
        new Handler(Looper.getMainLooper()).post(new Runnable () {
            @Override
            public void run () {
                callBack.accept(result);
            }
        });
        */

        // Code for Swing
        /* 
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                callBack.accept(result);
            }
        });
        */
    }
}
