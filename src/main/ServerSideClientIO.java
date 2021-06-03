package main;

import java.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import data.ClypeData;
import data.MessageClypeData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
/**
 * @author Qifeng Wu and Johnny Magnuson
 * 
 * This class happens to handle receiving the data from client and
 * sending it back to the client
 *  
 */


public class ServerSideClientIO implements Runnable {
	private boolean closeConnection;
	private ClypeData dataToReceiveFromClient;
	private ClypeData dataToSendToClient;
	private ObjectInputStream inFromClient;
	private ObjectOutputStream outToClient;
	private ClypeServer server;
	private Socket clientSocket;
	private ClypeData friendsListToSendToClients;
	private ObservableList <ServerSideClientIO> observableList; 


	/*Constructor 1: Default constructor*/
	ServerSideClientIO(ClypeServer server, Socket clientSocket){
		this.server = server;
		this.clientSocket = clientSocket;
		this.closeConnection = false;
		this.dataToReceiveFromClient = null;
		this.dataToSendToClient = null;
		this.inFromClient = null;
		this.outToClient = null;
		this.observableList = FXCollections.observableArrayList(server.getServerSideClientIO());
		
	}
	/*Accessor for dataToReceiveFromClient*/
	
	public ClypeData getDataToReceiveFromClient() {
		
			return this.dataToReceiveFromClient;
		
	}
	
	@Override
	
	/*Method that receives the data from client and send data to client(s)*/
	public void run() {
		
		
		try {
			this.outToClient = new ObjectOutputStream(clientSocket.getOutputStream());
			this.inFromClient = new ObjectInputStream(clientSocket.getInputStream());
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		System.out.println("DEBUG INFO: connection closed? "+ this.closeConnection);
				
			while(this.closeConnection != true) {
				
				/*while the connection is not closed, receiving the data from client and send
				 * the data to specific client or all clients*/
				
				receiveData();
						
				if(this.dataToReceiveFromClient.getType() == 1 ) {
					//If client tries to logout, this echoes back "DONE" to corresponding client
					this.dataToSendToClient = this.dataToReceiveFromClient;
					
					for(int x = 0 ; x < this.server.serverSideClientIOList.size(); x++) {
						if(this.dataToReceiveFromClient.getUserName() ==
								(this.server.serverSideClientIOList.get(x).getDataToReceiveFromClient().getUserName()))
							this.server.serverSideClientIOList.get(x).sendData();
					}
							
				}else if(this.dataToReceiveFromClient.getType() == 0 ){
					// If client tries to list all users' names, this echoes back all users' names to the corresponding client
					
					this.dataToSendToClient = new MessageClypeData(this.dataToReceiveFromClient.getUserName(),
							this.server.getAllUserName(),0);
					
					for(int x = 0 ; x < this.server.serverSideClientIOList.size(); x++) {
						if(this.dataToReceiveFromClient.getUserName()==
								(this.server.serverSideClientIOList.get(x).getDataToReceiveFromClient().getUserName())) {
							this.server.serverSideClientIOList.get(x).sendData();
						}
					}
					
				}else if(this.dataToReceiveFromClient.getType() == 5) {
					
					// If client logged out, server will send  a new friend list to all online clients
					 this.dataToSendToClient = new  MessageClypeData("Server",server.getAllUserName(),5);
					 this.server.broadcast(this.dataToSendToClient);
					 
				}else {
					// If client tries to send a message, this echoes back the message to all clients
					this.dataToSendToClient = this.dataToReceiveFromClient;
					this.server.broadcast(this.dataToSendToClient);
				}
				
			}
	} 
		
	
	public void sendData() {
		try {
			
			this.outToClient.writeObject(this.dataToSendToClient);
			this.outToClient.flush();
			System.out.println("Data to send to the client is: " );
			System.out.println( this.dataToSendToClient );
			

			if(this.dataToSendToClient.getType() == 1) {
				//If Log out, remove the corresponding client
				System.out.println("Server is disconnected with the client "+
									this.dataToSendToClient.getUserName());
				ServerSideClientIO serverSideClientToRemove = null;
				
				for(int x = 0 ; x < this.server.serverSideClientIOList.size(); x++) {
					
					if(this.dataToSendToClient.getUserName()==
							(this.server.serverSideClientIOList.get(x).getDataToReceiveFromClient().getUserName())) {
						serverSideClientToRemove = this.server.serverSideClientIOList.get(x);
					}
					
				}
			
				this.server.remove(serverSideClientToRemove);	
				this.dataToSendToClient = new MessageClypeData("Server",server.getAllUserName(),5);
				this.server.broadcast(this.dataToSendToClient);
				this.closeConnection = true;
				
			}
			
			
		}catch(IOException ioe) {
			ioe.printStackTrace();
			
		}
	}
	
	/*Mutator for the dataToSendToClient */
	public void setDataToSendToClient(ClypeData dataToSendToClient) {
		this.dataToSendToClient = dataToSendToClient;
	}
	
	public void receiveData() {
		try {
			
				this.dataToReceiveFromClient = (ClypeData)inFromClient.readObject();
					
				
				System.out.println("----------------------------------");
				System.out.println("Data Received from client "+ this.dataToReceiveFromClient.getUserName()+":");
				System.out.println(this.dataToReceiveFromClient.getData());
				System.out.println("----------------------------------");
				
						
		} catch(IOException ioe) {
			
				System.err.println("IO Exception occurred while reading from client");
		
		}catch (ClassNotFoundException cnfe) {
			System.err.println("Class not found");
		}catch (NullPointerException npe) {
			npe.printStackTrace();
		}
	}
	
	
	
	
	
	

}
