package math;

public class Vector {

	public static final Vector ZERO = new Vector(0, 0);

	public final double x, y;

	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector(CartesianPoint end) {
		this(end.x - CartesianPoint.origin.x, end.y - CartesianPoint.origin.y);
	}

	public Vector(CartesianPoint start, CartesianPoint end) {
		this(end.x - start.x, end.y - start.y);
	}

	public double dotProduct(Vector other) {
		return x * other.x + y * other.y;
	}

	public double magnitudeSquared() {
		return dotProduct(this);
	}

	public double magnitude() {
		return Math.sqrt(magnitudeSquared());
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

	public Vector invert() {
		return new Vector(1 / x, 1 / y);
	}

	public Vector multiply(Vector other) {
		return new Vector(x*other.x, y * other.y;
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
		double newX = x * Math.cos(angle) - Math.sin(angle);
		double newY = x * Math.sin(angle) + Math.cos(angle);
		return new Vector(newX, newY);
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Vector))
			return false;
		Vector v = (Vector) other;
		return x == v.x && y == v.y;
	}

}
