package nsida.kazey.showcase.view;

import java.io.UnsupportedEncodingException;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import jssc.SerialPortException;
import nsida.kazey.showcase.MainApp;
import nsida.kazey.showcase.model.Items;
import nsida.kazey.showcase.util.H2Provider;
import nsida.kazey.showcase.util.IPrinterListner;
import nsida.kazey.showcase.util.IScaleProListener;

public class ShowcaseController implements IScaleProListener, IPrinterListner {
	
	private MainApp mainApplication;
	
	@FXML
	private BorderPane showcaseRoot;	
	@FXML
	private GridPane showcaseBody;
	@FXML
	private Button leftButton;
	@FXML
	private Button rightButton;
	@FXML
	private Button centralButton;
	@FXML
	private Label currentWeight;
	private float currentWeightFloat;
	@FXML
	private Label currentDescription;
	@FXML
	private Label currentPrice;
	private float currentPriceFloat;
	
	private boolean weightIsStable = false;	
	private Items selectedItem = null;
	private int currentPage;
	private int totalItemsAtPage;
	private int totalPages;
	
	public ShowcaseController() {
	}
	
	@FXML
	private void initialize(){		
		
		initRoot();
		initShowcase();
		initFooter();
		
		fillHeader(null);
		fillShowcase();
		
	}
    
	
    public MainApp getMainApplication() {
		return mainApplication;
	}

	public void setMainApplication(MainApp mainApplication) {
		this.mainApplication = mainApplication;
	}

	public int getCurrentPage(){
        return this.currentPage;
    }
	
    public void setCurrentPage(int currenPage){
        if (currenPage <= 0) 
            this.currentPage = 1;
        else 
            this.currentPage = currenPage;
    }
    
    public int getTotalPages(){
        return this.totalPages;
    }

    public void setTotalPages(int totalProducts){
        
        if (totalProducts <= totalItemsAtPage)
            this.totalPages = 1;
        else{            
            int div = totalProducts/totalItemsAtPage;

            if((totalProducts % totalItemsAtPage) == 0)
                this.totalPages = div;
            else
                this.totalPages = div + 1;
        }
    }
    
    public Items getSelectedItem(){
        return this.selectedItem;
    }

    public void setSelectedItem(Items selectedItem){
        this.selectedItem = selectedItem;        
        TagMaster.TagDescription(selectedItem, this.currentDescription);
     }
 
	public float getCurrentPriceFloat() {
		return currentPriceFloat;
	}

	public void setCurrentPriceFloat(float currentPriceFloat) {
		this.currentPriceFloat = currentPriceFloat;
		TagMaster.TagPrice(currentPriceFloat, currentPrice);
	}

	public float getCurrentWeightFloat() {
		return currentWeightFloat;
	}

	public void setCurrentWeightFloat(float currentWeightFloat) {
		this.currentWeightFloat = currentWeightFloat;
		TagMaster.TagWeight(currentWeightFloat, currentWeight);
	}

	public boolean isWeightStable() {
		return weightIsStable;
	}

	public void setWeightStable(boolean weightIsStable) {
		this.weightIsStable = weightIsStable;
	}

	
	public void setFooterButtonsVisible(){

        leftButton.setVisible(currentPage != 1);
        rightButton.setVisible(currentPage != totalPages);
        centralButton.setVisible(true);
    }
    
	public void initRoot() {
		showcaseRoot.setOnMouseClicked(new RootEvents.OnMouseClicked(this));
	}
	
	public void initShowcase() {
		
		setCurrentPage(1);
		totalItemsAtPage = 
				showcaseBody.getColumnConstraints().size()*
				showcaseBody.getRowConstraints().size();

	}	

	public void initFooter() {
    	
		ImageView leftImgV = new ImageView(new Image("file:picts/icons/ArrowLeft2.png"));
		leftImgV.setFitHeight(45);
		leftImgV.setFitWidth(250);
		leftButton.setGraphic(leftImgV);
		leftButton.setAlignment(Pos.CENTER);
		leftButton.setOnMouseReleased(new FooterEvents.OnMouseReleased(this, -1));
	
		ImageView rightImgV = new ImageView(new Image("file:picts/icons/ArrowRight2.png"));
        rightImgV.setFitHeight(45);
        rightImgV.setFitWidth(250);
        rightButton.setGraphic(rightImgV);
        rightButton.setAlignment(Pos.CENTER);
        rightButton.setOnMouseReleased(new FooterEvents.OnMouseReleased(this, 1));
   
        centralButton.setOnMouseReleased(new FooterEvents.OnMouseReleased(this, 0));
        ImageView imgV = new ImageView(new Image("file:picts/icons/GreyHome1.png"));
        imgV.setFitHeight(45);
        imgV.setFitWidth(45);
        centralButton.setGraphic(imgV);
        
    }    
	
	public void fillHeader(Items item) {
		setSelectedItem(item);
		setCurrentPriceFloat(item==null ? 0 : item.getPrice());
	}

	public void fillShowcase() {
 
		showcaseBody.getChildren().clear();
		
		ObservableList<Items> itemsList;
		
		itemsList = H2Provider.getItemsForShowcase(selectedItem);

		int currentRow = 0, currentCol = 0;
		int lowIndex = totalItemsAtPage * (currentPage-1);
		int highIndex = lowIndex + totalItemsAtPage;
		
		for (Items curItem : itemsList) {
			if (itemsList.indexOf(curItem)>=lowIndex && 
					itemsList.indexOf(curItem)<highIndex) {
	            if (currentCol == this.showcaseBody.getColumnConstraints().size()) {
	                currentCol = 0;
	                currentRow++;
	            }
	
				showcaseBody.add(createButton(curItem), currentCol, currentRow);
				
				currentCol++;
			}
		}
		setTotalPages(itemsList.size());
		setFooterButtonsVisible();
	}
		
	private Node createButton(Items curItem) {
		
		ImageView imgView;
		try {
			Image img = new Image(curItem.getImgPath());
			imgView = new ImageView(img);
		} catch (Exception e ) {			
			System.out.println(e.getLocalizedMessage());
			imgView = new ImageView();
		}
				 
		imgView.setFitHeight(Screen.getPrimary().getBounds().getHeight()/showcaseBody.getRowConstraints().size()-92);
		imgView.setFitWidth(Screen.getPrimary().getBounds().getWidth()/showcaseBody.getColumnConstraints().size()-16);		
		
		Label lbl = new Label();
		lbl.setWrapText(true);
		lbl.setTextAlignment(TextAlignment.LEFT);
		lbl.setText(curItem.getButtonDescription().toUpperCase());
		lbl.setPrefHeight(curItem.isGroup() ? 45 : 35);
		lbl.setPadding(new Insets(0, 3, 0, 3));
		
		VBox newVBox = new VBox();
        newVBox.setAlignment(curItem.isGroup() ? Pos.CENTER : Pos.BOTTOM_CENTER);       
        newVBox.setId(curItem.isGroup() ? "group" : "item");
        newVBox.setOnMousePressed(
                new ShowcaseEvents.OnMousePressed(newVBox));
        newVBox.setOnMouseReleased(
                new ShowcaseEvents.OnMouseReleased(newVBox, curItem, this));
        
        ObservableList<Node> childList = newVBox.getChildren();
        if (!curItem.isGroup())
        	childList.add(imgView);
        childList.add(lbl);
                
        return newVBox;
	}
	
	
	@Override
	public void OnDataChanged(float value, boolean flag) {
		//if(getCurrentWeightFloat()!=value){
			Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
					setWeightStable(flag);
					setCurrentWeightFloat(value);
					
					if (selectedItem != null && 
							!selectedItem.isGroup() && 
							flag && 
							value != 0) {
						printLabel(selectedItem);
						setSelectedItem(null);
						setCurrentPriceFloat(0);
						initShowcase();
						fillShowcase();
					}
				}
			});
		//}
	}

	
	public void printLabel(Items src) {
		try {
			getMainApplication().getTermoPrinter().printItem(src, getCurrentWeightFloat());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (SerialPortException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void PaperState(boolean flag) {
		if (!flag){
//			Platform.runLater(new Runnable() {
//				
//				@Override
//				public void run() {
				
					showcaseBody.getChildren().clear();
//				}
//			});
		}
	}
}
