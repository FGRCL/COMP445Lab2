package server.library;

import java.io.IOException;
import java.net.Socket;
import java.util.InputMismatchException;

public class HttpServerThread extends Thread{
	private Socket client;
	private HttpRequestObserver observer;
	
	public HttpServerThread(Socket client, HttpRequestObserver observer) {
		this.client = client;
		this.observer = observer;
	}
	
	@Override
	public void run(){
		 HttpResponse response = null;
         try {
             HttpRequest request = new HttpRequest(client.getInputStream());
             response = observer.onRequest(request);
             client.getOutputStream().write(response.toString().getBytes("UTF-8"));
             client.getOutputStream().close();
         } catch(IOException e) {
             System.err.println(e.getMessage());
             response = new HttpResponse(HttpVersion.OnePointOh, Status.INTERNAL_SERVER_ERROR);
         } catch(InputMismatchException e) {
             System.out.println(e.getMessage());
             response = new HttpResponse(HttpVersion.OnePointOh, Status.BAD_REQUEST);
         }
	}
}
