package nsida.kazey.showcase.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import nsida.kazey.showcase.model.Items;

public class TermoPrinter {
	
	private SerialPort serialPort;
	
	private final byte[] status = {27, 118};
	private final byte[] init = {27, 64}; 			// defaulting printer, clear cash
	private final byte[] print = {10}; 				// print in buffer. uses in the pageMode
	private final byte[] printInPageMode = {12}; 	// print out from the buffer
	private final byte[] marksheetPositioning = {29, 12};
	private final byte[] setPageMode = {27, 76};	// set PageMode
	private final byte[] setRotatePrint180 = {27, 84, 2};
	private final byte[] setPaperModel = {27, 99, 48, 1};
	private final byte[] setCharacterSizeDefault = {29, 33, 0};
	private final byte[] setCharacterSizeDouble = {29, 33, 1};
	
	private final byte[] mainPrinArea 		= {27, 87, 16, 0, 16, 0, (byte)176, 1, (byte) 200, 0};
	private final byte[] pricePrinArea 		= {27, 87, (byte)136, 0, 16, 0, 72, 0, 48, 0};
	private final byte[] weightPrinArea 	= {27, 87, (byte)216, 0, 16, 0, 72, 0, 48, 0};
	private final byte[] totalPrinArea 		= {27, 87, 16, 0, (byte)112, 0, 96, 0, 32, 0};
	private final byte[] someTextPrinArea 	= {27, 87, 16, 0, 16, 0, (byte)176, 1, 24, 0};
	
	private final byte[] commandBarcode = {29, 107, 67, 13};
	private final byte[] posBarcodeHRI 	= {29, 72, 2};
	private final byte[] fontBarcodeHRI = {29, 102, 1};
	private final byte[] heightBarcode 	= {29, 104, 40};

	private ArrayList<IPrinterListner> listeners = new ArrayList<IPrinterListner>();
	
	public void AddListener(IPrinterListner listener) {
		listeners.add(listener);
	}
	
	public void RemoveListener(IPrinterListner listener) {
		listeners.remove(listener);
	}
	
	private void FireListeners(boolean flag) {
		for(IPrinterListner listener : listeners)
			listener.PaperState(flag);
	}
	
	public TermoPrinter(String printerName) {
	    
		serialPort = new SerialPort(printerName);
		try {

	    	serialPort.openPort();	        
	        serialPort.setParams(
	        		SerialPort.BAUDRATE_19200,
	                SerialPort.DATABITS_8,
	                SerialPort.STOPBITS_1,
	                SerialPort.PARITY_NONE);
	        serialPort.setEventsMask(SerialPort.MASK_RXCHAR);	         
	        serialPort.addEventListener(new SerialPortReader());
	        
	    }
	    catch (SerialPortException ex) {
	        System.out.println(ex);
	    }
	}
	
	public void addForeignEventListner(SerialPortEventListener listener) throws SerialPortException {
		serialPort.addEventListener(listener);
	}
	
	public void checkStatus() throws SerialPortException {
		serialPort.writeBytes(status);		
	}

	public void pushLabel() throws SerialPortException {
		
		serialPort.writeBytes(init);
		serialPort.writeBytes(setPaperModel);
		serialPort.writeBytes(setPageMode);
		serialPort.writeBytes(marksheetPositioning);
		
	}

	public void printItem(Items item, float weight) throws SerialPortException, UnsupportedEncodingException {
		
		String strBarcode = item.getBarcode();
        String strWeight = String.format("%.3f", weight);
        String strPrice = Float.toString(item.getPrice()).replace(".", ",");
        String strTotal = String.format("%.2f", item.getPrice()*weight).replace(".", ",");
        String strEan13 = getEAN13(strBarcode, strWeight);
        String[] descriptions = getDescriptions(item.getDescription());
		
        serialPort.writeBytes(init);
        serialPort.writeBytes(setPageMode);
        
		serialPort.writeBytes(mainPrinArea);                        
		serialPort.writeBytes(setRotatePrint180);
		serialPort.writeBytes(descriptions[0].getBytes("cp866"));
		serialPort.writeBytes(print);
		serialPort.writeBytes(descriptions[1].getBytes("cp866"));
		serialPort.writeBytes(print);
		serialPort.writeBytes(print);
		serialPort.writeBytes(" ".getBytes());
		serialPort.writeBytes(heightBarcode);
		serialPort.writeBytes(fontBarcodeHRI);
		serialPort.writeBytes(posBarcodeHRI);
		serialPort.writeBytes(commandBarcode);
		serialPort.writeBytes(strEan13.getBytes());
		serialPort.writeBytes(print);  
		
		serialPort.writeBytes(weightPrinArea);
		serialPort.writeBytes(strWeight.getBytes());
		serialPort.writeBytes(print);
		
		serialPort.writeBytes(pricePrinArea);
		serialPort.writeBytes(strPrice.getBytes());
		serialPort.writeBytes(print);
		
		serialPort.writeBytes(totalPrinArea);
		serialPort.writeBytes(setCharacterSizeDouble);
		serialPort.writeBytes(strTotal.getBytes());
		serialPort.writeBytes(print);
		
		serialPort.writeBytes(someTextPrinArea);
		serialPort.writeBytes(setCharacterSizeDefault);
		serialPort.writeBytes("Спасибо за покупку!".getBytes("cp866"));
		serialPort.writeBytes(print);
		
		serialPort.writeBytes(printInPageMode);
				
	}

	public void readSerialData(){
		try {
            byte buffer[] = serialPort.readBytes();
            for (byte curByte : buffer)
            	System.out.print(curByte + ", ");
            System.out.println();
        }
        catch (SerialPortException ex) {
            System.out.println(ex);
        }
	}
	
	private String getEAN13(String strBarcode, String strWeight) {
		
		strWeight = strWeight.replace(",", "");
		strWeight = strWeight.replace(".", "");
		
		while (strWeight.length() < 5)
			strWeight = "0" + strWeight;
		
		String strEan13 = strBarcode + strWeight;
        int chetInt = 0, nechetInt = 0;
        
        for(int i = 0; i < strEan13.length(); i++) {
        	if ((i&1) == 0)
        		chetInt += Integer.parseInt("" + strEan13.charAt(i));
        	else
        		nechetInt += Integer.parseInt("" + strEan13.charAt(i));
        }
        
        nechetInt *= 3;
        
        int tempNumber = (chetInt + nechetInt) % 10;
        int controlNumber = (tempNumber == 0 ? tempNumber : 10 - tempNumber);
        
        strEan13 += String.valueOf(controlNumber);
		return strEan13;
		
	}
	
    private static String[] getDescriptions(String inputString) {
		
    	final int endIndex = 36; 
		String[] descriptions = new String[2];
		
		if (inputString.length() > endIndex) {
			descriptions[0] = inputString.substring(0, 35);
			descriptions[1] = inputString.substring(35, inputString.length()-1);
		} 
		else {
			descriptions[0] = inputString;
			descriptions[1] = "";			
		}
		
		return descriptions;
	}

	class SerialPortReader implements SerialPortEventListener {
	
	    public void serialEvent(SerialPortEvent event) {
        	if(event.isRXCHAR()){
               try {
                    byte buffer[] = serialPort.readBytes();
                    
                    if (buffer.length == 4){
                    	switch (buffer[2]) {
                     		case 3 : 
                    			FireListeners(true);
                    			break;
                    		default : 
                    			FireListeners(false);
                    			break;
                    	}
                    }
                    else if (buffer.length == 1) {
						if (buffer[0] == 3)
							FireListeners(true);
						else
							FireListeners(false);
					}
                }
                catch (SerialPortException ex) {
                    System.out.println(ex);
                }
            }	
	    }
	}

}
