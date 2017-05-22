
public class Package {
	private int packageID;
	private String packageName;
	
	public Package(int packageID, String packageName) {
		this.packageID = packageID;
		this.packageName = packageName;		
	}
	
	public int getPackageID() {
		return this.packageID;
	}
	
	public String getPackageName() {
		return this.packageName;
	}
	
}