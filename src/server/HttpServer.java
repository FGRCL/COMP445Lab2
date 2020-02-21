package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.InputMismatchException;

public class HttpServer {
    private int port;
    private HttpRequestObserver observer;

    public HttpServer(int port, HttpRequestObserver observer) {
        this.port = port;
        this.observer = observer;
    }

    public void start() {
        try {
            final ServerSocket server = new ServerSocket(port);
            System.out.println("Listening on port " + port);
            listen(server);
        } catch(IOException e) {
            System.err.println(e);
        }
    }

    private void listen(ServerSocket server) throws IOException {
        while(true) {
            final Socket client = server.accept();
            HttpResponse response = null;
            try {
                HttpRequest request = new HttpRequest(client.getInputStream());
                response = observer.onRequest(request);
            } catch(IOException e) {
                System.err.println(e.getMessage());
                response = new HttpResponse(HttpVersion.OnePointOh, Status.INTERNAL_SERVER_ERROR);
            } catch(InputMismatchException e) {
                System.out.println(e.getMessage());
                response = new HttpResponse(HttpVersion.OnePointOh, Status.BAD_REQUEST);
            }

            client.getOutputStream().write(response.toString().getBytes("UTF-8"));
            client.getOutputStream().close();
        }
    }
}