package router;

import java.io.IOException;

public class Router{
	private static Process routerProcess;
	public static void start() {
		try {
			routerProcess = Runtime.getRuntime().exec("./router/router_x64.exe");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void stop() {
		routerProcess.destroy();
	}
}
