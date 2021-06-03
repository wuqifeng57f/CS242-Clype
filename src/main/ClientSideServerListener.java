package main;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;


/**
 * @author Qifeng Wu and Johnny Magnuson
 * 
 * This class happens to receive the data from the server and 
 * print it to the client
 *  
 */

public class ClientSideServerListener implements Runnable {
	private ClypeClient client;
	
	
	/*Constructor 1: Default Constructor*/
	ClientSideServerListener(ClypeClient client){
		this.client = client;
	}
	
	@Override
	/*Method that receives the data from server and prints the data received to client*/
	public void run() {
		
		
			
				while(this.client.closeConnection != true) {
			
					/*while connection is not closed, receiving and displaying the message and friends list*/
				
					this.client.receiveData();
			
					if(this.client.getDataToReceiveFromServer().getType() == 1) {
						
						this.client.closeConnection = true;
						
					}
					
					if(this.client.getDataToReceiveFromServer().getType() == 5) {
						
	    				this.client.getClypeApp().setTextArea_friends(this.client.printData());
	    				
	    			}else if(this.client.getDataToReceiveFromServer().getType() == 6){
	    				
	    				//convert ByteArray to BufferedImage
	    				InputStream in = new ByteArrayInputStream(this.client.getDataToReceiveFromServer().getByteArray());
	    				BufferedImage bufferedImage = null;
						try {
							bufferedImage = ImageIO.read(in);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	    				this.client.getClypeApp().setBufferedImage(bufferedImage);
	    				this.client.getClypeApp().addUserName(this.client.getDataToReceiveFromServer().getUserName());
	    				this.client.getClypeApp().addBufferedImage(bufferedImage);
	    				
	    			
	    				this.client.getClypeApp().setTextArea_message(this.client.getDataToReceiveFromServer().getUserName()+
	    						" sent a picture. Click View Picture to view it\n");
	    				
	    			}else if(this.client.getDataToReceiveFromServer().getType() == 3){
	    				this.client.getClypeApp().setTextArea_message(this.client.getDataToReceiveFromServer().getUserName()+ ": " + this.client.printData() + "\n");
	    					//Set users message
	    					
	    			}else if(this.client.getDataToReceiveFromServer().getType() == 4) {
	    				this.client.getClypeApp().setTextArea_message(this.client.printData()+"\n");
	    					//Set login message
	    			
	    			}else {
	    				this.client.getClypeApp().setTextArea_message(this.client.printData()+"\n");
	    			}
	    				
	    			
	    				
				
			    }

		
	}
	
}
