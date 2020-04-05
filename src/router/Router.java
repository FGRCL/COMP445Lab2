package router;

import java.io.IOException;
import java.util.Locale;

public class Router{
	private static Process routerProcess;
	public static void start(int port, float dropRate, String maxDelay, int seed) {
		try {
            String command = String.format(Locale.CANADA, "router_x64.exe --port=%d --drop-rate=%.2f --max-delay=%s --seed=%d", port, dropRate, maxDelay, seed);
            routerProcess = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void stop() {
		routerProcess.destroy();
	}
}
