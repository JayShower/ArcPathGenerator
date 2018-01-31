package math;

import java.util.function.Function;

public class LookupTable {

	private final Function<Double, Double> function;
	private final double lowerInput;
	private final double upperInput;
	private final double range;

	private int resolution;
	private double increment;
	private double[] table;

	public LookupTable(Function<Double, Double> function, double lowerInput, double upperInput) {
		this(function, lowerInput, upperInput, 500);
	}

	public LookupTable(Function<Double, Double> function, double lowerInput, double upperInput, int resolution) {
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

	private void makeLUT() {
		table = new double[resolution];
		// double[] newTable = new double[resolution];
		double input;
		double output;
		for (int i = 0; i < resolution; i++) {
			input = indexToInput(i);
			output = function.apply(input);
			// System.out.println(output);
			table[i] = output;
		}
		// table = newTable;
		if (!isIncreasing()) {
			System.err.println("Lookup table is not increasing!");
		}

	}

	private int inputToIndex(double input) {
		return (int) Math.round((input - lowerInput) / increment);
	}

	public double getOutput(double input) {
		if (input <= lowerInput)
			return table[0];
		int lowerIndex = inputToIndex(input);
		if (lowerIndex == resolution - 1)
			return table[resolution - 1];
		double lowerInput = indexToInput(lowerIndex);
		double lowerOutput = table[lowerIndex];
		int upperIndex = lowerIndex + 1;
		double upperInput = indexToInput(upperIndex);
		double upperOutput = table[upperIndex];
		double result = Util.linearInterpolate(lowerInput, lowerOutput, upperInput, upperOutput, input);
		/*
		 * System.out.
		 * printf("I %.6f: LX %d, LI %.6f, LO %.6f, UX %d, UI %.6f, UO %.6f, R %.6f%n",
		 * input, lowerIndex, lowerInput, lowerOutput, upperIndex, upperInput,
		 * upperOutput, result);
		 */
		return result;
	}

	public double getInput(double output) {
		int lowerIndex = binarySearch(output);
		if (lowerIndex == resolution - 1)
			return indexToInput(lowerIndex);
		double lowerInput = indexToInput(lowerIndex);
		double lowerOutput = table[lowerIndex];
		int upperIndex = lowerIndex + 1;
		double upperInput = indexToInput(upperIndex);
		double upperOutput = table[upperIndex];
		return Util.linearInterpolate(lowerOutput, lowerInput, upperOutput, upperInput, output);
	}

	private int linearSearch(double output) {
		if (output < table[0]) {
			return 0;
		}
		int i = 1;
		for (; i < resolution; i++) {
			if (output < table[i])
				return i - 1;
		}
		return i - 1;
	}

	private int binarySearch(double value) {
		if (value < table[0]) {
			return 0;
		}
		if (value > table[table.length - 1]) {
			return table.length - 1;
		}

		int lo = 0;
		int hi = table.length - 1;
		int mid;

		while (lo < hi) {
			mid = (hi + lo) / 2;
			if (value < table[mid]) {
				hi = mid - 1;
			} else if (value > table[mid] && value < table[mid + 1])
				return mid;
			else {
				lo = mid + 1;
			}
		}
		return lo;
	}

	private boolean isIncreasing() {
		for (int i = 1; i < table.length; i++) {
			if (table[i] < table[i - 1])
				return false;
		}
		return true;
	}

	public void printTable() {
		for (int i = 0; i < resolution; i++) {
			System.out.println(i + ": " + table[i]);
		}
	}

}
