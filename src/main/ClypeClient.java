package main;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Scanner;

import application.ClypeApp;
import data.ClypeData;
import data.FileClypeData;
import data.MessageClypeData;
import javafx.concurrent.Task;

/**
 * @author Qifeng Wu and Johnny Magnuson
 * 
 * This class happens to act as a client that hold
 * the user name, host name and the port number,
 * along with the status of the connection
 * and the data send to the server and receive from the server.
 *  
 */


public class ClypeClient{

	private String userName; // name of the user
	private String hostName; // name of the host
	private int port; // port number on server connected to
	public boolean closeConnection; // connected or not
	private ClypeData dataToSendToServer; // As the name described
	private ClypeData dataToReceiveFromServer; // As the name described
	private Scanner inFromStd; //Scan for user input
	private final String key = "apple"; // a constant key
	private ObjectInputStream inFromServer; // to receive data from the server
	private ObjectOutputStream outToServer;// to send data to the server
	private ClypeClient clypeClientLock = this;	
	private ClypeApp clypeApp;
	private String input = "";
	private boolean isConnectionFormed = false;
	private ClypeData notifyServer;

	/* Constructor 1 */
    public ClypeClient(String userName, String hostName, int port, ClypeApp clypeApp){
    
			this.userName = userName;
			this.hostName = hostName;
			this.port = port;
			this.closeConnection = false;
			this.dataToSendToServer = null;
			this.dataToReceiveFromServer = null;
			this.inFromServer = null;
			this.outToServer = null;
			this.clypeApp = clypeApp;
		
			
			if(this.userName == null)
				throw new IllegalArgumentException("Invalid User Name");
			if(this.hostName == null)
				throw new IllegalArgumentException("Invalid Host Name");
	    	if(this.port < 1024)
	    		throw new IllegalArgumentException("Invalid Port Number");
    	
	}
    
    /* Constructor 2 */
    public ClypeClient(String userName,String hostName,ClypeApp clypeApp){
    	this(userName,hostName,7000,clypeApp);	
    }
    
    /* Constructor 3 */
    public ClypeClient(String userName,ClypeApp clypeApp){
    	this(userName,"localhost",clypeApp);   	
    }
    
    /* Constructor 4: Default Constructor */
    public ClypeClient(ClypeApp clypeApp) {
    	this("anonymous user",clypeApp);
    }
    public ClypeData getDataToReceiveFromServer() {
    	return this.dataToReceiveFromServer;
    }
    public ClypeClient getClypeClientLock() {
  
    		return this.clypeClientLock;
    	
    }
    public ClypeApp getClypeApp() {
    	return clypeApp;
    }
    public void sendDoneToServer() {
    	 this.dataToSendToServer = new MessageClypeData(this.userName,"DONE",1);
    	 sendData();
    }
    public void sendLeavingStatusToServer() {
    	this.dataToSendToServer = new MessageClypeData(this.userName,"leaving",5);
    } 
   
    public boolean isConnectionTrue() {
    	return isConnectionFormed;
    }
    public ObjectOutputStream getObjectOutputStream() {
    	return outToServer;
    }
    /* Method that starts the client's communication with the server*/
    public void start() {
    	
    	
    	this.inFromStd = new Scanner(System.in);
    	
    			try {
    				
    				System.out.println("Client trying to connect...");
					Socket socket = new Socket(hostName,port);
					System.out.println("Connection established with " + hostName +
										" at port " + port );
					isConnectionFormed = true;
	    			outToServer = new ObjectOutputStream(socket.getOutputStream());
					inFromServer = new ObjectInputStream(socket.getInputStream());
					
					sendData();
					
					ClientSideServerListener clientSideServerListenerRunnable = new ClientSideServerListener(this);
					Thread clientSideServerListenerThread = new Thread(clientSideServerListenerRunnable);
					
					
					Task <Void> task = new Task<Void>() {
						
						@Override protected Void call() throws Exception{

							clientSideServerListenerThread.start();
    	    				while(closeConnection != true) {
    	    					
    	    					
    	    					
    	    					if(closeConnection == true) {
    	    						
    	    						break;
    	    					}
    	    					
    	    					synchronized(clypeClientLock) {
    	    						
    	    						notifyServer = new MessageClypeData(userName,"notify",5);
        	    					outToServer.writeObject(notifyServer);
        	    					outToServer.flush();
        	    					
    	    						clypeClientLock.wait();
    	    					}
    	    					
    			    			readClientData();
    			    			sendData();

    			    		}
    				
    						if(closeConnection == true) {
    							
    	    					outToServer.flush();
    							outToServer.close();
    							inFromServer.close();
    							socket.close();
    						}
    						return null;
						}
					};
					
					
					Thread t = new Thread(task);
					t.start();
							
					
		    	}catch(UnknownHostException uhe) {
		    		System.err.println("Host is unknown");
		    	}catch(NoRouteToHostException nrthe){
		        	System.err.println("Route cannot be found");
		        }catch(ConnectException ce) {
		        	System.err.println("Connection failed");
		        }catch(IOException ioe) {
		        	System.err.println("IO issue; port number is already in use");
		       }catch(NullPointerException npe) {
		        	System.err.println("Null pointer");
		       }
					
    }
    
    /* Method that read from Client input*/
    public void readClientData() throws IOException {
    		
    	String filename = "";
    	
    	
	    	if(closeConnection!=true) {
	    		
	    		input = clypeApp.sendInput();
	    	}
    	
    	
    	/*Way to get the name of the file when client
    	 * inputs "SENDFILE<filename>" */
    	if(input.length()>= 8) {
    		if(input.substring(0,8).equals("SENDFILE")){
			
    			Scanner scan = new Scanner(input).useDelimiter("SENDFILE<");
    			Scanner scan2 = new Scanner(scan.next()).useDelimiter(">");
    			try {
    				filename = scan2.next();
    			}catch(NoSuchElementException nsee) {
    				System.err.println("No filename inside the <>");
    			}
    			scan.close();
    			scan2.close();
    		}
    	}
    	
    	
    	if(input.equals("DONE")) {
    		
    		/*if the input is "DONE", data to send to server will be "DONE"*/
    		this.dataToSendToServer = new MessageClypeData(this.userName,input,1);
    		
    		
    		
    	}else if(input.equals("SENDFILE<"+filename+">")){
    		
    		/*if the input is "SENDFILE<filename>", we access the file name provided
    		 * by the users, and then start reading contents in the file if the file
    		 * is openable */
    			this.dataToSendToServer = new FileClypeData(this.userName,filename,2);
    			
    		try {
    			((FileClypeData) this.dataToSendToServer).readFileContents();
    		}catch(FileNotFoundException fnfe) {
    			System.err.println(fnfe.getMessage());
    		}catch(IOException ioe){
    			System.err.println(ioe.getMessage());
    		}
    		
    	}else if(input.equals("LISTUSERS")) {
    		
    		/*if the input is "LISTUSERS", data to send to server will be "LISTUSERS"*/
    		
    		this.dataToSendToServer = new MessageClypeData(this.userName,input,0);
    		
    	}else {
    		/*Other than the cases above, we will set type to 3(Send a Message);
    		 * the input is the message */
    		this.dataToSendToServer = new MessageClypeData(this.userName,input,3);
    	}
    	
 	
    }
   
  
    /*Method to print out meaningful real-time data to Client*/
    public String printData() {

    	if(this.closeConnection) {
    	
    		return "You have successfully logged out on "+ new Date();
    	}else {
    	
    		return this.dataToReceiveFromServer.getData();
    	}
    	
    }
    
    /*Method that sends the data to the server*/
    public void sendData() {
    	try {
    			if(this.dataToSendToServer == null) {
    				this.dataToSendToServer = new MessageClypeData(this.userName,this.userName + " is connected to the server",4);
    				this.outToServer.writeObject(this.dataToSendToServer);
    				this.outToServer.flush();
    				
    			}else if(this.closeConnection != true) {
    				this.outToServer.writeObject(this.dataToSendToServer);
    				this.outToServer.flush();
    			}
    
    	}catch(IOException ioe){
    		System.err.println("IO Exception occurred while sending data to server");
    	}
    }
    
    /*Method that receives the data from the server*/
    public void receiveData() {
    	try {
    		
    		if(this.closeConnection != true) {
	    		this.dataToReceiveFromServer = (ClypeData)inFromServer.readObject();
	    		
				if(this.dataToReceiveFromServer.getType() == 1) {
    				this.closeConnection = true;
    			}
    		}
    		
    	
    		
    	}catch(IOException ioe) {
    		ioe.printStackTrace();
    	}catch(ClassNotFoundException cnfe) {
    		System.err.println("Class not found");
    	}catch(NullPointerException npe) {
    		npe.printStackTrace();
    	}
    }
    
    /* Accessor for the name of the user */
    public String getUserName() {
    	return this.userName;
    }
    
    /* Accessor for the name of the host*/
    public String getHostName() {
    	return this.hostName;
    }
    
    /* Accessor for the port number */
    public int getPort() {
    	return this.port;
    }
    
    @Override
    
    /* Method to set up a unique value for object and return it */
    public int hashCode() {
    	int result = 17;
    		result = 37 * result + this.port + this.userName.length();
    			
    		return result;
    }
    
    /* Method to compare two object's equality */
    public boolean equals(Object other) {
    	ClypeClient otherClypeClient = (ClypeClient) other;
    	
    	if(!(other instanceof ClypeClient)) {
    		return false;
    	}
    	
    	if(dataToSendToServer == null) {
    		return false;
    	}
    	if(dataToReceiveFromServer == null) {
    		return false;
    	}
    	
    	return this.userName == otherClypeClient.userName &&
    		   this.hostName == otherClypeClient.hostName &&
    		   this.port == otherClypeClient.port &&
    		   this.closeConnection == otherClypeClient.closeConnection &&
    		   this.dataToSendToServer == otherClypeClient.dataToSendToServer &&
    		   this.dataToReceiveFromServer == otherClypeClient.dataToReceiveFromServer;
    }
    
    /* Method to return a full description of the class with all instance variables */
    public String toString() {
    	return "User name is " + this.userName +
    			"\nHost name is " + this.hostName + 
    			"\nPort is " + this.port +
    			"\nConnection closed? " + this.closeConnection +
    			"\nData that send to the server is " + this.dataToSendToServer +
    			"\nData that receive from the server is " + this.dataToReceiveFromServer;
    }
    
    
   
}
