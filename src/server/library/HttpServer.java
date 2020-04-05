package server.library;
import java.io.IOException;

import tcpsockets.TCPServerSocket;
import tcpsockets.TCPSocket;

public class HttpServer {
    private int port;
    private HttpRequestObserver observer;
    private boolean open;
    TCPServerSocket server;

    public HttpServer(int port, HttpRequestObserver observer) {
        this.port = port;
        this.observer = observer;
        this.open = true;
    }

    public void start() throws IOException {
        server = new TCPServerSocket(port);
        System.out.println("Listening on port " + port);
        listen(server);
    }

    private void listen(TCPServerSocket server) throws IOException {
        while(open) {
            TCPSocket client = server.accept();
            new HttpServerThread(client, observer).start();
        }
        System.out.println("Server closed deliberately");
        server.close();
    }

    public void close() {
        open = false;
    }
}