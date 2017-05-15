import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.*;



public class SocketServer {
	public static void main(String[] args) throws UnknownHostException, IOException {
		System.out.println("Server started");
		Socket s = new Socket("10.0.1.1", 1111);
		DataOutputStream out = new DataOutputStream(s.getOutputStream());
		out.writeUTF("Hello EV3!");
		System.out.println("Wrote to EV3 1");
		
		System.out.println("Control the robot by using the WASD-keys");
		
		char inputChar = 0;
		
		try {
			while(inputChar != 'e') {
				inputChar = (char) System.in.read();
				out.writeChar(inputChar);
			}
		}
		catch (IOException e){
			System.out.println("Error");		
		}
		
		s.close();
		
	}
	
}
