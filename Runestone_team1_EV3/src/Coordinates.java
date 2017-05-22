
public class Coordinates {
	
	public int x,y,z;
	
	public Coordinates(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/** 
	 * Compares two sets of coordinates to see if they are equal to each other.
	 * @param  coord1	Coordinates to be compared with coord2
	 * @param  coord2	Coordinates to be compared with coord1
	 * @return  True if the coordinates match, false otherwise.
	 */
	public static boolean compareCoordinates(Coordinates coord1, Coordinates coord2) {
		return coord1.x == coord2.x && coord1.y == coord2.y && coord1.z == coord2.z;
	}
}