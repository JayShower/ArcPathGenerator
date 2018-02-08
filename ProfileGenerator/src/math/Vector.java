package math;

public final class Vector {

	public static final Vector ZERO = new Vector(0, 0);

	public final double x, y;

	private double magnitude = -1;

	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector(double angle) {
		this.x = Math.cos(angle);
		this.y = Math.sin(angle);
	}

	public Vector(Vector start, Vector end) {
		this(end.x - start.x, end.y - start.y);
	}

	public double dotProduct(Vector other) {
		return x * other.x + y * other.y;
	}

	public double magnitudeSquared() {
		return dotProduct(this);
	}

	public double magnitude() {
		if (magnitude == -1) {
			magnitude = Math.sqrt(magnitudeSquared());
		}
		return magnitude;
	}

	public double cosine() {
		return x / magnitude();
	}

	public double slope() {
		return y / x;
	}

	public double sine() {
		return y / magnitude();
	}

	public Vector normalized() {
		return new Vector(x / magnitude(), y / magnitude());
	}

	public Vector scale(double scale) {
		return new Vector(x * scale, y * scale);
	}

	public Vector negate() {
		return new Vector(-x, -y);
	}

	public Vector add(Vector other) {
		return new Vector(x + other.x, y + other.y);
	}

	public Vector subtract(Vector other) {
		return new Vector(x - other.x, y - other.y);
	}

	public Vector invert() {
		return new Vector(1 / x, 1 / y);
	}

	public Vector multiply(Vector other) {
		return new Vector(x * other.x, y * other.y);
	}

	public Vector divide(Vector other) {
		return new Vector(x / other.x, y / other.y);
	}

	/**
	 * Rotates vector in positive theta direction ("counterclockwise")
	 * 
	 * @param initial
	 *            initial vector
	 * @param angle
	 *            angle to rotate in radians
	 * @return rotated vector
	 */
	public Vector rotate(double angle) {
		double newX = x * Math.cos(angle) - y * Math.sin(angle);
		double newY = x * Math.sin(angle) + y * Math.cos(angle);
		return new Vector(newX, newY);
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Vector))
			return false;
		Vector v = (Vector) other;
		return positionIsEqual(v);
	}

	@Override
	public String toString() {
		return String.format("%.2f, %.2f", x, y);
	}

	public static final double headingE = 1.0e-12;

	public boolean headingIsEqual(Vector other) {
		return Util.epsilonEquals(sine(), other.sine(), headingE)
				&& Util.epsilonEquals(cosine(), other.cosine(), headingE);
	}

	public static final double positionE = 1.0e-12;

	public boolean positionIsEqual(Vector other) {
		return Util.epsilonEquals(x, other.x, positionE) && Util.epsilonEquals(y, other.y, positionE);
	}

}
