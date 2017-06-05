package nsida.kazey.showcase.view;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import nsida.kazey.showcase.model.Items;

public class ShowcaseEvents {

	public static class OnMouseReleased implements EventHandler<MouseEvent>{
	        
        private VBox btn = null;
        private Items src = null;
        private ShowcaseController sc = null;
        
        public OnMouseReleased(VBox btn, Items src, ShowcaseController sc) {
            this.btn = btn;
            this.src = src;
            this.sc = sc;
        }
        
        @Override
        public void handle(MouseEvent event) {            
           
        	this.btn.setScaleX(1);
            this.btn.setScaleY(1);
                     	            
            if (!src.isGroup()) { 
        		if (sc.isWeightStable() & (sc.getCurrentWeightFloat()!=0)) {
        			sc.printLabel(src);
        			sc.initShowcase();
        			sc.fillHeader(null);
        	 		sc.fillShowcase();
        		} else {
        			sc.fillHeader(src);
            	}
            } else {
            	sc.initShowcase();
            	sc.fillHeader(src);
            	sc.fillShowcase();
            } 
            
        }   
    }
	    
	public static class OnMousePressed implements EventHandler<MouseEvent>{

        private VBox btn = null;
        
        public OnMousePressed(VBox btn){
            this.btn = btn;
        }
        @Override
        public void handle(MouseEvent event) {
            btn.setScaleX(1.05);
            btn.setScaleY(1.05);
        }   
        
    }
	
}
