package tests;

import org.junit.Test;

import tcpsockets.Stopwatch;

public class StopwatchTests {
	
	@Test
	public void shouldHaveInterval(){
		Stopwatch watch = new Stopwatch();
		watch.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long time = watch.getTime();
		assert(time >= 1000);
	}
	
	@Test
	public void shouldHaveMoreTime(){
		Stopwatch watch = new Stopwatch();
		watch.start();
		long time  = watch.getTime();
		assert(watch.getTime() > time);
	}
	
	@Test
	public void shouldBeSmallerAfterReset() {
		Stopwatch watch = new Stopwatch();
		watch.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		watch.reset();
		assert(watch.getTime() < 1000);
	}
}
