package jade.fov;

import jade.core.World;
import jade.util.datatype.Coordinate;
import jade.util.datatype.MutableCoordinate;

import java.util.Collection;
import java.util.HashSet;

/**
 * Raycasting algorithm from Brogue, modified from C to Java.
 * Special thanks to Brian Walker!
 */
public class ModifiedRayCaster extends ViewField{
	static int LOS_SLOPE_GRANULARITY = 32768;

	private Collection<Coordinate> fov = new HashSet<Coordinate>();

	// Returns a boolean grid indicating whether each square is in the field of view of (xLoc, yLoc).
	// forbiddenTerrain is the set of terrain flags that will block vision (but note that the blocking cell itself is
	// illuminated); forbiddenFlags is the set of map flags that will block vision.
	// If cautiousOnWalls is set, we will not illuminate blocking tiles unless the tile one space closer to the origin
	// is visible to the player; this is to prevent lights from illuminating a wall when the player is on the other
	// side of the wall.
	@Override
	protected Collection<Coordinate> calcViewField(World world, int x, int y, int r)
	{
		fov.clear();
		fov.add(new Coordinate(x,y));
		for (int i=1; i<=8; i++) {
			scanOctantFOV(world, x, y, i, r, 1, LOS_SLOPE_GRANULARITY * -1, 0,
					true);
		}
		return fov;
	}

	/*
	Octants:
	\7|8/
	6\|/1
	--@--
	5/|\2
	/4|3\
	 */
	MutableCoordinate betweenOctant1andN(int x, int y, int x0, int y0, int n) {
		int dx = x - x0, dy = y - y0;
		MutableCoordinate coord = new MutableCoordinate(x0,y0);
		switch (n) {
		case 1: 
			coord.translate(dx, dy);
			break;
		case 2:
			coord.translate(dx, -dy);
			break;
		case 5:
			coord.translate(-dx, -dy);
			break;
		case 6:
			coord.translate(-dx, dy);
			break;
		case 8:
			coord.translate(-dy, -dx);
			break;
		case 3:
			coord.translate(-dy, dx);
			break;
		case 7:
			coord.translate(dy, -dx);
			break;
		case 4:
			coord.translate(dy, dx);
			break;
		}
		return coord;
	}


	// This is a custom implementation of recursive shadowcasting.
	void scanOctantFOV(World world, int xLoc, int yLoc, int octant, float maxRadius,
			int columnsRightFromOrigin, int startSlope, int endSlope, boolean cautiousOnWalls) {
		MutableCoordinate coord;
		if (columnsRightFromOrigin >= maxRadius) return;

		int i, a, b, iStart, iEnd, x, y, x2, y2; // x and y are temporary variables on which we do the octant transform
		int newStartSlope, newEndSlope;
		boolean cellObstructed;

		newStartSlope = startSlope;

		a = ((LOS_SLOPE_GRANULARITY / -2 + 1) + startSlope * columnsRightFromOrigin) / LOS_SLOPE_GRANULARITY;
		b = ((LOS_SLOPE_GRANULARITY / -2 + 1) + endSlope * columnsRightFromOrigin) / LOS_SLOPE_GRANULARITY;

		iStart = Math.min(a, b);
		iEnd = Math.max(a, b);

		// restrict vision to a circle of radius maxRadius
		if ((columnsRightFromOrigin*columnsRightFromOrigin + iEnd*iEnd) >= maxRadius*maxRadius) {
			return;
		}
		if ((columnsRightFromOrigin*columnsRightFromOrigin + iStart*iStart) >= maxRadius*maxRadius) {
			iStart = (int) (-1 * Math.sqrt(maxRadius*maxRadius - columnsRightFromOrigin*columnsRightFromOrigin));
		}

		x = xLoc + columnsRightFromOrigin;
		y = yLoc + iStart;
		coord = betweenOctant1andN(x, y, xLoc, yLoc, octant);
		x = coord.x();y = coord.y();
		boolean currentlyLit = world.insideBounds(x, y) && world.passableAt(x, y);

		for (i = iStart; i <= iEnd; i++) {

			x = xLoc + columnsRightFromOrigin;
			y = yLoc + i;
			coord = betweenOctant1andN(x, y, xLoc, yLoc, octant);
			x = coord.x();y = coord.y();
			if (!world.insideBounds(x, y)) {
				// We're off the map -- here there be memory corruption.
				continue;
			}

			cellObstructed = !world.passableAt(x, y);

			// if we're cautious on walls and this is a wall:
			if (cautiousOnWalls && cellObstructed) {

				// (x2, y2) is the tile one space closer to the origin from the tile we're on:
				x2 = xLoc + columnsRightFromOrigin - 1;
				y2 = yLoc + i;
				if (i < 0) {
					y2++;
				} else if (i > 0) {
					y2--;
				}
				coord = betweenOctant1andN(x2, y2, xLoc, yLoc, octant);
				x2 = coord.x();y2 = coord.y();
				//FIXME tu by mal byt test ci prepusta svetlo?
				//if (pmap[x2][y2].flags & IN_FIELD_OF_VIEW) {
				if (world.passableAt(x2, y2)){
					// previous tile is visible, so illuminate
					fov.add(new Coordinate(x,y));
				}
			} else {
				// illuminate
				fov.add(new Coordinate(x,y));
			}

			if (!cellObstructed && !currentlyLit) { // next column slope starts here

				newStartSlope = (int) ((LOS_SLOPE_GRANULARITY * (i) - LOS_SLOPE_GRANULARITY / 2)
						/ (columnsRightFromOrigin + 0.5));

				currentlyLit = true;

			} else if (cellObstructed && currentlyLit) { // next column slope ends here

				newEndSlope = (int) ((LOS_SLOPE_GRANULARITY * (i) - LOS_SLOPE_GRANULARITY / 2)
						/ (columnsRightFromOrigin - 0.5));

				if (newStartSlope <= newEndSlope) {
					// run next column
					scanOctantFOV(world, xLoc, yLoc, octant, maxRadius, columnsRightFromOrigin + 1, newStartSlope, newEndSlope,
							cautiousOnWalls);
				}

				currentlyLit = false;
			}
		}

		if (currentlyLit) { // got to the bottom of the scan while lit

			newEndSlope = endSlope;

			if (newStartSlope <= newEndSlope) {
				// run next column
				scanOctantFOV(world, xLoc, yLoc, octant, maxRadius, columnsRightFromOrigin + 1, newStartSlope, newEndSlope,
						cautiousOnWalls);
			}
		}
	}
}

