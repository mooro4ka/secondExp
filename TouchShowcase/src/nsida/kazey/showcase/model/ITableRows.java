package nsida.kazey.showcase.model;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public interface ITableRows {

	public StringProperty goodsNameProperty();
	
	public String getGoodsName();
	
	public void setGoodsName(String goodsName);
		

	public StringProperty buttonNameProperty();
	
	public String getButtonName();
	
	public void setButtonName(String buttonName);

	
	public StringProperty barcodeProperty();
	
	public String getBarcode();
	
	public void setBarcode(String barcode);
	
	
	public FloatProperty priceProperty();
	
	public Float getPrice();
	
	public void setPrice(Float price);
	
	
	public StringProperty groupNameProperty();
	
	public String getGroupName();
	
	public void setGroupName(String groupName);


	public IntegerProperty goodsCodeProperty();
	
	public Integer getGoodsCode();
	
	public void setGoodsCode(Integer goodsCode);

}
