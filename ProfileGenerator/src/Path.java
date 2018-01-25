import java.util.ArrayList;
import java.util.List;

public class Path {

	public final List<TrajectoryPoint> centerPath;

	public final ArrayList<TrajectoryPoint> leftPath;
	public final ArrayList<TrajectoryPoint> rightPath;

	public Path(List<TrajectoryPoint> centerPath) {
		this.centerPath = centerPath;
		leftPath = null;
		rightPath = null;
	}

	public Path(List<TrajectoryPoint> centerPath, double robotWidth) {
		this.centerPath = centerPath;
		leftPath = shiftPath(centerPath, -robotWidth / 2);
		rightPath = shiftPath(centerPath, robotWidth / 2);
	}

	private ArrayList<TrajectoryPoint> shiftPath(List<TrajectoryPoint> center, double normalDistance) {
		return null;
	}

}