package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Qifeng Wu and Johnny Magnuson
 * 
 * This class implements a container for storing
 * the data, including the name and content of the file.
 */
public class FileClypeData extends ClypeData {
	
	private String fileName; // name of the file
	private String fileContents; // contents in the file

	/* Constructor 1 */
	public FileClypeData(String userName, String fileName, int type){
		super(userName,type);
		this.fileName = fileName;
		this.fileContents = null;
	}

	/* Constructor 2: Default Constructor */
	public FileClypeData(){
		super();
		this.fileName = "Empty File Name";
		this.fileContents = null;
	}
	
	/* Mutator for the name of the file */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	/* Accessor for the name of the file */ 
	public String getFileName() {
		return this.fileName;
	}
	
	/* Accessor for the content in the file*/
	public String getData() {
		return this.fileContents;
	}
	/* Method to read the file contents and save to the instance variable fileContents*/
	public void readFileContents() throws IOException {
		
		try {
			File file = new File(this.fileName);
			FileReader f_reader = new FileReader(file);
			BufferedReader b_reader = new BufferedReader(f_reader);
			
			String data = "";
			this.fileContents = "";
			while((data = b_reader.readLine()) != null) {
					this.fileContents += data;
			}
			
			b_reader.close();
			
		}catch(FileNotFoundException fnfe) {
			System.err.println("There is no such a file called "+ fileName);
		}catch(IOException ioe) {
			System.err.println("Could not read or close your file");
		}
	}
	
	/*Method to read the file contents and save the encrypted contents to
	 * the instance variable fileContents using a key */
	public void readFileContents(String key) throws IOException{
		try {
			File file = new File(fileName);
			FileReader f_reader = new FileReader(file);
			BufferedReader b_reader = new BufferedReader(f_reader);
			
			String content = "";
			String dataInLine = "";
			this.fileContents = "";
			while((dataInLine = b_reader.readLine()) !=null) {
				content += dataInLine;
			}
			this.fileContents = (super.encrypt(content, key));
			
			b_reader.close();
		}catch(FileNotFoundException fnfe) {
			System.err.println(fnfe.getMessage());
		}catch(IOException ioe) {
			System.err.println("Could not read or close your file");
		}
	}
	
	/*Method to write whatever that is inside the instance variable fileContents to the output file */
	public void writeFileContents() throws IOException {
		try {
			File file = new File(this.fileName);
			FileWriter writer = new FileWriter(file);
			
			for(int i = 0; i < this.fileContents.length();i++) {
				writer.write(this.fileContents.charAt(i));
			}
			writer.close();
			
		}catch(IOException ioe) {
			System.err.println("Could not write to the file");
		}
	}
	
	/*Method to decrypt whatever that is inside of the instance variable fileContents using a key
	 *and then write to the output file */
	public void writeFileContents(String key) throws IOException {
		try {
			File file = new File(this.fileName);
			FileWriter writer = new FileWriter(file);
			
			String contentsInFile = super.decrypt(this.fileContents, key);
			for(int i = 0; i < contentsInFile.length();i++) {
				writer.write(contentsInFile.charAt(i));
			}
			writer.close();
			
		}catch(IOException ioe) {
			System.err.println("Could not write to the file");
		}
	}
	@Override
	
	/* Method to set up a unique value for object and return it */
	public int hashCode() {
		int result = 13;
			result = 37 * result + this.fileName.length();
			
			return result;
	}
	
	/* Method to compare two object's equality */
	public boolean equals(Object other) {
		FileClypeData otherFileClypeData = (FileClypeData) other;
		
		if(!(other instanceof FileClypeData)) {
			return false;
		}
		if(this.fileContents == null) {
			return false;
		}
		
		return this.fileName == otherFileClypeData.fileName &&
			   this.fileContents == otherFileClypeData.fileContents;
	}
	
	/* Method to return a full description of the class with all instance variables */
	public String toString() {
		return "The file " + this.fileName + " contains " + this.fileContents; 
	}
	
	/* Method to return the decrypted string of file contents*/
	public String getData(String key) {
		return super.decrypt(this.fileContents, key);
	}
}

