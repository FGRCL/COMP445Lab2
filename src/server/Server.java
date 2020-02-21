package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable{
	
	@Override
	public void run() {
		ServerSocket socket;
		try {
			socket = new ServerSocket(8080);
			while(true) {
				Socket clientSocket = socket.accept();
			    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			    
			    int character = in.read();
				while(character != -1){
					character = in.read();
					System.out.println((char) character);
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
