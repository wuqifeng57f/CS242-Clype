package data;
import java.io.Serializable;
import java.util.Date;
/**
 * @author Qifeng Wu and Johnny Magnuson
 * 
 * This class implement a container that
 * holds the name of the user, the type of
 * the connection and the date when it happens.
 * 
 */
public abstract class ClypeData implements Serializable {
	
	private String userName; // name of the user
	private final int type;	   /* type of the connection
								0: give a listing of all users to connected to this session
								1: log out
								2: send a file
								3: send a message
								4: log in 
								5: show friend list
								6: picture view
							   */
	private Date date; // the date when object is created
	 
	/* Constructor 1 */
	ClypeData(String userName,int type){
		this.userName = userName;
		this.type = type;
		date = new Date();
		
	}

	/* Constructor 2 */
	ClypeData(int type){
		this("Anon",type);
	}
	
	/* Constructor 3: Default Constructor */
	ClypeData(){
		this(0);
	}
	
	/* Accessor for the type of the connection of the user */
	public int getType() {
		return this.type;
	}
	
	/* Accessor for the name of the user */
	public String getUserName() {
		return this.userName;
	}
	
	/* Accessor for the date */
	public Date getDate() {
		return this.date;
	}
	
	/* Accessor for the all data contained in the this class */
	public String getData() {
		return this.userName + " " + "Type " + this.type + " "+ this.date; 
	}
	
	/* This method encrypts the input string by using a key, and return a encrypted string */
	protected String encrypt(String inputStringToEncrypt, String key) {
		
		
		String encryptedString = "";
		
		for(int stringIndex =0,keyIndex=0; stringIndex < inputStringToEncrypt.length(); stringIndex++) {
			
			int indexOfCharAtInputString = 0; // number of position of the characters at input string
			int indexOfCharAtKey = 0;// number of position of the key
			
			char charAtInputString = inputStringToEncrypt.charAt(stringIndex); // character at input string by index
			char charAtKey = key.charAt(keyIndex); // character at key by index
			
			boolean containLetter = true;
			
			/* Check if the characters at input string are all character. If not, they will remain the same */
			if ((Character.toUpperCase(charAtInputString) >= 'A') && (Character.toUpperCase(charAtInputString) <= 'Z')) {
				
				/* Count the number of position of the characters at input string*/
				while(Character.toUpperCase(charAtInputString) != 'A') { 
	
					indexOfCharAtInputString++;
					
					char charTemp = charAtInputString;
					charTemp = (char)(charTemp-1);
					charAtInputString = charTemp;
				}
			
			}else {
				containLetter = false;
			}
			
			/* Count the number of position of the characters at key */
			while(Character.toUpperCase(charAtKey) != 'A') {
				indexOfCharAtKey++;
				char charTemp = charAtKey; 
				charTemp= (char)(charTemp-1);
				charAtKey = charTemp;
			}
			
			keyIndex++;
			
			/* Control the index of the key in range; repeat the key */
			if(keyIndex >= key.length()) 
				keyIndex = keyIndex % key.length();
			
			
			/* Non-characters will not be encrypted, but remain the same. On the other hand, all character will be encrypted by using Vign¨¨re Cipher*/
			if(containLetter == false) {
				encryptedString += charAtInputString;
			}else {
				if(Character.isUpperCase(charAtInputString) == true) {
					/*add up the position of input string and key; in case of being out of range, 
					 * we need to take the remainder of it by dividing by 26. finally,
					 * we add upper case A to the remainder. */
					encryptedString += (char)('A'+(indexOfCharAtInputString + indexOfCharAtKey) % 26);  
																									    		
				}else {
					 /* same as the one above, but using lower case a instead */
					encryptedString += (char)('a'+(indexOfCharAtInputString + indexOfCharAtKey) % 26);
				}																							
			}
		}
		return encryptedString;
	}
	
	/* This method decrypts the encrypted string using the same key that we used to encrypt the input string*/
	protected String decrypt(String inputStringToDecrypt, String key) {
	    
		String decryptedString = "";
		
		for(int encryptedStringIndex = 0,keyIndex = 0; encryptedStringIndex < inputStringToDecrypt.length(); encryptedStringIndex++) {
			
			int indexOfCharAtInputEncryptedString = 0; // number of position of characters at input encrypted string
			int indexOfCharAtKey = 0;// number of position of characters at key
			
			char charAtInputEncryptedString = inputStringToDecrypt.charAt(encryptedStringIndex); // character at input encrypted string by index
			char charAtKey = key.charAt(keyIndex); // character at key by index
			
			boolean containLetter_ = true;
			
			/* Check if the characters at input string are all character. If not, they will remain the same */
			if(Character.toUpperCase(charAtInputEncryptedString)>= 'A' && Character.toUpperCase(charAtInputEncryptedString) <= 'Z') {
			
				/* Count the number of position of the characters at input string*/
				while(Character.toUpperCase(charAtInputEncryptedString) != 'A') {
					indexOfCharAtInputEncryptedString++;
					char charTempForEncryptedString = charAtInputEncryptedString;
					charTempForEncryptedString = (char)(charTempForEncryptedString -1);
					charAtInputEncryptedString = charTempForEncryptedString;	
				}
			}else {
				containLetter_ = false;
			}
			
			/* Count the number of position of the characters at key */
			while(Character.toUpperCase(charAtKey) != 'A') {
				indexOfCharAtKey++;
				char charTempforKey = charAtKey;
				charTempforKey = (char)(charTempforKey-1);
				charAtKey = charTempforKey;
			}
			
			keyIndex++;
			
			/* Control the index of the key in range; repeat the key */
			if(keyIndex >= key.length())
				keyIndex = keyIndex % key.length();
			
			/* Non-characters will not be encrypted, but remain the same. On the other hand, all character will be encrypted by using Vign¨¨re Cipher*/
			if(containLetter_ == false) {
				decryptedString += charAtInputEncryptedString;
			}else {
				if(Character.isUpperCase(charAtInputEncryptedString) == true) {
					if((indexOfCharAtInputEncryptedString-indexOfCharAtKey) < 0) {
						/* In case of upper case and when their difference is negative, we need to
						 * get the absolute value of the difference between the position of input encrypted string
						 * and key, and then 26 minus it will get the position of the character of the string*/		
						decryptedString += (char)('A'+(26-Math.abs(indexOfCharAtInputEncryptedString-indexOfCharAtKey)));
																												
					}else {																								  
						/* In case of upper case and when their difference is positive, it would work similarly as when
						 * we doing the encryption because we could just doing backward to get the original character.
						 * so, we need to get the difference between the position of character at string and key instead of addition
						 * between them. And then we do the rest as same as encryption by taking the remainder of it by dividing by
						 * 26 and then add up to A */																								
						decryptedString += (char)('A' + (indexOfCharAtInputEncryptedString-indexOfCharAtKey) % 26);
					}
				}else {
					
					if((indexOfCharAtInputEncryptedString-indexOfCharAtKey) < 0) {
						/*Same as above as we did in upper case case; this one is just doing it in lower case*/
						decryptedString += (char)('a'+(26-Math.abs(indexOfCharAtInputEncryptedString-indexOfCharAtKey)));
					}else
						/*Same as above as we did in upper case case; this one is just doing it in lower case*/
						decryptedString += (char)('a' + (indexOfCharAtInputEncryptedString-indexOfCharAtKey) % 26);
				}
			}
		}
		
		return decryptedString;
	}
	
		/*Method that allows the ClypeData packages that send through the client and server to access the the same method below from PictureClypeData*/
		public byte [] getByteArray() {
			byte[] byteArray = null;
			return byteArray;
		}
	
		/* A abstract method that is meant to return the encrypted string*/
		public abstract String getData(String key);
	
	
}
