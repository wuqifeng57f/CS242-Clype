/**
 * @author Qifeng Wu and Johnny Magnuson
 * 
 * This class implements a container that store the image in byte array;
 * 
 */

package data;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class PictureClypeData extends ClypeData{
	
	private byte[] imageInByte; //image in byte
	private File file; // a file
	String extension = ""; // extension of the file type
	
	/*Default constructor*/
	public PictureClypeData(String userName, File file, int type) {
		
		super(userName,type);
		this.file = file;
	}
	/*Method that return the extension of the file type*/
	public String getData() {
		return extension;
	}
	/*Convert the image into byte*/
	public void createImageInByte() {
		
		try {
			BufferedImage bufferedImage; 
			bufferedImage = ImageIO.read(file);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			extension = file.getName().substring(file.getName().length()-3,file.getName().length());
			System.out.println(extension);
			ImageIO.write(bufferedImage, extension, buffer);
			buffer.flush();
			imageInByte = buffer.toByteArray();
			buffer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*Method that return the image in byte array*/
	@Override
	public byte[] getByteArray() {
		return imageInByte;
	}
	
	/*This is a empty method; the only reason it is here is because we extends ClypeData and we need to implement the abstract method*/
	public String getData(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
}
