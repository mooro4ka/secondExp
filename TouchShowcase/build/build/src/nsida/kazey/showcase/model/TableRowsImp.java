package nsida.kazey.showcase.model;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableRowsImp implements ITableRows{

	private final StringProperty GOODS_NAME;
	private final IntegerProperty GOODS_CODE;
	private final StringProperty BUTTON_NAME;
	private final StringProperty BARCODE;
	private final FloatProperty PRICE;
	private final StringProperty GROUP_NAME;
	
	public TableRowsImp(Integer goodsCode, String goodsName, String buttonName, 
			String barcode,	Float price, String groupName) {
	
		GOODS_CODE = new SimpleIntegerProperty(goodsCode);
		GOODS_NAME = new SimpleStringProperty(goodsName);
		BUTTON_NAME = new SimpleStringProperty(buttonName);
		BARCODE = new SimpleStringProperty(barcode);
		PRICE = new SimpleFloatProperty(price);
		GROUP_NAME = new SimpleStringProperty(groupName);
	
	}
	
	@Override
	public StringProperty goodsNameProperty() {
		return GOODS_NAME;
	}

	@Override
	public String getGoodsName() {
		return GOODS_NAME.get();
	}

	@Override
	public void setGoodsName(String goodsName) {
		GOODS_NAME.set(goodsName);
	}


	@Override
	public StringProperty buttonNameProperty() {
		return BUTTON_NAME;
	}

	@Override
	public String getButtonName() {
		return BUTTON_NAME.get();
	}

	@Override
	public void setButtonName(String buttonName) {
		BUTTON_NAME.set(buttonName);
	}

	
	@Override
	public StringProperty barcodeProperty() {
		return BARCODE;
	}

	@Override
	public String getBarcode() {
		return BARCODE.get();
	}

	@Override
	public void setBarcode(String barcode) {
		BARCODE.set(barcode);
	}

	
	@Override
	public FloatProperty priceProperty() {
		return PRICE;
	}

	@Override
	public Float getPrice() {
		return PRICE.get();
	}

	@Override
	public void setPrice(Float price) {
		PRICE.set(price);
	}

	
	@Override
	public StringProperty groupNameProperty() {
		return GROUP_NAME;
	}

	@Override
	public String getGroupName() {
		return GROUP_NAME.get();
	}

	@Override
	public void setGroupName(String groupName) {
		GROUP_NAME.set(groupName);
	}

	@Override
	public IntegerProperty goodsCodeProperty() {
		return GOODS_CODE;
	}

	@Override
	public Integer getGoodsCode() {
		return GOODS_CODE.get();
	}

	@Override
	public void setGoodsCode(Integer goodsCode) {
		GOODS_CODE.set(goodsCode);
	}

}
