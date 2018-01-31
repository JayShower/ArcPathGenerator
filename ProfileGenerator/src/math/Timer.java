package math;

public class Timer {

	private long startTime = System.nanoTime();

	public void reset() {
		startTime = System.nanoTime();
	}

	public double elapsed() {
		return (System.nanoTime() - startTime) / 1.0e6;
	}

	public long elapsedNano() {
		return (System.nanoTime() - startTime);
	}

	public void printElapsed(String message) {
		System.out.println(message + elapsed());
	}

}
