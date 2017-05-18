import json.*;
import lejos.hardware.motor.Motor;
import lejos.utility.Delay;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.port.SensorPort;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.io.*;

/**
 * The robot class is the code run in the ev3 robot
 * It has 2 attributes, the sensor and the priorityQueue 
 *
 */

public class Robot {
	final EV3ColorSensor sensor;
	public PriorityQueue<JSONObject> instructions; 

	
	public Robot() {
		sensor = new EV3ColorSensor(SensorPort.S1);
		instructions = new PriorityQueue<JSONObject>(50, new MyJSONComparator()); 
	}
	
	/**
	 * Main method that read instructions from the priorityQueue one by one
	 * type 0 : reset the Queue
	 * type 1 : keyboard control (manual mode)
	 * type 2 : motor control (semi-autonomous mode)
	 * @param args
	 */
	
	public static void main(String[] args){
		
		Robot ev3 = new Robot();
		Thread t = new Thread(new Bluetooth(ev3));
		t.start();
		
			
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
	}
	
	/**
	 * move method in case of type 2
	 * it start the motor selected, with the speed the direction and the angle 	
	 * 
	 * @param infos JSON object that contain all needed information to activate the robot
	 * @param sensor
	 */
		
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
					Delay.msDelay(2000); 
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
					Delay.msDelay(2000); 
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

	/**
	 * This method is used to follow lines on the floor, correcting the speed of one wheel if it goes too fast
	 * The function stop when an intersection is reached
	 * 
	 */
	private static void detectIntersection(EV3ColorSensor sensor){
		
		while(true){
			System.out.println(sensor.getColorID());
			
			if (sensor.getColorID() == 0){
				break;
			}
			if (sensor.getColorID() == 13){
				Motor.A.setSpeed(Motor.A.getRotationSpeed() - 30);
				Delay.msDelay(500);
				Motor.A.setSpeed(Motor.A.getRotationSpeed() + 30);

			}
			if (sensor.getColorID() == 2){
				Motor.B.setSpeed(Motor.B.getRotationSpeed() - 30);
				Delay.msDelay(500);
				Motor.B.setSpeed(Motor.B.getRotationSpeed() + 30);

			}
			
		
		}
		
	}

	/**
	 * This method is used when contoling the robot using keyboard
	 * @param infos
	 */
	private static void keyPress(JSONObject infos){
		switch (infos.getInt("key")){
			case 1: //left motor
				Motor.A.setSpeed(540);
				if(infos.getInt("press") > 0){
					Motor.A.forward();
					Delay.msDelay(100);
				}else{
					Motor.A.stop();
				}
				break;
			case 2: //rigth motor
				Motor.B.setSpeed(540);
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

				}else{
					Motor.A.flt(true);
					Motor.B.flt();
				}
				break;
			case 4: //both backward
				Motor.A.setSpeed(360);
				Motor.B.setSpeed(360);
				if(infos.getInt("press") > 0){
					Motor.A.backward();
					Motor.B.backward();
				}else{
					Motor.A.flt(true);
					Motor.B.flt();
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

/**
 * Comparator used in the priority queue
 */
class MyJSONComparator implements Comparator<JSONObject> {

		public int compare(JSONObject o1, JSONObject o2) {
		    int int1 = (o1.getInt("type"));
		    int int2 = (o2.getInt("type"));
		    return (int1 - int2);
		}
	}