package in.ac.iitb.cse.intentor;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiService {
    public String getApiResponse(String apiUrl,String response_code) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);

        // Send registration ID in the request body
        String requestBody = "registration_code=" + response_code;
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(requestBody.getBytes());
        outputStream.close();

        InputStream inputStream = connection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder response = new StringBuilder();
        String line;

        
        while ((line = bufferedReader.readLine()) != null) {
            response.append(line);
        }

        bufferedReader.close();
//        return "Response" + response + "\nResponse Code" + responseCode + "\nError" + responseMessage  ;
        return response.toString();
    }
}
