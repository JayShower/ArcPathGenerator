package math;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
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

	public static final int DEFAULT_SIZE = 500;
	private static final ExecutorService threadPool = Executors.newWorkStealingPool(); // Executors.newCachedThreadPool();
	private static final ExecutorService singleThread = Executors.newSingleThreadExecutor();

	private final BiFunction<Double, Double, Double> deltaY;
	private final double lowerInput;
	// private final double upperInput;
	private final double range;

	private int resolution;
	private double increment;
	private Future<?> doneChecker;
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
		inOut = new ConcurrentSkipListMap<>();
		inOut.put(lowerInput, 0.0);
		outIn = new ConcurrentSkipListMap<>();
		outIn.put(0.0, lowerInput);

		List<Future<Double>> puts = new ArrayList<>(resolution);
		puts.add(threadPool.submit(() -> 0.0));

		for (int i = 1; i < resolution; i++) {
			int index = i; // need to do this for lambda expression below
			double prevIn = indexToInput(index - 1);
			double currentIn = indexToInput(index);

			Future<Double> ds = threadPool.submit(() -> deltaY.apply(prevIn, currentIn));

			Future<Double> putTask = singleThread.submit(() -> {
				double change = ds.get();
				double prevResult = puts.get(index - 1).get();
				double result = prevResult + change;
				inOut.put(currentIn, result);
				outIn.put(result, currentIn);
				return change + prevResult;
			});
			puts.add(putTask);
		}
		doneChecker = puts.get(resolution - 1);
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
		Double gotVal = inOut.get(input);
		if (gotVal != null) {
			return gotVal;
		}

		Entry<Double, Double> lower = inOut.floorEntry(input);
		Entry<Double, Double> upper = inOut.ceilingEntry(input);

		if (lower == null && upper == null) {
			System.err.println("ERROR: BOTH SHOULDN'T BE NULL");
			return 0.0;
		} else if (lower == null) {
			return upper.getValue();

		} else if (upper == null) {
			return lower.getValue();
		}

		Function<Double, Double> f = d -> Util.linearInterpolate(lower.getKey(), lower.getValue(), upper.getKey(),
				upper.getValue(), d);

		return inOut.computeIfAbsent(input, f);
	}

	// basically does an interpolation search on the tree
	public double getInput(double output) {
		Double gotVal = outIn.get(output);
		if (gotVal != null) {
			return gotVal;
		}

		Entry<Double, Double> lower = outIn.floorEntry(output);
		Entry<Double, Double> upper = outIn.ceilingEntry(output);

		if (lower == null && upper == null) {
			System.err.println("ERROR: BOTH SHOULDN'T BE NULL");
			return 0.0;
		} else if (lower == null) {
			return upper.getValue();

		} else if (upper == null) {
			return lower.getValue();
		}

		Function<Double, Double> f = d -> Util.linearInterpolate(lower.getKey(), lower.getValue(), upper.getKey(),
				upper.getValue(), d);

		return outIn.computeIfAbsent(output, f);
	}

}
