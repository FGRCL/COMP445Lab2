package tcpsockets;

public class Stopwatch {
	private long startMillis;
	private boolean started;
	
	public Stopwatch() {
		startMillis = 0;
		started = false;
	}
	
	public void start() {
		startMillis = System.currentTimeMillis();
		started = true;
	}
	
	public long getTime() {
		if(!started) {
			throw new TimerNotStartedException();
		}
		return System.currentTimeMillis()-startMillis;
	}
	
	public void reset() {
		startMillis = System.currentTimeMillis();
		
	}
	
	private class TimerNotStartedException extends RuntimeException {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public TimerNotStartedException() {
			super("Time hasn't been started");
		}
	}
}
