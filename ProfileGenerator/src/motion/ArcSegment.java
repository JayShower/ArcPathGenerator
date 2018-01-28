package motion;

public class ArcSegment extends MotionSegment {

	public final double radius;

	public ArcSegment(MotionState start, MotionState end, double radius) {
		super(start, end);
		this.radius = radius;
	}

	// override the relevant methods below here

}
