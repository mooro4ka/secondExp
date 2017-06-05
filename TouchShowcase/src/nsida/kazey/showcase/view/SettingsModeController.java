package nsida.kazey.showcase.view;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import nsida.kazey.showcase.MainApp;
import nsida.kazey.showcase.util.IPrinterListner;

public class SettingsModeController implements IPrinterListner{
	
	@FXML
	private Label ipLabel;

	private MainApp mainApplication;
	
	public SettingsModeController(){
		
	}
	
	@FXML
	private void initialize() {
		refreshIP();
	}

	@FXML
	public void refreshIP() {
		String myIP = "";
		
		try {
			myIP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		if ((!myIP.equals("127.0.0.1")) && (!myIP.equals("127.0.1.1")))
			ipLabel.setText(myIP);
		
		String tempIP = "";
		
		Enumeration<NetworkInterface> en;
		try {
			en = NetworkInterface.getNetworkInterfaces();
			while(en.hasMoreElements()){
			    NetworkInterface ni= en.nextElement();
			    Enumeration<InetAddress> ee = ni.getInetAddresses();
			    while(ee.hasMoreElements()) {
			        InetAddress ia=  ee.nextElement();
			        tempIP = ia.getHostAddress();
			        
			        if ((!tempIP.equals("127.0.0.1")) && 
			        		(!tempIP.endsWith("%eth0")) && 
			        		(!tempIP.endsWith("%lo")))
			        	myIP = tempIP;
			    }
			 }
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		
		ipLabel.setText("IP-адрес: " + myIP);
	}

	public MainApp getMainApplication() {
		return mainApplication;
	}

	public void setMainApplication(MainApp mainAppication) {
		this.mainApplication = mainAppication;
	}

	@Override
	public void PaperState(boolean flag) {
		// TODO Auto-generated method stub
		
	}

}
