import json.*;
import lejos.hardware.motor.Motor;
import lejos.utility.Delay;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.port.SensorPort;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.io.*;


public class Robot {
	final EV3ColorSensor sensor;
	public PriorityQueue<JSONObject> instructions; 

	
	public Robot() {
		sensor = new EV3ColorSensor(SensorPort.S1);
		instructions = new PriorityQueue<JSONObject>(50, new MyJSONComparator()); 
	}
	/**
	 * Connect the robot to the server using sockets. The server and the robot need to be in the same Bluetooth PAN network. 
	 * @param port Port number of server's socket. 
	 * @throws IOException
	 */
	
	
	public static void main(String[] args){
		
		Robot ev3 = new Robot();
		Thread t = new Thread(new Bluetooth(ev3));
		t.start();
		
		
		
		
		//ev3.instructions.add(ins2);
		//ev3.instructions.add(ins3);
		//ev3.instructions.add(ins1);
		//ev3.instructions.add(ins4); //TODO receive these ev3.instructions from bluetooth
		
		JSONObject currentInstruct = new JSONObject();
		
		while (true){
			if(!ev3.instructions.isEmpty()){
				currentInstruct = ev3.instructions.remove();
				switch (currentInstruct.getInt("type")) {
					case 0:  System.out.println("reset queue");
							 ev3.instructions.clear();
							 break;
				
					case 1:  System.out.println("keyboard type");
						 	 keyPress(currentInstruct.getJSONObject("content"));
						 	 break;
					
					case 2:  System.out.println("motor type");
					     	 move(currentInstruct.getJSONObject("content"), ev3.sensor);
							 break;
				
					default: System.out.println("Error: invalid type");
						     break;
				}
			}
			
		}
		//ev3.sensor.close();
	}
	
		
		
	private static void move(JSONObject infos, EV3ColorSensor sensor){
		switch (infos.getInt("motor")) {
			case 1: Motor.A.setSpeed(infos.getInt("speed"));//left motor
					Motor.B.setSpeed(infos.getInt("speed"));
					if(infos.getInt("direction") == 1){
						Motor.A.forward();
						Motor.B.backward();
					}else{
						Motor.A.backward();
						Motor.B.forward();

					}
					Delay.msDelay(1000); 
					Motor.A.stop(true);
					Motor.B.stop();
					break;

			case 2: Motor.B.setSpeed(infos.getInt("speed"));//right motor
					Motor.A.setSpeed(infos.getInt("speed"));
					if(infos.getInt("direction") == 1){
						Motor.B.forward();
						Motor.A.backward();
					}else{
						Motor.B.backward();
						Motor.A.forward();
					}
					Delay.msDelay(1000); 
					Motor.B.stop(true);
					Motor.A.stop();
					break;

			case 3: Motor.A.setSpeed(infos.getInt("speed"));//moving forward
					Motor.B.setSpeed(infos.getInt("speed"));
					if(infos.getInt("direction") == 1){
						Motor.A.forward();
						Motor.B.forward();
					}else{
						Motor.A.backward();
						Motor.B.backward();
					}
					Delay.msDelay(1000);
					detectIntersection(sensor);
					Motor.A.stop(true); 
					Motor.B.stop();
					break;
					
			case 4: Motor.C.rotateTo(infos.getInt("angle") * infos.getInt("direction"), false);//elevator
					break;

			default: System.out.println("error wrong motor command");
					break;
		}
	}

	private static void detectIntersection(EV3ColorSensor sensor){
		
		while(true){
			System.out.println(sensor.getColorID());
			Delay.msDelay(100);
			if (sensor.getColorID() == 0 || sensor.getColorID() == 6){
				break;
			}
			if (sensor.getColorID() == 1){
				Motor.A.setSpeed(Motor.A.getRotationSpeed() - 50);
			}
			if (sensor.getColorID() == 2){
				Motor.B.setSpeed(Motor.B.getRotationSpeed() - 50);
			}
		
		
		}
		
	}

	private static void keyPress(JSONObject infos){
		switch (infos.getInt("key")){
			case 1: //left motor
				Motor.A.setSpeed(360);
				if(infos.getInt("press") > 0){
					Motor.A.forward();
					Delay.msDelay(100);
				}else{
					Motor.A.stop();
				}
				break;
			case 2: //rigth motor
				Motor.B.setSpeed(360);
				if(infos.getInt("press") > 0){
					Motor.B.forward();
					Delay.msDelay(100);

				}else{
					Motor.B.stop();
				}
				break;
			case 3: //both forward
				Motor.A.setSpeed(360);
				Motor.B.setSpeed(360);
				if(infos.getInt("press") > 0){
					Motor.A.forward();
					Motor.B.forward();
					Delay.msDelay(100);

				}else{
					Motor.A.stop(true);
					Motor.B.stop();
				}
				break;
			case 4: //both backward
				Motor.A.setSpeed(360);
				Motor.B.setSpeed(360);
				if(infos.getInt("press") > 0){
					Motor.A.backward();
					Motor.B.backward();
					Delay.msDelay(100);
				}else{
					Motor.A.stop(true);
					Motor.B.stop();
				}
				break;
			case 5: //elevator up
				Motor.C.setSpeed(100);
				if(infos.getInt("press") > 0){
					Motor.C.forward();
				}else{
					Motor.C.stop();
				}
				break;
			case 6: //elevator down
				Motor.C.setSpeed(100);
				if(infos.getInt("press") > 0){
					Motor.C.backward();
				}else{
					Motor.C.stop();
				}
				break;
		}
	}
}
class MyJSONComparator implements Comparator<JSONObject> {

		public int compare(JSONObject o1, JSONObject o2) {
		    int int1 = (o1.getInt("type"));
		    int int2 = (o2.getInt("type"));
		    return (int1 - int2);
		}
	}