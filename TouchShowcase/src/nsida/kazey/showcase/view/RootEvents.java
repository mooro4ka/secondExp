package nsida.kazey.showcase.view;

import java.util.Timer;
import java.util.TimerTask;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.stage.Screen;

public class RootEvents {
		
	public static class OnMouseClicked implements EventHandler<MouseEvent> {
		
		private ShowcaseController sc;
		private int clickCounter;
		private Timer clickTimer;
		
		private ScreenSquare firstSquare;
		private ScreenSquare secondSquare;
		private ScreenSquare thirdSquare;
		private ScreenSquare fourthSquare;
		
		public OnMouseClicked(ShowcaseController sc) {
			
			this.sc = sc;
			
			double screenHeight = Screen.getPrimary().getBounds().getHeight();
			double screenWidth = Screen.getPrimary().getBounds().getWidth();
			
			clickCounter = 0;
			
			firstSquare = new ScreenSquare(0, 
					0, 
					screenWidth * 0.5, 
					screenHeight * 0.5);
			secondSquare = new ScreenSquare(screenWidth - screenWidth * 0.5,
					0, 
					screenWidth, 
					screenHeight * 0.5);
			thirdSquare = new ScreenSquare(screenWidth - screenWidth * 0.5,
					screenHeight - screenHeight * 0.5, 
					screenWidth, 
					screenHeight);
			fourthSquare = new ScreenSquare(0, 
					screenHeight - screenHeight * 0.5,
					screenWidth * 0.5, 
					screenWidth);
		}
		
		@Override
		public void handle(MouseEvent event) {
			
			if (firstSquare.isPointInSquare(event.getX(), event.getY())) {
				clickCounter = 1;
				
				if (clickTimer != null) {
					clickTimer.cancel();
				}
				
				clickTimer = new Timer();
				clickTimer.schedule(new TimerTask() {					
					@Override
					public void run() {
						clickCounter = 0;
					}					
				}, 5000);
			} else if ((secondSquare.isPointInSquare(event.getX(), event.getY()))
					&& (clickCounter == 1)) {
				clickCounter++;
			} else if ((thirdSquare.isPointInSquare(event.getX(), event.getY()))
					&& (clickCounter == 2)) {
				clickCounter++;
			} else if ((fourthSquare.isPointInSquare(event.getX(), event.getY())) 
					&& (clickCounter == 3)){
				clickCounter = 0;
				if (clickTimer != null) {
					clickTimer.cancel();
					clickTimer = null;
				}
				sc.getMainApplication().setSettingsModeScene();
			}			
		}		
	}
	
	public static class ScreenSquare {
		
		private double x0, y0, x1, y1; 
		
		public ScreenSquare(double x0, double y0, double x1, double y1) {
			
			this.x0 = x0;
			this.y0 = y0;
			this.x1 = x1;
			this.y1 = y1;
			
		}
		
		public boolean isPointInSquare(double x, double y) {
						
			return (x>=this.x0 && x<=this.x1 && y>=this.y0 && y<=this.y1);
			
		}
	}
	
	public static class OnTouchPressed implements EventHandler<TouchEvent> {

		@Override
		public void handle(TouchEvent event) {
			event.consume();			
		}
		
	}
}
