package math;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.BiFunction;

public class IntegralLookupTable {

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
		this(function, lowerInput, upperInput, 100);
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
		inOut = new ConcurrentSkipListMap<>();
		outIn = new ConcurrentSkipListMap<>();
		inOut.put(0.0, 0.0);
		outIn.put(0.0, 0.0);
		for (int i = 1; i < resolution; i++) {
			double in = indexToInput(i);
			double value = function.apply(indexToInput(i - 1), indexToInput(i));
			inOut.put(indexToInput(i), inOut.get(indexToInput(i - 1)) + value);
			outIn.put(inOut.get(in), in);
		}
		if (!isIncreasing()) {
			System.err.println("Lookup table is not increasing!");
		}

	}

	private int inputToIndex(double input) {
		return (int) Math.floor((input - lowerInput) / increment);
	}

	public double getOutput(double input) {
		Entry<Double, Double> lower = inOut.floorEntry(input);
		Entry<Double, Double> upper = inOut.ceilingEntry(input);
		Double val = inOut.get(input);
		if (val != null) {
			return val;
		}
		return inOut.computeIfAbsent(input,
				d -> Util.linearInterpolate(lower.getKey(), lower.getValue(), upper.getKey(), upper.getValue(), d));
	}

	public double getInput(double output) {
		Entry<Double, Double> lower = outIn.floorEntry(output);
		Entry<Double, Double> upper = outIn.ceilingEntry(output);
		Double val = inOut.get(output);
		if (val != null) {
			return val;
		}
		return inOut.computeIfAbsent(output,
				d -> Util.linearInterpolate(lower.getKey(), lower.getValue(), upper.getKey(), upper.getValue(), d));
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
