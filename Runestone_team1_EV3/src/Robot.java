import json.*;
import lejos.hardware.motor.Motor;
import lejos.utility.Delay;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.port.SensorPort;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Comparator;
import java.io.*;

/**
 * The robot class is the code run in the ev3 robot
 * It has 2 attributes, the sensor and the priorityQueue 
 *
 */

public class Robot {
	final EV3ColorSensor sensor;
	final EV3GyroSensor gyro;
	public Queue<JSONObject> instructions; 
	public Queue<JSONObject> tasksDone;

	
	public Robot() {
		sensor = new EV3ColorSensor(SensorPort.S1);
		gyro = new EV3GyroSensor(SensorPort.S4);
		gyro.reset();
		instructions = new LinkedList<JSONObject>();
		tasksDone = new LinkedList<JSONObject>();
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
					     	 move(currentInstruct.getJSONObject("content"), ev3);
							 ev3.tasksDone.add(currentInstruct);
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
		
	private static void move(JSONObject infos, Robot ev3){
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
					mesureAngle(ev3);
					//Delay.msDelay(2000); 
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
					mesureAngle(ev3);
					//Delay.msDelay(2000); 
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
					Delay.msDelay(1500); 

					detectIntersection(ev3);
					if(infos.getInt("direction") == -1){
						Delay.msDelay(400);
					}
					Motor.A.stop(true); 
					Motor.B.stop();
					break;
					
			case 4: Motor.C.setSpeed(100);
					Motor.C.rotateTo(infos.getInt("angle") * infos.getInt("direction"), false);//elevator
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
	private static void detectIntersection(Robot ev3){
		int greenCount = 0;
		int blueCount = 0;
		while(true){
			int turnDuration = 50;
			int turnDuration2 = 150;
			int turnSpeed = 40;
			int turnSpeed2 = 40;
			
			int color = ev3.sensor.getColorID();
			
			if (color == 0){//red
			                //Delay.msDelay(200);
			                //if (greenCount > 0) { //has turned from green
			                    //for (int i = 0; i < blueCount; i++) { 
			                    	//if (ev3.sensor.getColorID() != 0){
			                    		//break;
			                    	//}
			                    	//Motor.B.setSpeed(Motor.B.getSpeed() + turnSpeed);
			                  //      Motor.A.stop();
			                	//	Delay.msDelay(turnDuration);
			                        //Motor.B.setSpeed(Motor.B.getSpeed() - turnSpeed);
			                    //}

			                //}
			                //if (blueCount >0){    //has turned from blue
			                    //for (int i = 0; i < greenCount; i++) { 
			                    	//if (ev3.sensor.getColorID() != 0){
			                    		//break;
			                    	//}
			                    	//Motor.A.setSpeed(Motor.A.getSpeed() + turnSpeed);
			                  //      Motor.B.stop();
			                	//	Delay.msDelay(turnDuration);
			                        //Motor.A.setSpeed(Motor.A.getSpeed() - turnSpeed);
			                    //}                        
			                //}
			                greenCount = 0;
			                blueCount = 0;
			                Delay.msDelay(100);
			                break;
			            }

			

			if (color == 13){//green
				System.out.println(color);
			                greenCount++;
			                Motor.A.setSpeed(Motor.A.getSpeed() - turnSpeed2);
			                Delay.msDelay(turnDuration2);
			                //while(ev3.sensor.getColorID() == 13){
			                //}
			/*                Motor.A.setSpeed(Motor.A.getSpeed() + 60);
			                Delay.msDelay(100);*/
			                Motor.A.setSpeed(Motor.A.getSpeed() + turnSpeed2);


			            }
			if (color == 2){//blue
				System.out.println(color);
			                blueCount++;
			                Motor.B.setSpeed(Motor.B.getSpeed() - turnSpeed2);
			                Delay.msDelay(turnDuration2);
			                //while(ev3.sensor.getColorID() == 2){
			                //}
			/*                Motor.B.setSpeed(Motor.B.getSpeed() + 60);
			                Delay.msDelay(100);*/
			                Motor.B.setSpeed(Motor.B.getSpeed() + turnSpeed2);

			            }

			 if (color == 7){//black


			                if (greenCount == 0) { //has turned from green
			                	System.out.println(blueCount);
			                    for (int i = 0; i < blueCount; i++) {
			                    	if (ev3.sensor.getColorID() != 7){
			                    		break;
			                    	}
			          
			                        Motor.B.setSpeed(Motor.B.getSpeed() + turnSpeed);
			                        Delay.msDelay(turnDuration);
			                        Motor.B.setSpeed(Motor.B.getSpeed() - turnSpeed);
			                    }

			                }
			                else {    //has turned from blue
			                    for (int i = 0; i < greenCount; i++) { 
			                    	if (ev3.sensor.getColorID() != 7){
			                    		break;
			                    	}
			                    	Motor.A.setSpeed(Motor.A.getSpeed() + turnSpeed);
			                        Delay.msDelay(turnDuration);
			                        Motor.A.setSpeed(Motor.A.getSpeed() - turnSpeed);
			                    }                        
			                }
			                greenCount = 0;
			                blueCount = 0;
			            }
		}
		
	}
	
	private static void mesureAngle(Robot ev3){
		float initialAngle[] = new float[1];
		ev3.gyro.getAngleMode().fetchSample(initialAngle, 0);
		while(true){
			float angle[] = new float[1];
			ev3.gyro.getAngleMode().fetchSample(angle, 0);
			//System.out.println(angle[0]);
			if (Math.abs(initialAngle[0] - angle[0]) > 85){
				break;
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
		    int diff = (int1 - int2);
		    if (diff == 0){
		    	diff = 1;
		    }
		    return diff;
		}
	}