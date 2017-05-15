import java.io.*;
import java.util.Vector;

public class Server {
	public static void main(String [ ] args) throws IOException {
		
	
		//Vector vect = new Vector();
		commTest com = new commTest();
		com.searchForPorts();
		com.connect();
		com.initIOStream();
		com.initListener();
		//com.writeData();
		//com.disconnect();
	

		//Test code for the database
		/*Database db = new Database(); 
		db.populateDatabase(2, 6, 3);
		db.printDB();
		Package pack = new Package(12345,"newPackage");
		Coordinates coord = new Coordinates(1,2,2);
		Shelf newShelf = new Shelf(pack, coord);
		db.storePackage(newShelf);
		db.printDB();*/
	}
}