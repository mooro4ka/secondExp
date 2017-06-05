package nsida.kazey.showcase.model;


import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Goods implements Items{
	
	private final boolean isGroup;
	private final StringProperty description;
	private final StringProperty buttonDescription;
	private final StringProperty imgPath;
	private final StringProperty barcode;
	private final FloatProperty price;
	private final IntegerProperty code;
	
	public Items parent = null;
	
	public Goods(boolean isGroup, String description, String buttonDescription, 
					String imgPath, String barcode, float price, Integer code) {	
		
		this.isGroup = isGroup;
		this.description = new SimpleStringProperty(description);
		this.buttonDescription = new SimpleStringProperty(buttonDescription);
		this.imgPath = new SimpleStringProperty("file:picts/goods/" + imgPath);
		this.barcode = new SimpleStringProperty(barcode);
		this.price = new SimpleFloatProperty(price);
		this.code = new SimpleIntegerProperty(code);

	}

	@Override
	public boolean isGroup() {
		return this.isGroup;
	}

	
	@Override
	public String getDescription() {
		return this.description.get();
	}
	
	@Override
	public void setDescription(String description) {
		this.description.set(description);
	}

	@Override
	public StringProperty descriptionProperty() {
		return this.description;
	}

	
	@Override
	public String getImgPath() {
		return this.imgPath.get();
	}

	@Override
	public void setImgPath(String imgPath) {
		this.imgPath.set(imgPath);
	}

	@Override
	public StringProperty imgPathProperty() {
		return this.imgPath;
	}

	
	@Override
	public String getBarcode() {
		return this.barcode.get();
	}

	@Override
	public void setBarcode(String barcode) {
		this.barcode.set(barcode);
	}

	@Override
	public StringProperty barcodeProperty() {
		return this.barcode;
	}

	
	@Override
	public float getPrice() {
		return this.price.get();
	}

	@Override
	public void setPrice(float price) {
		this.price.set(price);
	}

	@Override
	public FloatProperty priceProperty() {
		return this.price;
	}
	
	@Override
	public String getButtonDescription() {
		return this.buttonDescription.get();
	}

	@Override
	public void setButtonDescription(String buttonDescription) {
		this.buttonDescription.set(buttonDescription);
	}

	@Override
	public StringProperty buttonDescriptionProperty() {
		return this.buttonDescription;
	}

	@Override
	public Integer getCode() {
		return this.code.get();
	}

	@Override
	public IntegerProperty codeProperty() {
		return this.code;
	}
	
}
