import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.*;
import java.awt.event.*;

import json.JSONObject;

import java.io.*;



public class SocketServer extends KeyAdapter {
	
	
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		JSONObject ins1 = new JSONObject("{ \"type\": \"1\" , "
				+ " \"content\" : { "
				     + " \"motor\" : \"4\" ,"
				     + " \"direction\" : \"1\" , "
				     + " \"speed\" : \"540\" , "
				     + " \"angle\" : \"180\" }"
				+ " }");
		JSONObject ins2 = new JSONObject("{ \"type\": \"1\" , "
				+ " \"content\" : { "
					+ " \"motor\" : \"3\" ,"
					+ " \"direction\" : \"1\" , "
					+ " \"speed\" : \"540\" , "
					+ " \"angle\" : \"-180\" }"
				+ " }");
		JSONObject ins3 = new JSONObject("{ \"type\": \"1\" , "
				+ " \"content\" : { "
					+ " \"motor\" : \"1\" ,"
					+ " \"direction\" : \"1\" , "
					+ " \"speed\" : \"540\" , "
					+ " \"angle\" : \"-180\" }"
				+ " }\n");
		JSONObject ins4 = new JSONObject("{ \"type\": \"1\" , "
				+ " \"content\" : { "
					+ " \"motor\" : \"3\" ,"
					+ " \"direction\" : \"-1\" , "
					+ " \"speed\" : \"720\" , "
					+ " \"angle\" : \"-180\" }"
				+ " }\n");
		
		
		System.out.println("Server started");
		Socket s = new Socket("10.0.1.1", 1111);
		DataOutputStream out = new DataOutputStream(s.getOutputStream());
		
		System.out.println("Wrote to EV3 1");
		System.out.println("Control the robot by using the WASD-keys");
		
		out.writeUTF("{ \"type\": \"2\" , "
				+ " \"content\" : { "
				+ " \"motor\" : \"3\" ,"
				+ " \"direction\" : \"1\" , "
				+ " \"speed\" : \"360\" , "
				+ " \"angle\" : \"-180\" }"
			+ " }\n");
		//Thread.sleep(2000);
		out.writeUTF("{ \"type\": \"2\" , "
				+ " \"content\" : { "
				+ " \"motor\" : \"1\" ,"
				+ " \"direction\" : \"1\" , "
				+ " \"speed\" : \"720\" , "
				+ " \"angle\" : \"-180\" }"
			+ " }\n");
		out.writeUTF("{ \"type\": \"2\" , "
				+ " \"content\" : { "
				+ " \"motor\" : \"3\" ,"
				+ " \"direction\" : \"1\" , "
				+ " \"speed\" : \"360\" , "
				+ " \"angle\" : \"-180\" }"
			+ " }\n");
		
		
		
		
		/*KeyEvent key;
		while(key.getKeyCode() != KeyEvent.VK_ESCAPE){
			if(key.getKeyCode() == KeyEvent.VK_UP){
				out.writeUTF("{ \"type\" : \"1\", \"content\" : { \"key\" : \"3\" }}");
			}
			if(key.getKeyCode() == KeyEvent.VK_DOWN){
				out.writeUTF("{ \"type\" : \"1\", \"content\" : { \"key\" : \"4\" }}");
			}
			if(key.getKeyCode() == KeyEvent.VK_LEFT){
				out.writeUTF("{ \"type\" : \"1\", \"content\" : { \"key\" : \"1\" }}");
			}
			if(key.getKeyCode() == KeyEvent.VK_RIGHT){
				out.writeUTF("{ \"type\" : \"1\", \"content\" : { \"key\" : \"2\" }}");
			}
			
		}*/
		
		
		char inputChar = 0;
		
		
		s.close();
		
	}
	
}