import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import json.JSONObject;
import json.JSONTokener;
import lejos.hardware.ev3.EV3;

public class Bluetooth implements Runnable {
	private Robot ev3;
	private BufferedReader inputStream; 
	private BufferedWriter outputStream; 
	
	public Bluetooth (Robot ev3) {
		this.ev3 = ev3;
	}
	
	public void run(){
		try {
			connectToServer(1111);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String instruction;
		while(true){
			try {
				instruction = inputStream.readLine();
				if (instruction != null){
					instruction = instruction.substring(2);
					System.out.println(instruction);
					ev3.instructions.add(new JSONObject(instruction));
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	public void connectToServer(int port) throws IOException {
		ServerSocket serv = new ServerSocket(port);
		Socket s = serv.accept(); //Wait for Laptop to connect
		this.inputStream = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
		this.outputStream = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		System.out.println("Socket connected");
	}
}
