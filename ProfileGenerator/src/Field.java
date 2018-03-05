

import java.awt.geom.Rectangle2D;

/*
 * Class for data about the field, like coordinates of stuff
 * Where everything SHOULD be
 * 
 * Origin is close left corner when standing at driverstation
 * positive Y direction is going outwards
 * positive X direction is towards right side
 * Put in default coordinates
 * 
 * All units in inches
 * 
 * Some assumptions coordinate system is based on:
 * 1. DriverWallToSideWall is correct. This being wrong at competition will shift the whole Y axis (doesn't matter if we don't drive close to the wall)
 * 2. Driverstation wall - make sure it isn't tilted
 */
public class Field {

	// private static final double DriverWallToSideWall = 29.69;

	public static final class Scale {
		public static final Rectangle2D LEFT_PLATE = new Rectangle2D.Double(72, 299.65, 36, 48);
		public static final Rectangle2D RIGHT_PLATE = new Rectangle2D.Double(216, 299.65, 36, 48);
		public static final Rectangle2D PLATFORM = new Rectangle2D.Double(97, 261.47, 130, 54);
		public static final Rectangle2D LEFT_NULL_ZONE = new Rectangle2D.Double(0, 288, 95, 72);
		public static final Rectangle2D RIGHT_NULL_ZONE = new Rectangle2D.Double(229, 288, 95, 72);
	}

	public static final class Switch {
		public static final Rectangle2D BOUNDARY = new Rectangle2D.Double(85.25, 140, 153.5, 56);
		public static final Rectangle2D LEFT_PLATE = new Rectangle2D.Double(85, 140, 44, 56);
		public static final Rectangle2D RIGHT_PLATE = new Rectangle2D.Double(195, 140, 44, 56);
		public static Rectangle2D[] CUBES = { new Rectangle2D.Double(85, 195, 13, 13),
				new Rectangle2D.Double(113, 195, 13, 13), new Rectangle2D.Double(141, 195, 13, 13),
				new Rectangle2D.Double(169, 195, 13, 13), new Rectangle2D.Double(197, 195, 13, 13),
				new Rectangle2D.Double(225, 195, 13, 13), };
	}

	public static final class Zones {
		public static final Rectangle2D POWER_CUBE_ZONE = new Rectangle2D.Double(140, 98, 45, 42);
		public static final Rectangle2D EXCHANGE_ZONE = new Rectangle2D.Double(102, 0, 48, 36);
		public static final Rectangle2D EXCHANGE_OPENING = new Rectangle2D.Double(122.125, 0, 21, 0);
	}
}