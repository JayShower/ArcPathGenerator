package math;

import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Function;

/*
 * Warning: although this class uses multithreading, setResolution SHOULD NOT
 * be called from a thread different than the one using it, because then it could
 * be in the middle of creating the table when trying to access it
 */

public class LookupTable {

	public static final int DEFAULT_SIZE = 1000;
	private static final ExecutorService threadPool = Executors.newWorkStealingPool(); // Executors.newCachedThreadPool();
	private static final ExecutorService singleThread = Executors.newSingleThreadExecutor();

	private final BiFunction<Double, Double, Double> deltaY;
	private final double lowerInput;
	// private final double upperInput;
	private final double range;

	private int resolution;
	private double increment;
	private Future doneChecker;
	private NavigableMap<Double, Double> inOut;
	private NavigableMap<Double, Double> outIn;

	public LookupTable(BiFunction<Double, Double, Double> deltaY, double lowerInput, double upperInput) {
		this(deltaY, lowerInput, upperInput, DEFAULT_SIZE);
	}

	public LookupTable(BiFunction<Double, Double, Double> function, double lowerInput, double upperInput,
			int resolution) {
		super();
		this.deltaY = function;
		this.lowerInput = lowerInput;
		// this.upperInput = upperInput;
		this.range = upperInput - lowerInput;
		setResolution(resolution);
	}

	public void setResolution(int resolution) {
		this.resolution = resolution;
		this.increment = range / ((double) resolution - 1);
		makeLUT();
	}

	private double indexToInput(int i) {
		return lowerInput + i * increment;
	}

	private void makeLUT() {
		Queue<Future<Double>> tasks = new LinkedList<>();
		tasks.add(threadPool.submit(() -> 0.0));
		for (int i = 1; i < resolution; i++) {
			double low = indexToInput(i - 1);
			double up = indexToInput(i);
			tasks.add(threadPool.submit(() -> deltaY.apply(low, up)));
		}
		Queue<Future<Double>> puts = new LinkedList<>();
		inOut = new ConcurrentSkipListMap<>();
		outIn = new ConcurrentSkipListMap<>();
		double prevOut = 0.0;
		int i = 0;
		while (!tasks.isEmpty()) {
			double in = indexToInput(i);
			double out = 0;
			try {
				out = tasks.poll().get(); // blocking
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			double result = prevOut + out;
			puts.add(threadPool.submit(() -> inOut.put(in, result)));
			puts.add(threadPool.submit(() -> outIn.put(result, in)));
			prevOut = result;
			i++;
		}

		// puts list SHOULD have all the inOut/outIn tasks by now
		doneChecker = singleThread.submit(() -> {
			while (!puts.isEmpty()) { // remove all done, check again
				puts.removeIf((f) -> f.isDone());
			}
		});

	}

	public void waitToBeDone() {
		try {
			doneChecker.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	public boolean checkIfDone() {
		return doneChecker.isDone();
	}

	// private int inputToIndex(double input) {
	// return (int) Math.floor((input - lowerInput) / increment);
	// }

	public double getOutput(double input) {
		Double val = inOut.get(input);
		if (val != null) {
			return val;
		}
		Entry<Double, Double> lower = inOut.floorEntry(input);
		Entry<Double, Double> upper = inOut.ceilingEntry(input);
		if (lower == null)
			return upper.getValue();
		if (upper == null)
			return lower.getValue();
		Function<Double, Double> f = d -> Util.linearInterpolate(lower.getKey(), lower.getValue(),
				upper.getKey(),
				upper.getValue(), d);
		return inOut.computeIfAbsent(input, f);
	}

	public double getInput(double output) {
		Double val = outIn.get(output);
		if (val != null) {
			return val;
		}
		Entry<Double, Double> lower = outIn.floorEntry(output);
		Entry<Double, Double> upper = outIn.ceilingEntry(output);
		if (lower == null)
			return upper.getValue();
		if (upper == null)
			return lower.getValue();
		Function<Double, Double> f = d -> Util.linearInterpolate(lower.getKey(), lower.getValue(),
				upper.getKey(),
				upper.getValue(), d);
		return outIn.computeIfAbsent(output,
				f);
	}

	// private boolean isIncreasing() {
	// List<Double> list = new ArrayList<Double>(inOut.values());
	// for (int i = 1; i < list.size(); i++) {
	// if (list.get(i) < list.get(i - 1))
	// return false;
	// }
	// return true;
	// }

	public void printTable() {
		for (int i = 0; i < resolution; i++) {
			System.out.println(i + ": " + inOut.get(indexToInput(i)));
		}
	}

}
