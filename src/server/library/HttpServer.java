package server.library;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    private int port;
    private HttpRequestObserver observer;
    private boolean open;
    ServerSocket server;

    public HttpServer(int port, HttpRequestObserver observer) {
        this.port = port;
        this.observer = observer;
        this.open = true;
    }

    public void start() throws IOException {
        server = new ServerSocket(port);
        System.out.println("Listening on port " + port);
        listen(server);
    }

    private void listen(ServerSocket server) throws IOException {
        while(open) {
            final Socket client = server.accept();
            new HttpServerThread(client, observer).start();
        }
        System.out.println("Server closed deliberately");
        server.close();
    }

    public void close() {
        try {
        server.close();
        } catch(IOException e) {
            
        }
    }
}