/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author Felix
 */
public class Client {

    private final Options options;
    private final int MAX_REDIRECTS = 10;

    public Client(Options options) {
        this.options = options;
    }

    public String sendRequest() {
        SplitUrl url = new SplitUrl(options.url);

        try {
            Socket socket = new Socket(url.getDomain(), url.getPort());

            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            String request = Request.create(options);

            outputStream.write(request.getBytes());
            outputStream.flush();
            
            if(options.outFile != null) {
                Response.writeToFile(options.outFile, inputStream);
                return "Wrote output to " + options.outFile;
            }

            String response = Response.create(inputStream);
            response = handleRedirect(response);

            if (!options.verbose) {
                String delimiter = "\r\n\r\n";
                int i = response.indexOf(delimiter);
                response = response.substring(i + delimiter.length());
            }

            socket.close();

            return response;
        } catch (IOException e) {
            return e.getClass() + ":" + e.getMessage();
        }
    }

    private String handleRedirect(String response) {
        int redirects = 0;
        String locationHeader = "Location: ";

        while (redirects < MAX_REDIRECTS) {
            if (response.contains("HTTP/1.0 3") && response.contains(locationHeader)) {
                int locationStartIndex = response.indexOf(locationHeader) + locationHeader.length();
                int locationEndIndex = response.indexOf("\r\n", locationStartIndex);
                String newLocation = response.substring(locationStartIndex, locationEndIndex);
                this.options.setHostHeader(newLocation);
                response = this.sendRequest();
                redirects++;
                
            } else {
                return response;
            }
        }
        
        throw new RuntimeException("Maximum redirect limit reached");
    }
}
