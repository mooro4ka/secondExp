package nsida.kazey.showcase.view;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class FooterEvents {
    public static class OnMouseReleased implements EventHandler<MouseEvent>{    
        
        private ShowcaseController sc = null;
        private int direction = 0;
        
        public OnMouseReleased(ShowcaseController dsc, int direction){
            this.sc = dsc;
            this.direction = direction;
        }
        
        @Override
        public void handle(MouseEvent event) {
            int currentPage = this.sc.getCurrentPage();

            switch(this.direction){
                case 1: if (currentPage < this.sc.getTotalPages()){
                    currentPage++;
                    sc.setCurrentPage(currentPage); break;
                }
                case -1: if(currentPage > 0) {
                    currentPage--;
                    sc.setCurrentPage(currentPage); break;
                }
                case 0: {
                    sc.initShowcase();
                    sc.fillHeader(null); break;
                }
            }            
            sc.fillShowcase();
        }
    }

}
