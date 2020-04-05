package server.library;

import java.io.IOException;
import java.util.InputMismatchException;

import tcpsockets.TCPSocket;

public class HttpServerThread extends Thread{
	private TCPSocket client;
	private HttpRequestObserver observer;
	
	public HttpServerThread(TCPSocket client, HttpRequestObserver observer) {
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
             client.close();
         } catch(IOException e) {
             System.err.println(e.getMessage());
             response = new HttpResponse(HttpVersion.OnePointOh, Status.INTERNAL_SERVER_ERROR);
         } catch(InputMismatchException e) {
             System.out.println(e.getMessage());
             response = new HttpResponse(HttpVersion.OnePointOh, Status.BAD_REQUEST);
         }
	}
}
