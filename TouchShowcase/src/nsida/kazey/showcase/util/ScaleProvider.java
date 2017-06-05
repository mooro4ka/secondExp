package nsida.kazey.showcase.util;

import java.util.ArrayList;

import jssc.SerialPort;
import jssc.SerialPortException;

public class ScaleProvider extends Thread{
	
	private final SerialPort serialPort;
	private float currentWeight;
	private boolean weightIsStable;
	private int powerOfTen;
	private boolean setZeroWeight = false;
	@SuppressWarnings("unused")
	private boolean setTareWeight = true;
	
	private static final byte ENQ = 0x05;
	private static final byte STX = 0x02;
	private static final byte ACK = 0x06;
	private static final byte NAK = 0x15;
	private static final byte[] WEIGHT_QUERY = {STX, 0x05, 0x3A, 0, 0, 0, 0, 0x3F};
	private static final byte[] CHANNEL_PROPS = {STX, 0x02, (byte) 0xE8, 0, (byte) 0xEA};
	private static final byte[] SET_ZERO = {STX, 0x05, 0x30, 0, 0, 0, 0, 0x35};
	
	private ArrayList<IScaleProListener> listeners = new ArrayList<IScaleProListener>();
	
	public void AddListener(IScaleProListener listener) {
		listeners.add(listener);
	}
	
	public void RemoveListener(IScaleProListener listener) {
		listeners.remove(listener);
	}
	
	private void FireListeners(float value, boolean flag) {
		for(IScaleProListener listener : listeners)
			listener.OnDataChanged(value, flag);
	}
	
	public ScaleProvider(String portName) {
		
		this.setDaemon(true);
		
		serialPort = new SerialPort(portName);
		currentWeight = 0;
		
		try {
          
            serialPort.openPort();
           
            serialPort.setParams(SerialPort.BAUDRATE_9600,
                                 SerialPort.DATABITS_8,
                                 SerialPort.STOPBITS_1,
                                 SerialPort.PARITY_NONE);
            
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | 
                                          SerialPort.FLOWCONTROL_RTSCTS_OUT);
                       
			readChannelProps(); 	
        }
        catch (SerialPortException e) {          
        	System.out.println(e);
            
        	try{
            	
            	if(serialPort.isOpened()) 
            		serialPort.closePort();
            	
            } catch(SerialPortException ex) {
            	System.out.println(ex);
            }
        	
        } catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void setZeroWeight() throws SerialPortException, InterruptedException {
		setZeroWeight = true;
	}

	private void readChannelProps() throws SerialPortException, InterruptedException {
		
		serialPort.writeBytes(CHANNEL_PROPS);
		Thread.sleep(30);
						
		byte answer[] = serialPort.readBytes();
		
		if (answer[0] == ACK) {	
			powerOfTen = answer[8];
		}
	}

	public boolean isReady() {
				
		try {
			if (serialPort.isOpened()) {
				serialPort.writeByte(ENQ);
				
				Thread.sleep(100);
				
				for(int i = 0; i<3; i++) {				
					byte buf[] = serialPort.readBytes();				
					if (buf.length > 0 && buf[0] == NAK)
						return true;
					
					Thread.sleep(100);
				}
			}
		} catch(SerialPortException e) {
			System.out.println(e);
		} catch(InterruptedException e) {
			System.out.println(e);
		} 
		
		return false;		
	}
		
	private void ClosePort() {
		try{
        	if(serialPort.isOpened()) 
        		serialPort.closePort();
        } catch(SerialPortException ex) {
        	System.out.println(ex);
        }
	}
			
	@Override
	public void run() {
		do 
		{
			currentWeight = 0;
			weightIsStable = false;
			if(!Thread.interrupted())
			{
				try {
					if (setZeroWeight) {
						serialPort.writeBytes(SET_ZERO);
						Thread.sleep(30);
						
						@SuppressWarnings("unused")
						byte answer[] = serialPort.readBytes();
						
						setZeroWeight = false;
					}
					serialPort.writeBytes(WEIGHT_QUERY);
					Thread.sleep(30);
									
					byte answer[] = serialPort.readBytes();
					
					if (answer[0] == ACK) {					
						currentWeight = ((answer[8]&255)<<8) + (answer[7]&255);
						weightIsStable = (answer[5] == 21);
					} 
				} catch(SerialPortException e) {
					System.out.println(e);
				} catch(InterruptedException e) {
					System.out.println(e);
				} finally {
					FireListeners(convert2KG(currentWeight), weightIsStable);
				}
			}
			else {
				ClosePort();
				return;
			}
				
		}
		while(true);
	}

	private float convert2KG(float weight) {
		
		switch (powerOfTen) {
			case -4: return weight/10000;
			case -3: return weight/1000;
			case -2: return weight/100;
			default : return 0;
		}
		
		
	}
}
