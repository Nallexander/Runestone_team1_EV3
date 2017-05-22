import java.net.ServerSocket;
import java.net.Socket;

import lejos.utility.Delay;

import java.io.*;


public class SocketClient {
	public static void main(String[] args) throws IOException {
		System.out.println("Program started");
		ServerSocket serv = new ServerSocket(1111);
		Socket s = serv.accept(); //Wait for Laptop to connect
		DataInputStream in = new DataInputStream(s.getInputStream());
		DataOutputStream out = new DataOutputStream(s.getOutputStream());
		System.out.println("Socket connected");
	
		//Test msg from laptop
		try {
			System.out.println(in.readChar());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Did not receive message from server");
			e.printStackTrace();
		}
		// Wait in order to see the message on the display
		Delay.msDelay(10000);
	};
	
};