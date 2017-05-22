import gnu.io.*;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TooManyListenersException;


public class commTest implements SerialPortEventListener {

	//for containing the ports that will be found
	private Enumeration ports = null;
	//map the port names to CommPortIdentifiers
	private HashMap portMap = new HashMap();
	
	//this is the object that contains the opened port
	private CommPortIdentifier selectedPortIdentifier = null;
	private SerialPort serialPort = null;
	
	//input and output streams for sending and receiving data
	private InputStream input = null;
	private OutputStream output = null;
	
	//just a boolean flag that i use for enabling
	//and disabling buttons depending on whether the program
	//is connected to a serial port or not
	private boolean bConnected = false;
	
	//the timeout value for connecting with the port
	final static int TIMEOUT = 2000;
	
	//some ascii values for for certain things
	final static int SPACE_ASCII = 32;
	final static int DASH_ASCII = 45;
	final static int NEW_LINE_ASCII = 10;
	


	
	
	public void searchForPorts()
    {
        ports = CommPortIdentifier.getPortIdentifiers();

        while (ports.hasMoreElements())
        {
            CommPortIdentifier curPort = (CommPortIdentifier)ports.nextElement();
            if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL)
            {
            	System.out.println(curPort.getName());
                portMap.put(curPort.getName(), curPort);
            }
        }
    }
	
	
	
    public void connect()
    {
        String selectedPort = "COM4";       
        selectedPortIdentifier = (CommPortIdentifier)portMap.get(selectedPort);
        System.out.println(selectedPortIdentifier);
        CommPort commPort = null;
        try
        {
            commPort = selectedPortIdentifier.open("TigerControlPanel", TIMEOUT);
            serialPort = (SerialPort)commPort;
            System.out.println("Connected!");
        
        }
        catch (PortInUseException e)
        {
          
        }
        catch (Exception e)
        {
      
        }
    }
	
    
    //open the input and output streams
    //pre style="font-size: 11px;": an open port
    //post: initialized input and output streams for use to communicate data
    public boolean initIOStream()
    {
        //return value for whether opening the streams is successful or not
        boolean successful = false;

        try {
            //
            input = serialPort.getInputStream();
            output = serialPort.getOutputStream();
            System.out.println("I/O streams initiated!");

            successful = true;
            return successful;
        }
        catch (IOException e) {
              
            return successful;
        }
    }
	
    
  //starts the event listener that knows whenever data is available to be read
    //pre style="font-size: 11px;": an open serial port
    //post: an event listener for the serial port that knows when data is received
    public void initListener()
    {
        try
        {
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
            System.out.println("Listeners initiated!");
        }
        catch (TooManyListenersException e)
        {
             
        }
    }
    
    
    public void disconnect()
    {

        try
        {  
            serialPort.removeEventListener();
            serialPort.close();
            input.close();
            output.close();
            System.out.println("Disconnected!");
                  
        }
        catch (Exception e)
        {
            
  
        }
    }
    
    @Override
    public void serialEvent(SerialPortEvent evt) {
        if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE)
        {
            try
            {
                byte singleData = (byte)input.read();

                if (singleData != NEW_LINE_ASCII)
                {
                	System.out.println(new String(new byte[] {singleData}));
                }
                else
                {
                    
                }
            }
            catch (Exception e)
            {   
            	System.out.println("Failed to read data");

            }
        }
    }
    
    public void writeData()
    {
       /* try
        {
        	output.write("hh".getBytes());
        	System.out.println("Hej?");
            output.flush();
            System.out.println("ah");

        }
        catch (Exception e)
        {
        	System.out.print("Failed to write data");
        }*/
     // create a new thread for sending data to the arduino
		Thread thread = new Thread(){
			@Override public void run() {
				// wait after connecting, so the bootloader can finish
				try {Thread.sleep(100); } catch(Exception e) {}

				// enter an infinite loop that sends text to the arduino
				PrintWriter out = new PrintWriter(output);
				while(true) {
					out.print("hh".getBytes());
					out.flush();
					try {Thread.sleep(100); } catch(Exception e) {}
				}
			}
		};
		thread.start();
        
    }
    

}