package application;
/**
/**
 * @author Qifeng Wu and Johnny Magnuson
 * 
 * This class acts as a creator for the minimize button and close button on the title bar
 *  
 */

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ToolBarButtons extends HBox {

	private ClypeApp clypeApp_;  // create a instance variable of ClypeApp
	private Button button_minimize = new Button("_"); // a minimize button 
	private Button button_close = new Button("X"); // a close button
	
		/* Constructor 1 */
		public ToolBarButtons(Stage primaryStage) {
			 
	            button_minimize.setStyle(" -fx-background-color: darkgreen;-fx-text-fill: yellow");
	            
	            button_minimize.setOnAction(new EventHandler<ActionEvent>() {
	            	
	            	@Override
	            	public void handle(ActionEvent actionEvent) {
	                     primaryStage.setIconified(true);
	            	}
	           
	            });
	             
	          
	            button_close.setStyle(" -fx-background-color: darkgreen; -fx-text-fill: yellow");
	            
	           
	            button_close.setOnAction(new EventHandler<ActionEvent>() {
	                    	
	               @Override
	               public void handle(ActionEvent actionEvent) {
	            	   
	            	   primaryStage.close();
	                   
	               }
	           });
	          
	            this.getChildren().addAll(button_minimize,button_close);
		}
	
		/* Constructor 2 */
        public ToolBarButtons(Stage primaryStage,ClypeApp clypeApp) {
        	
            clypeApp_ = clypeApp;
            
            button_minimize.setStyle(" -fx-background-color: darkgreen;-fx-text-fill: yellow");
            
            button_minimize.setOnAction(new EventHandler<ActionEvent>() {
            	
            	@Override
            	public void handle(ActionEvent actionEvent) {
                     primaryStage.setIconified(true);
            	}
           
            });
             
            button_close.setStyle(" -fx-background-color: darkgreen; -fx-text-fill: yellow");
            
           
            button_close.setOnAction(new EventHandler<ActionEvent>() {
                    	
               @Override
               public void handle(ActionEvent actionEvent) {
            	   
            	   if(clypeApp_.getClient() == null || clypeApp_.getClient().isConnectionTrue() == false) {
            		   Platform.exit();
            	   }else {
            		   Platform.exit();
            		   clypeApp_.getClient().sendDoneToServer();
            	   }
                   
               }
           });
          
            this.getChildren().addAll(button_minimize,button_close);
            
        }
        
   }

