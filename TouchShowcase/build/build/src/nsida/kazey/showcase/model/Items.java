package nsida.kazey.showcase.model;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public interface Items {
	
	public boolean isGroup();


	public Integer getCode(); 
	
	public IntegerProperty codeProperty();

	
	public String getDescription(); 
	
	public void setDescription(String description);

	public StringProperty descriptionProperty();
	

	public String getButtonDescription(); 
	
	public void setButtonDescription(String description);

	public StringProperty buttonDescriptionProperty();
	
	
	public String getImgPath(); 

	public void setImgPath(String imgPath);
	
	public StringProperty imgPathProperty();
	
	
	public String getBarcode();

	public void setBarcode(String barcode);

	public StringProperty barcodeProperty();
	
	
	public float getPrice();
	
	public void setPrice(float price);
	
	public FloatProperty priceProperty();
		
}
