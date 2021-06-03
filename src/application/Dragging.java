package application;
/**
 * @author Qifeng Wu and Johnny Magnuson
 * 
 * This class allows the title bar to be draggable
 *  
 */
import javafx.event.EventHandler;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Dragging extends HBox{
	
	double x; // x coordinate
	double y; // y coordinate
	
	/* Default Constructor */
	public Dragging(Stage primaryStage,ToolBar toolBar) {

		/*toolBar's response to mouse press*/
		toolBar.setOnMousePressed(new EventHandler<MouseEvent>() {
			
			@Override 
			public void handle(MouseEvent me) {
   
			x = primaryStage.getX() - me.getScreenX();
			y = primaryStage.getY() - me.getScreenY();
			
			}
		});
		
		/*toolBar 's response to mouse dragging*/
		toolBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
			
			@Override
			public void handle(MouseEvent me) {
				primaryStage.setX(me.getScreenX() + x);
				primaryStage.setY(me.getScreenY() + y);
				}
		});
		
	}
}
