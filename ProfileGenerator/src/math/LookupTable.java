package math;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public class LookupTable {

	public static final int DEFAULT_SIZE = 150;

	private final BiFunction<Double, Double, Double> deltaY;
	private final double lowerInput;
	// private final double upperInput;
	private final double range;

	private int resolution;
	private double increment;
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
		inOut = new TreeMap<>();
		inOut.put(lowerInput, 0.0);
		outIn = new TreeMap<>();
		outIn.put(0.0, lowerInput);
		List<Double> puts = new ArrayList<>(resolution);
		puts.add(0.0);

		for (int i = 1; i < resolution; i++) {
			int index = i; // need to do this for lambda expression below
			double prevIn = indexToInput(index - 1);
			double currentIn = indexToInput(index);
			double change = deltaY.apply(prevIn, currentIn);
			double prevResult = puts.get(index - 1);
			double result = prevResult + change;
			inOut.put(currentIn, result);
			outIn.put(result, currentIn);
			puts.add(result);
		}
	}

	// private int inputToIndex(double input) {
	// return (int) Math.floor((input - lowerInput) / increment);
	// }

	public double getOutput(double input) {
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

		double mu = (input - lower.getKey()) / (upper.getKey() - lower.getKey());
		Function<Double, Double> f = d -> Util.lerp(lower.getValue(), upper.getValue(), mu);

		return inOut.computeIfAbsent(input, f);
	}

	// basically does an interpolation search on the tree
	public double getInput(double output) {
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

		double mu = (output - lower.getKey()) / (upper.getKey() - lower.getKey());
		Function<Double, Double> f = d -> Util.lerp(lower.getValue(), upper.getValue(), mu);

		return outIn.computeIfAbsent(output, f);
	}

}
