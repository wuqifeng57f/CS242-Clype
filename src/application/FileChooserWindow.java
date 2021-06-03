package application;
/**
 * @author Qifeng Wu and Johnny Magnuson
 * 
 *This class that is able to open file chooser window ; In this case, user should open the picture
 *
 */
import java.io.File;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FileChooserWindow {
	
	File file; // a file variable
	
	/*Method that return the file name*/
	public File getFile() {
		return file;
	}
	
	/*Method to open the file*/
	public void open() {
		
		FileChooser fc = new FileChooser();
		
		fc.setTitle("Open a picture");
		fc.getExtensionFilters().addAll(
	                new FileChooser.ExtensionFilter( "Image File",
	                								 "*.jpg",
	                								 "*.png") 
	                );
		 file = fc.showOpenDialog(null);
		 
	
	}
}
