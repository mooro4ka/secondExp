package nsida.kazey.showcase.view;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import jssc.SerialPortException;
import nsida.kazey.showcase.MainApp;
import nsida.kazey.showcase.model.Items;
import nsida.kazey.showcase.util.H2Provider;
import nsida.kazey.showcase.util.IPrinterListner;
import nsida.kazey.showcase.util.IScaleProListener;

public class PackingController implements IScaleProListener, IPrinterListner{

	@FXML
	private Label ipLabel;
	@FXML
	private TableView<Items> goodsTable;	
	@FXML
	private TableColumn<Items, Integer> goodsCodeColoumn;
	@FXML
	private TableColumn<Items, String> goodsNameColoumn;	
	@FXML
	private TableColumn<Items, String> barcodeColoumn;
	@FXML
	private TableColumn<Items, Float> priceColoumn;
	@FXML
	private GridPane buttonsGridPane;
	@FXML
	private TextField searchWordsField;
	@FXML 
	private Label currentWeightLabel;
	
	private float currentWeightFloat;
	
	private MainApp mainApplication;
	
	private boolean weightIsStable = false;
	
	private boolean canPrint = true;
	
	public PackingController() {
		
	}

	@FXML
    private void initialize() {

		goodsCodeColoumn.setCellValueFactory(cellData -> cellData.getValue().codeProperty().asObject());
		goodsNameColoumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		priceColoumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
		
		for (Node curAPNode : buttonsGridPane.getChildren()) {
			AnchorPane apn = (AnchorPane) curAPNode;
			
			for (Node curBtnNode : apn.getChildren()) { 
				try{
					if (curBtnNode instanceof Button) {
						Button btn = (Button)curBtnNode;
						
						if (isValidNumber(btn.getText()))
							btn.setOnMouseReleased(
									new buttonsGridPaneEvents(btn.getText()));
					}
				} catch(ClassCastException e) { 
					e.printStackTrace(); 
				}
			}
		}
	}

	public void refreshIP() {
		String myIP = "";
		
		try {
			myIP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
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
		
		ipLabel.setText(myIP);
	}

	@FXML
	private void handleClose() {
		goodsTable.getItems().clear();
		mainApplication.setMainScene();		
	}
	
	@FXML
	private void pushLabel() {
		try {
			mainApplication.getTermoPrinter().pushLabel();
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void backSpacingSearchWord() {
		searchWordsField.setText(
				searchWordsField.getText().substring(
						0, 
						searchWordsField.getLength()-1)
				);
		
		if (searchWordsField.getText().isEmpty())
			fillTable(null);
	}
	
	@FXML
	private void findItemsByCode() {
		fillTable(
				searchWordsField.getText().isEmpty()
				? null : Integer.parseInt(searchWordsField.getText()
				)				
		);
	}
	
	@FXML
	private void showIPAddres() {
		refreshIP();	
	}
	
	@FXML
	private void handlePrint() {
		if (weightIsStable && currentWeightFloat != 0) {
			Items selectedItem = goodsTable.getSelectionModel().getSelectedItem();
			if (selectedItem != null)
				printLabel(selectedItem);
		}	
	}
	
	@FXML
	private void setZeroWeight() {
		try {
			mainApplication.getScaleProvider().setZeroWeight();
		} catch (SerialPortException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void fillTable(Integer code) {
		goodsTable.setItems(H2Provider.getItemsForPacking(code));
	}
	
	private boolean isValidNumber(String string) {
		
		return (string != null) 
				&& (string.length() == 1) 
				&& (Character.isDigit(string.charAt(0)));
	}
	
	private void appEndSearchWord(String symbol) {
		searchWordsField.setText(
				searchWordsField.getText().trim() + symbol
				);		
	}
	
	
	private void setCurrentWeightLabel(float currentWeight) {
		this.currentWeightLabel.setText(
				"Вес: " + String.format("%.3f", currentWeight)
		);
	}

	private float getCurrentWeightFloat() {
		return currentWeightFloat;
	}

	private void setCurrentWeightFloat(float currentWeightFloat) {
		this.currentWeightFloat = currentWeightFloat;
	}
	

	@SuppressWarnings("unused")
	private boolean isWeightIsStable() {
		return weightIsStable;
	}

	private void setWeightIsStable(boolean weightIsStable) {
		this.weightIsStable = weightIsStable;
	}

	private void printLabel(Items selectedItem) {
		try {
			mainApplication.getTermoPrinter().printItem(selectedItem, getCurrentWeightFloat());
			canPrint = false;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (SerialPortException e) {
			e.printStackTrace();
		}				
	}

	
	public void setMainApplication(MainApp mainApplication) {
		this.mainApplication = mainApplication;
		searchWordsField.setText("");
		fillTable(null);
	}

	@Override
	public void OnDataChanged(float value, boolean flag) {
		
		if (value == 0)
			canPrint = true;
		
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				setWeightIsStable(flag);
				setCurrentWeightFloat(value);
				setCurrentWeightLabel(value);				
				
				if (canPrint && flag && value != 0) {
					Items selectedItem = goodsTable.getSelectionModel().getSelectedItem();
					if (selectedItem != null)
						printLabel(selectedItem);
				}
			}
			
		});		
	}

	class buttonsGridPaneEvents implements EventHandler<MouseEvent> {

		private String symbol;
		
		public buttonsGridPaneEvents(String symbol) {
			this.symbol = symbol;
		}
		
		@Override
		public void handle(MouseEvent event) {			
			appEndSearchWord(symbol);		
		}
		
	}

	@Override
	public void PaperState(boolean flag) {
		if (!flag){
			mainApplication.setAlarmScene();
		}		
	}

}
