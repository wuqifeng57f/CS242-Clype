package main;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import data.ClypeData;

/**
 * @author Qifeng Wu and Johnny Magnuson
 * 
 * This class acts as a server that storing the port number,
 * the status of the connection, and the data that
 * receive from the client and send to the client.
 * 
 */

public class ClypeServer {
	private int port; // port number on server connected to
	public boolean closeConnection; // connected or not?
	public ArrayList <ServerSideClientIO> serverSideClientIOList;
	private ClypeServer clypeServerLock = this;

	/* Constructor 1 */
	public ClypeServer(int port){
		this.port = port;
		this.closeConnection = false;
		this.serverSideClientIOList = new ArrayList<ServerSideClientIO>();
		
	
    	if(this.port < 1024)
    		throw new IllegalArgumentException("Invalid Port Number");
	}
	
	/* Constructor 2: Default Constructor */
	public ClypeServer(){
		this(7000);
		
	}
	public ArrayList <ServerSideClientIO> getServerSideClientIO(){
		return serverSideClientIOList;
	}

	/* Method that starts the server's communication with client */
	public void start() {
		

		
		try {
			System.out.println("Waiting for client's request...");
			
			ServerSocket serverSocket = new ServerSocket(this.port); // Open a server socket
			
			while(this.closeConnection != true) {
				
				Socket clientSocket = serverSocket.accept(); // Accept the server socket
				System.out.println("Connection established with the client at port " + this.port);
				
				ServerSideClientIO serverSideClientIO = new ServerSideClientIO(
															this,clientSocket);
				this.serverSideClientIOList.add(serverSideClientIO);
			
				
				Thread serverSideClientIOThread = new Thread(serverSideClientIO);
				
				serverSideClientIOThread.start();
				
				
			}
			
			
			/*if(this.closeConnection == true) {
				serverSocket.close();
			}*/
			
		}catch(IOException ioe) {
			System.err.println("IO Exception occurred; port number is already in use."); 
		}
		
	}
	
	public ClypeServer getClypeServerLock() {
		return this.clypeServerLock;
	}
	/*Method to get all users' name from the serverSideClientIOList*/
	public String getAllUserName() {
		
			String allUserName = "";
			ServerSideClientIO serverSideClientIO;
			for(int x = 0; x < serverSideClientIOList.size(); x++) {
				serverSideClientIO = serverSideClientIOList.get(x);
				if(x == 0) {
					allUserName = serverSideClientIO.getDataToReceiveFromClient().getUserName();
				}else {
					allUserName = allUserName + "\n" + serverSideClientIO.getDataToReceiveFromClient().getUserName();
				}
			}
			return allUserName;
		
	}
	
	/*send data to every client within the serverSideClientIOList*/
	public synchronized void broadcast(ClypeData dataToBroadcastToClients) {
		
		ServerSideClientIO serverSideClientIO;
		
		for(int x = 0; x < serverSideClientIOList.size(); x++) {
			serverSideClientIO = serverSideClientIOList.get(x);
			serverSideClientIO.setDataToSendToClient(dataToBroadcastToClients);
			serverSideClientIO.sendData();
		}
	
	}
	
	/*Remove the stated client from the serverSideClientIOList*/
	public synchronized void remove(ServerSideClientIO serverSideClientToRemove) {
	
			this.serverSideClientIOList.remove(serverSideClientToRemove);
		//	this.observableList.remove(serverSideClientToRemove);
	
	}
	
	/*Accessor for the port number*/
	public int getPort() {
		return this.port;
	}
	
	@Override
	
	/* Method to set up a unique value for object and return it */
	public int hashCode() {
		int result = 23;
			result = 37 * result + this.port;
			
			return result;
	}
	
	/* Method to compare two object's equality */
	public boolean equals(Object other) {
		
		ClypeServer otherClypeServer = (ClypeServer) other;
		
		if(!(other instanceof ClypeServer)) {
			return false;
		}
		
    	
		return this.port == otherClypeServer.port &&
			   this.closeConnection == otherClypeServer.closeConnection;
		
	}
	
	/* Method to return a full description of the class with all instance variables */
	public String toString() {
		return "The port is " + this.port
				+ "\nConnection closed(" + this.closeConnection + ")";
			
	}
	
	
	
	
	/**
	 * The main function
	 * 
	 * @param args command line arguments
	 *  
	 */
	public static void main(String []args) {
	
		String input = "";
		for(String s: args) {
			input = s;
		}
	//	input = "1998";
		int portNumber = 0;
		
			ClypeServer clypeServer = new ClypeServer();
			
			
			if(input.isEmpty()) {
				/*If the user input in the command line is empty, clypeServer initializes
				 * with the default constructor. */
				clypeServer = new ClypeServer();
			}
			
			if(!input.isEmpty()) {
				/* If the user input in the command line is not empty, we will firstly try to convert
				 * the string into integer and then initialize the clypeServer with the
				 * constructor taking the argument portNumber
				 * */
				try {				
				
					portNumber = Integer.parseInt(input);
	
					try {
							clypeServer = new ClypeServer(portNumber);
						
					//	System.out.println("The port number is " + clypeServer.getPort());
							
					}catch(IllegalArgumentException iae) {
						System.err.println(iae.getMessage());
					}
				
				}catch(NumberFormatException nfe) {
					System.err.println("Port Number should be integer");
				}
			}
			
	
			clypeServer.start();
			
		
	
	
	/*	int portNumber = 2000;
		
			
		ClypeServer clypeServer = new ClypeServer(portNumber);
			clypeServer.start();*/
			
		
	}
}
