package nsida.kazey.showcase.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import nsida.kazey.showcase.MainApp;
import nsida.kazey.showcase.util.IPrinterListner;

public class AlarmController implements IPrinterListner{
	
	@FXML
	private Label alarmText;
	
	private MainApp mainApplication;
	
	public AlarmController(){
		
	}

	@FXML
    private void initialize() {
		alarmText.setText("Закончилась этикетка!");
	}

	public MainApp getMainApplication() {
		return mainApplication;
	}

	public void setMainApplication(MainApp mainApplication) {
		this.mainApplication = mainApplication;
	}

	@Override
	public void PaperState(boolean flag) {
		// TODO Auto-generated method stub
		
	}
}
