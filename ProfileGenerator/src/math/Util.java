package math;

import java.util.function.Function;

public class Util {

	private static final double[][] gaussQuad64 = { { 0.0486909570091397, -0.0243502926634244 },
			{ 0.0486909570091397, 0.0243502926634244 }, { 0.0485754674415034, -0.072993121787799 },
			{ 0.0485754674415034, 0.072993121787799 }, { 0.048344762234803, -0.121462819296121 },
			{ 0.048344762234803, 0.121462819296121 }, { 0.0479993885964583, -0.169644420423993 },
			{ 0.0479993885964583, 0.169644420423993 }, { 0.0475401657148303, -0.217423643740007 },
			{ 0.0475401657148303, 0.217423643740007 }, { 0.04696818281621, -0.264687162208767 },
			{ 0.04696818281621, 0.264687162208767 }, { 0.0462847965813144, -0.311322871990211 },
			{ 0.0462847965813144, 0.311322871990211 }, { 0.0454916279274181, -0.357220158337668 },
			{ 0.0454916279274181, 0.357220158337668 }, { 0.0445905581637566, -0.402270157963992 },
			{ 0.0445905581637566, 0.402270157963992 }, { 0.0435837245293235, -0.446366017253464 },
			{ 0.0435837245293235, 0.446366017253464 }, { 0.0424735151236536, -0.489403145707053 },
			{ 0.0424735151236536, 0.489403145707053 }, { 0.0412625632426235, -0.531279464019895 },
			{ 0.0412625632426235, 0.531279464019895 }, { 0.0399537411327203, -0.571895646202634 },
			{ 0.0399537411327203, 0.571895646202634 }, { 0.0385501531786156, -0.611155355172393 },
			{ 0.0385501531786156, 0.611155355172393 }, { 0.03705512854024, -0.648965471254657 },
			{ 0.03705512854024, 0.648965471254657 }, { 0.0354722132568824, -0.685236313054233 },
			{ 0.0354722132568824, 0.685236313054233 }, { 0.0338051618371416, -0.719881850171611 },
			{ 0.0338051618371416, 0.719881850171611 }, { 0.0320579283548516, -0.752819907260532 },
			{ 0.0320579283548516, 0.752819907260532 }, { 0.0302346570724025, -0.783972358943341 },
			{ 0.0302346570724025, 0.783972358943341 }, { 0.0283396726142595, -0.813265315122797 },
			{ 0.0283396726142595, 0.813265315122797 }, { 0.0263774697150547, -0.84062929625258 },
			{ 0.0263774697150547, 0.84062929625258 }, { 0.0243527025687109, -0.865999398154093 },
			{ 0.0243527025687109, 0.865999398154093 }, { 0.0222701738083833, -0.889315445995114 },
			{ 0.0222701738083833, 0.889315445995114 }, { 0.0201348231535302, -0.910522137078503 },
			{ 0.0201348231535302, 0.910522137078503 }, { 0.0179517157756973, -0.92956917213194 },
			{ 0.0179517157756973, 0.92956917213194 }, { 0.0157260304760247, -0.946411374858403 },
			{ 0.0157260304760247, 0.946411374858403 }, { 0.0134630478967186, -0.961008799652054 },
			{ 0.0134630478967186, 0.961008799652054 }, { 0.0111681394601311, -0.973326827789911 },
			{ 0.0111681394601311, 0.973326827789911 }, { 0.0088467598263639, -0.983336253884626 },
			{ 0.0088467598263639, 0.983336253884626 }, { 0.0065044579689784, -0.991013371476744 },
			{ 0.0065044579689784, 0.991013371476744 }, { 0.0041470332605625, -0.996340116771955 },
			{ 0.0041470332605625, 0.996340116771955 }, { 0.0017832807216964, -0.999305041735772 },
			{ 0.0017832807216964, 0.999305041735772 } };

	private static final double[][] gaussQuad8 = { { 0.3626837833783620, -0.1834346424956498 },
			{ 0.3626837833783620, 0.1834346424956498 }, { 0.3137066458778873, -0.5255324099163290 },
			{ 0.3137066458778873, 0.5255324099163290 }, { 0.2223810344533745, -0.7966664774136267 },
			{ 0.2223810344533745, 0.7966664774136267 }, { 0.1012285362903763, -0.9602898564975363 },
			{ 0.1012285362903763, 0.9602898564975363 } };

	/**
	 * Integrates function from 0 to z
	 * 
	 * @param function
	 *            function to integrate
	 * @param lower
	 *            lower limit of integration
	 * @param upper
	 *            upper limit of integration
	 * @return the approximate integral
	 */
	public static double gaussQuadIntegrate64(Function<Double, Double> function, double lower, double upper) {
		if (upper - lower == 0)
			return 0;
		double sum = 0;
		for (int i = 0; i < gaussQuad64.length; i++) {
			double input = (upper - lower) * gaussQuad64[i][1] / 2 + (upper + lower) / 2;
			sum += gaussQuad64[i][0] * function.apply(input);
		}
		return sum * (upper - lower) / 2;
	}

	public static double gaussQuadIntegrate8(Function<Double, Double> function, double lower, double upper) {
		if (upper - lower == 0)
			return 0;
		double sum = 0;
		for (int i = 0; i < gaussQuad8.length; i++) {
			double input = (upper - lower) * gaussQuad8[i][1] / 2 + (upper + lower) / 2;
			sum += gaussQuad8[i][0] * function.apply(input);
		}
		return sum * (upper - lower) / 2;
	}

	public static double limit(double d, double min, double max) {
		return d < min ? min : d > max ? max : d;
	}

	public static boolean epsilonEquals(double v1, double v2, double epsilon) {
		return Math.abs(v1 - v2) < epsilon;
	}

	public static boolean epsilonEquals(double v1, double v2) {
		return epsilonEquals(v1, v2, 1.0e-12);
	}

	public static double linearInterpolate(double x0, double y0, double x1, double y1, double x) {
		double slope = (y1 - y0) / (x1 - x0);
		return slope * (x - x0) + y0;
	}

	public static double curvature2d(double x1, double x2, double y1, double y2) {
		return (x1 * y2 - y1 * x2) / Math.pow(x1 * x1 + y1 * y1, 1.5);
	}

	public static boolean isIncreasing(double[] array) {
		double start = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] < start)
				return false;
		}
		return true;
	}

	public static boolean isDecreasing(double[] array) {
		double start = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] > start)
				return false;
		}
		return true;
	}

	public static double lerp(double low, double high, double mu) {
		return (high - low) * mu + low;
	}

	public static double cerp(double y1, double y2, double mu) {
		double mu2 = (1 - Math.cos(mu * Math.PI)) / 2;
		return (y1 * (1 - mu2)) + (y2 * mu2);
	}
	
	public static void logForGraphing(Object... obs) {
		for(Object o : obs) {
			System.out.print(o + ", ");
		}
		System.out.println();
	}

}
