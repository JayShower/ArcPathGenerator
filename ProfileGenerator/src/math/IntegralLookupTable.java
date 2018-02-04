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

public class IntegralLookupTable {

	private static final ExecutorService threadPool = Executors.newFixedThreadPool(1);

	private final BiFunction<Double, Double, Double> function;
	private final double lowerInput;
	private final double upperInput;
	private final double range;

	private int resolution;
	private double increment;
	// private volatile double[] table;
	private NavigableMap<Double, Double> inOut;
	private NavigableMap<Double, Double> outIn;

	public IntegralLookupTable(BiFunction<Double, Double, Double> function, double lowerInput, double upperInput) {
		this(function, lowerInput, upperInput, 500);
	}

	public IntegralLookupTable(BiFunction<Double, Double, Double> function, double lowerInput, double upperInput,
			int resolution) {
		super();
		this.function = function;
		this.lowerInput = lowerInput;
		this.upperInput = upperInput;
		this.range = upperInput - lowerInput;
		// System.out.println("Lower, upper input: ");
		// System.out.println(lowerInput);
		// System.out.println(upperInput);
		setResolution(resolution);
	}

	public void setResolution(int resolution) {
		this.resolution = resolution;
		this.increment = range / ((double) resolution - 1);
		// System.out.println("Resolution, increment: ");
		// System.out.println(resolution);
		// System.out.println(increment);
		makeLUT();
	}

	private double indexToInput(int i) {
		return lowerInput + i * increment;
	}

	// private ExecutorService threadPool = Executors.newCachedThreadPool();
	// private Future<Double>[] lutValues;
	// private int k = 1;
	//
	// private int getK() {
	// return k;
	// }

	private void makeLUT() {
		ArrayList<Future<Double>> tasks = new ArrayList<>(resolution);
		tasks.add(threadPool.submit(() -> 0.0));
		for (int i = 1; i < resolution; i++) {
			double low = indexToInput(i - 1);
			double up = indexToInput(i);
			tasks.add(threadPool.submit(() -> function.apply(low, up)));
		}
		inOut = new ConcurrentSkipListMap<>();
		outIn = new ConcurrentSkipListMap<>();
		double prevOut = 0.0;
		// Timer t = new Timer();
		for (int i = 0; i < tasks.size(); i++) {
			double in = indexToInput(i);
			double out = 0;
			try {
				// t.reset();
				out = tasks.get(i).get();
				// t.printElapsed("Get elapsed time: ");
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			prevOut = prevOut + out;
			inOut.put(in, prevOut);
			outIn.put(prevOut, in);
		}
		if (!isIncreasing()) {
			System.err.println("Lookup table is not increasing!");
		}

	}

	private int inputToIndex(double input) {
		return (int) Math.floor((input - lowerInput) / increment);
	}

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

	private boolean isIncreasing() {
		List<Double> list = new ArrayList<Double>(inOut.values());
		for (int i = 1; i < list.size(); i++) {
			if (list.get(i) < list.get(i - 1))
				return false;
		}
		return true;
	}

	public void printTable() {
		for (int i = 0; i < resolution; i++) {
			System.out.println(i + ": " + inOut.get(indexToInput(i)));
		}
	}

}
