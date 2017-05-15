import java.util.ArrayList;

public class Database {
	
	private ArrayList<Shelf> list = new ArrayList<Shelf>(); //The list 
	
	/**
	 * Returns the full database ArrayList
	 * @return  The full database ArrayList.
	 */
	public ArrayList<Shelf> getListOfShelves() {
		return this.list;
	}
	
	/**
	 * Stores a Shelf object in the database where the coordinates match. 
	 * @param  newShelf  The Shelf to be stored in the database
	 */
	public void storePackage(Shelf newShelf) {
		for(int i = 0; i < list.size(); i++) {
			if(Coordinates.compareCoordinates(newShelf.getCoordinates(), list.get(i).getCoordinates())) {
				list.set(i, newShelf);
			}
		}
	}
	
	/**
	 * Finds the first Shelf object in the database which has no Package object and returns
	 * the coordinates of that Shelf object. 
	 * @return	The coordinates of the first empty slot.
	 */
	public Coordinates findEmptySlot() {
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).getPackage() == null) {
				return list.get(i).getCoordinates();
			}
		}
		return null;
	}
	
	/**
	 * Finds the first Shelf object in the database which contains a Package object with 
	 * the name defined by packageName and returns the coordinates of that Shelf.
	 * @param	packageName	The name of the package to be found in the database
	 * @return	The coordinates in the Shelf object.
	 */
	public Coordinates getPackageLocation(String packageName) {
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).getPackage().getPackageName() == packageName) {
				return list.get(i).getCoordinates();
			}
		}
		return null;
	}
	
	/**
	 * Finds the first Shelf object in the database which contains a Package object with 
	 * the ID defined by packageID and returns the coordinates of that Shelf.
	 * @param	packageID	The ID of the package to be found in the database
	 * @return	The coordinates in the Shelf object.
	 */
	public Coordinates getPackageLocation(int packageID) {
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).getPackage().getPackageID() == packageID) {
				return list.get(i).getCoordinates();
			}
		}
		return null;
	}
	
	
	/**
	 * Populates the database with a set of empty shelves.
	 * @param	rows			The number of rows in the warehouse.
	 * @param	shelvesPerRow	The number shelves per row in the warehouse.
	 * @param	shelfLevels		The number of levels per shelf in the warehouse.
	 */
	public void populateDatabase(int rows, int shelvesPerRow, int shelfLevels) {
		
		for(int i = 0; i < rows; i++) {
			for(int k = 0; k < shelvesPerRow; k++) {
				for(int j = 0; j < shelfLevels; j++) {
					Coordinates coord = new Coordinates(i,k,j);
					Shelf newShelf = new Shelf(null,coord);
					list.add(newShelf);
				}
			}
		}
		
		
	}
	
	/**
	 * Prints the current database on the form "Package - Coordinates"
	 */
	public void printDB() {
		for(int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i).getPackage() + " " + list.get(i).getCoordinates().x + 
					" " + list.get(i).getCoordinates().y + " " + list.get(i).getCoordinates().z);
		}
		
	}


}