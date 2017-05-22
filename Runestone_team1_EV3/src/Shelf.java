
public class Shelf {

	private Package pack;
	private Coordinates coord;
	
	public Shelf(Package pack, Coordinates coord) {
		this.pack = pack;
		this.coord = coord;
	}
	
	public Package getPackage() {
		return this.pack;	
	}
	
	public Coordinates getCoordinates() {
		return this.coord;
	}
	
}