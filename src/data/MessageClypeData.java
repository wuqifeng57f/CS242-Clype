package data;
/**
 * @author Qifeng Wu and Johnny Magnuson
 * 
 * This class implement a container that
 * stores message from the user
 */


public class MessageClypeData extends ClypeData {
	
	private String message; // Message from the users
	
	/* Constructor 1 */
	public MessageClypeData(String userName,String message, String key, int type){
		super(userName,type);
		this.message = super.encrypt(message, key);
	}
	/* Constructor 2 */
	public MessageClypeData(String userName, String message, int type) {
		super(userName,type); 
		this.message = message;
	}
	
	
	/* Constructor 3: Default Constructor */
	public MessageClypeData(){
		super();
		this.message = "Empty Message";
	}
	
	/* Accessor for the message from the user */
	public String getData() {
		return this.message;
	}
	
	@Override
	/* Method to set up a unique value for object and return it */
	public int hashCode() {
		
		int result = 19;
			result = 37 * result + this.message.length();
		
			return result;
	}
	
	/* Method to compare if two object are equal */
	public boolean equals(Object other) {
		MessageClypeData otherMessageClypeData = (MessageClypeData) other;
		
		if(!(other instanceof MessageClypeData)) {
			return false;
		}
			return this.message == otherMessageClypeData.message;
	}
	
	/* Method to return a full description of the class with all instance variables */
	public String toString() {
		return "Type " + this.getType()
				+"\nFrom user: " + this.getUserName()+ " on " + this.getDate()
				+"\nThe message: " + this.message;
	}
	
	/* Method to return a decrpyted string of message */
	public String getData(String key) {
		return super.decrypt(this.message, key);
	}

}
