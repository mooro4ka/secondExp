package nsida.kazey.showcase.view;

import javafx.scene.control.Label;
import nsida.kazey.showcase.model.Items;

public class TagMaster {

	public static void TagDescription(Items selectedItem, Label place) {
		if (selectedItem == null || selectedItem.getButtonDescription().trim().isEmpty())
			place.setText("Выберите группу товаров");
		else
			place.setText(selectedItem.getButtonDescription().trim());
	}
	
	public static void TagWeight(float value, Label lblPlace) {
		lblPlace.setText("Вес: " + String.format("%.3f", value));
	}
	
	public static void TagPrice(float value, Label place) {
		place.setText("Цена: " + String.format("%.2f", value));
	}
	
}
	