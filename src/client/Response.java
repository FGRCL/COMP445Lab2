package client;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Response {
    public static String create(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();

        int data = inputStream.read();

        while(data != -1) {
            builder.append((char)data);
            data = inputStream.read();
        }
        
        return builder.toString();
    }
    
    public static void writeToFile(String filename, InputStream inputStream) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(filename);
        
        int data = inputStream.read();
        
        while(data != -1) {
            outputStream.write(data);
            data = inputStream.read();
        }
        
        outputStream.close();
    }
}
