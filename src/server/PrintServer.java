package server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

class PrintServer {
    public void start() throws IOException {
        int port = 80;
        
        try(final ServerSocket server = new ServerSocket(port)) {
            
            System.out.println("Listening on port " + port);
    
            while(true) {
                final Socket client = server.accept();
                InputStreamReader input = new InputStreamReader(client.getInputStream());
                BufferedReader reader = new BufferedReader(input);
    
                String line = reader.readLine();
                while(!line.isEmpty()) {
                    System.out.println(line);
                    line = reader.readLine();
                }
    
                String httpResponse = "HTTP/1.0 200 OK\r\n\r\n" + new Date();
                client.getOutputStream().write(httpResponse.getBytes("UTF-8"));
                client.getOutputStream().close();
            }
        } catch(IOException e) {
            throw e;
        }
        
    }
}