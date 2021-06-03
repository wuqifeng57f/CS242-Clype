
/**
 * @author Qifeng Wu & Johnny Magnuson
 * 
 * This class is able to view picture
 * 
 */
package application;

import java.awt.image.BufferedImage;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PictureView extends Application{
//	private static final String url = "file:/D:/Java CS242/Projects/Clype2.0/image3.jpg";
	
	public BufferedImage bufferedImage; // store the image;
	
	/* Default constructor */
	public PictureView(BufferedImage bufferedImage) {
		this.bufferedImage = bufferedImage;
	}
	
	/* Method that create the new style of title bar*/
	private void makeTitleBar(Stage primaryStage, AnchorPane root) {
		ToolBar toolBar = new ToolBar();
		ToolBarButtons toolBarButtons = new ToolBarButtons(primaryStage);
		toolBar.setPrefHeight(32);
        toolBar.setMinHeight(32);
        toolBar.setMaxHeight(32);
        toolBar.setStyle(" -fx-background-color:transparent" );
        toolBar.getItems().add(toolBarButtons);
        toolBarButtons.setOpacity(0.5);
        toolBar.getItems().add(new Dragging(primaryStage,toolBar));
        
        root.getChildren().add(toolBar);  
        AnchorPane.setRightAnchor(toolBar, 0.0);
        AnchorPane.setLeftAnchor(toolBar, 0.0);
	}
	
	/*Start the GUI of picture viewer*/
	@Override
	public void start(Stage stage) {
		
		stage.initStyle(StageStyle.UNDECORATED);
		AnchorPane root = new AnchorPane();
		
		Image image = SwingFXUtils.toFXImage(bufferedImage, null);
		
		ImageView imageView = new ImageView();
		imageView.setImage(image );
		imageView.setPreserveRatio(true);
		
        Scene scene = new Scene(root);
        
        root.getChildren().add(imageView);
        makeTitleBar(stage,root);
        stage.setTitle("ImageView");
        
        stage.setScene(scene); 
        stage.sizeToScene(); 
        stage.show(); 
    }
	
	/**
	 * The main function
	 * 
	 * Launch the GUI
	 * 
	 * @param args command line arguments
	 *  
	 */
    public static void main(String[] args) {
        launch(args);
    }
}