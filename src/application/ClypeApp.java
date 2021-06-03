package application;
	
/**
 * @author Qifeng Wu and John Magnuson
 * 
 * Handles the GUI between the client and the server. 
 * Begins a login GUI for userName, localHost, and port, 
 * and then opens up a single-session chat GUI 
 * 
 */
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import data.PictureClypeData;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.ClypeClient;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;


public class ClypeApp extends Application{
	private AnchorPane loginRoot = new AnchorPane();
	private AnchorPane mainStageRoot = new AnchorPane();
	private TextArea textArea_message = new TextArea();
	private TextArea textArea_friends = new TextArea();
	private final Scene loginScene = new Scene(loginRoot,550,370);
	private final Scene mainScene = new Scene(mainStageRoot,1200,900);
	private TextField textField_input = new TextField();
	private ClypeClient clypeClient = null;
	private ClypeApp clypeAppLock = this;
	private Alert alert_exit = new Alert(Alert.AlertType.WARNING);
	private Alert alert_login = new Alert(Alert.AlertType.INFORMATION);
	private Alert alert_NoPicture = new Alert(Alert.AlertType.INFORMATION);
	private ToolBarButtons toolBarButtons; 
	private Label errorLabel = new Label();
	private TextField userField = new TextField("Anonymous User");
	private TextField hostField = new TextField("localHost");
	private TextField portField = new TextField("7000");
	private String userName;
	private String hostName;
	private int portNumber;
	private BufferedImage bufferedImage = null;
	private ArrayList<BufferedImage> bufferedImageList = new ArrayList<BufferedImage>();
	private ArrayList<String> userNameList = new ArrayList<String>();

	/* Method to call the AnchorPane mainStageRoot */
	public AnchorPane getMainStageRoot() {
		return mainStageRoot;
	}
	
	/* Method to set ClypeClient in the clypeApp GUI with a userName, localHost, and port */
	public void setClypeClient(String userName, String localhost, int port) {
		clypeClient = new ClypeClient(userName,localhost,port,this);
	}
	
	/* Method to call the clypeClient */
	public ClypeClient getClient() {
		return clypeClient;
	}

	/* Method that receives input, buffers, and sends the messages into the Message Box */
	public void setTextArea_message(String message) {
		
		textArea_message.appendText(message);

			synchronized(clypeAppLock) {
				clypeAppLock.notify();
			}
			
			if(clypeClient.getDataToReceiveFromServer().getData().equals("DONE")) {
				exit();
			}

	}
	
	/* Method to set bufferedImage */
	public void setBufferedImage(BufferedImage bufferedImage) {
		this.bufferedImage = bufferedImage;
		
	}
	
	/* Method to add a bufferedImage to a the bufferedImageList */
	public void addBufferedImage(BufferedImage bufferedImage) {
		this.bufferedImageList.add(bufferedImage);
	}
	
	/* Method to add a userName to the userNameList */
	public void addUserName(String userName) {
		this.userNameList.add(userName);
	}

	/* Method to add a friend to the friendList text box  */
	public void setTextArea_friends(String friendsList) {
		textArea_friends.setText(friendsList+"\n");
	}
	
	/* Method to exit from the Main chat GUI */
	public void exit() {
		
		Platform.runLater(() -> {
			
			for(int x = 3; x > 0; x--) {
				
				alert_exit.setHeaderText("Exit in "+ x + " seconds");
				alert_exit.show();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				alert_exit.close();
			}
        });
		
		Platform.exit();
		
		synchronized(this.getClient()){
			this.getClient().notify();
		}
		
	}
	
	/* Method to confirm and send login info to the Main chat GUI */
	public void login(Stage loginStage) {
		
		loginStage.hide();
			
			alert_login.setContentText("Logging In...");
			alert_login.show();
			
			userName = userField.getText();
			hostName = hostField.getText();
			
			if(!portField.getText().equals(""))
				portNumber = Integer.parseInt(portField.getText());
			
			if(portNumber < 1024) {
				alert_login.close();
				loginStage.show();
				portField.clear();
				errorLabel.setText("\t\tPort must be greater than 1024");
				if(!portField.getText().equals(""))
					portNumber = Integer.parseInt(portField.getText());
			}else {
				
				setClypeClient(userName,hostName,portNumber);
				clypeClient.start();
				
				if(clypeClient.isConnectionTrue() == true) {
					loginStage.close();
					showMainStageGUI();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					alert_login.close();
				}else {
					
					alert_login.close();
					alert_login.setContentText("Fail to log in");
					alert_login.show();
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					alert_login.close();
					loginStage.show();

				}
				errorLabel.setText("Make sure that the host name and port are correct");
			}
	}

	/* Method to make a TitleBar for a given stage and AnchorPane*/
	private void makeTitleBar(Stage primaryStage, AnchorPane root) {
		ToolBar toolBar = new ToolBar();
		toolBarButtons = new ToolBarButtons(primaryStage,this);
		toolBar.setPrefHeight(32);
        toolBar.setMinHeight(32);
        toolBar.setMaxHeight(32);
        toolBar.setStyle(" -fx-background-color:transparent" );
        toolBar.getItems().add(toolBarButtons);
        toolBar.getItems().add(new Dragging(primaryStage,toolBar));
        
        root.getChildren().add(toolBar);  
        AnchorPane.setRightAnchor(toolBar, 0.0);
        AnchorPane.setLeftAnchor(toolBar, 0.0);
	}
	
	/* Method to create, set, and position anchors for the GUI elements in LoginStage */
	private void addLoginGUIContents(Stage loginStage) {
		
		String labelStyle = "-fx-font-size: 18; -fx-text-fill: rgba(255,255,0,0.9)";
		
		Label loginTitle = new Label("Clype 2.0 Login");
		loginTitle.setStyle("-fx-font-size: 24; -fx-text-fill: rgba(255,255,0,0.9)");
		
		Label userLabel = new Label("User Name:");
		userLabel.setStyle( labelStyle );
		
		Label hostLabel = new Label("Host Name:");
		hostLabel.setStyle( labelStyle );
		
		
		Label portLabel = new Label("Port Number:");
		portLabel.setStyle( labelStyle );
		
		errorLabel.setStyle("-fx-background-color: darkgreen; -fx-font-size: 22; -fx-text-fill: red"); 

		Button loginBtn = new Button();
		loginBtn.setText( "Log in" );
		loginBtn.setStyle( "-fx-background-color: rgba(255,255,0,0.9); -fx-font-size: 20; -fx-text-fill: darkgreen" );
		loginBtn.setOnAction( new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				login(loginStage);
			}
		});
		
		loginRoot.setOnKeyPressed( new EventHandler<KeyEvent>() {
			
			@Override
			public void handle( KeyEvent ke ) {
				if ( ke.getCode() == KeyCode.ENTER) {
					loginRoot.requestFocus();
					login(loginStage);
				}
			}
		});

		loginRoot.getChildren().add( loginTitle );
		
		loginRoot.getChildren().add( userLabel );
		loginRoot.getChildren().add( userField );
		
		loginRoot.getChildren().add( hostLabel );
		loginRoot.getChildren().add( hostField );
		
		loginRoot.getChildren().add( portLabel );
		loginRoot.getChildren().add( portField );
		
		loginRoot.getChildren().add( loginBtn );
		loginRoot.getChildren().add( errorLabel );

		
		AnchorPane.setTopAnchor( loginTitle , 50.0 );
		AnchorPane.setLeftAnchor( loginTitle, 185.0 );
		
		AnchorPane.setTopAnchor( userLabel, 110.0 );
		AnchorPane.setLeftAnchor( userLabel, 130.0 );

		AnchorPane.setTopAnchor( userField, 110.0 );
		AnchorPane.setLeftAnchor( userField, 260.0 );
		
		AnchorPane.setTopAnchor( hostLabel, 160.0 );
		AnchorPane.setLeftAnchor( hostLabel, 130.0 );
		
		AnchorPane.setTopAnchor( hostField, 160.0 );
		AnchorPane.setLeftAnchor( hostField, 260.0 );
		
		AnchorPane.setTopAnchor( portLabel, 210.0 );
		AnchorPane.setLeftAnchor( portLabel, 130.0 );
		
		AnchorPane.setTopAnchor( portField, 210.0 );
		AnchorPane.setLeftAnchor( portField, 260.0 );
		
		AnchorPane.setBottomAnchor( loginBtn, 50.0 );
		AnchorPane.setLeftAnchor( loginBtn , 220.0 );
		
		AnchorPane.setBottomAnchor( errorLabel, 15.0 );
		AnchorPane.setLeftAnchor( errorLabel, 25.0 );
	}

	/* Method to create, set, and position anchors for the Main chat GUI Elements */
	private void addMainGUIContents() {
		
		Label label_title = new Label("Clype 2.0");
		label_title.setStyle(" -fx-background-color: transparent; -fx-text-fill: yellow; -fx-font-size: 40;");
		
		TextField textField_message_title = new TextField();
		textField_message_title.setStyle(" -fx-background-color: rgba(0,130,0,0.1); -fx-text-fill: yellow; -fx-font-size: 25;");
		textField_message_title.setText("Message Area");
		textField_message_title.setEditable(false);
		
		textArea_message.setStyle("-fx-text-fill: yellow; -fx-font-size: 20;");
		textArea_message.setWrapText(true);
		textArea_message.setEditable(false);
		
		TextField textField_friends_title = new TextField();
		textField_friends_title.setStyle(" -fx-background-color: rgba(0,130,0,0.1); -fx-text-fill: yellow; -fx-font-size: 25;");
		textField_friends_title.setText("Friends List");
		textField_friends_title.setEditable(false);
		
		textArea_friends.setStyle(" -fx-text-fill: yellow; -fx-font-size: 20;");
		textArea_friends.setEditable(false);
		
		textField_input.setStyle("-fx-background-color: yellow; -fx-text-fill: darkgreen; -fx-font-size:21");
		
		textField_input.setPromptText("Enter Your Message Here");
		
		Button button_send = new Button();
		button_send.setText("Send");
		button_send.setStyle("-fx-background-color: rgba(255,255,0,0.9); -fx-font-size:20; -fx-text-fill: darkgreen");
		
		Thread thread_send = new Thread(new Runnable() {
			
			@Override 
			public void run() {
				
				button_send.setOnAction(new EventHandler <ActionEvent> () {
					@Override
					public void handle(ActionEvent ae) {
						
						textField_input.requestFocus();
						
						synchronized(clypeClient.getClypeClientLock()) {
							clypeClient.getClypeClientLock().notify();
						}
						
						synchronized(clypeAppLock) {
							try {
								clypeAppLock.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						textField_input.clear();
					}
				});
				
				mainStageRoot.setOnKeyPressed(new EventHandler <KeyEvent>() {
					@Override
					public void handle(KeyEvent ke) {
						if(ke.getCode() == KeyCode.ENTER) {
							
							button_send.requestFocus();
							textField_input.requestFocus();
							
							synchronized(clypeClient.getClypeClientLock()) {
								clypeClient.getClypeClientLock().notifyAll();
							}
							synchronized(clypeAppLock) {
								try {
									clypeAppLock.wait();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							textField_input.clear();
						}
					}
				});
				
			}
		});
		thread_send.start();
		
		Button button_sendPicture = new Button();
		button_sendPicture.setText("Send Picture");
		button_sendPicture.setStyle("-fx-background-color: rgba(255,255,0,0.9); -fx-font-size:20; -fx-text-fill: darkgreen");
		Platform.runLater(()->{
			button_sendPicture.setOnAction(new EventHandler<ActionEvent> (){
				@Override
				public void handle(ActionEvent ae) {
					
					FileChooserWindow fcw = new FileChooserWindow();
					PictureClypeData pcd;
					fcw.open();
					if(fcw.getFile()!=null) {
						System.out.println(fcw.getFile().getPath());
						pcd = new PictureClypeData(clypeClient.getUserName(),fcw.getFile(),6);
						pcd.createImageInByte();
						try {
							clypeClient.getObjectOutputStream().writeObject(pcd);
						} catch (IOException e) {
							e.printStackTrace();
						}	
					}
				}
			});
		});
		
		Button button_viewPicture = new Button("View Picture");
		button_viewPicture.setStyle("-fx-background-color: rgba(255,255,0,0.9); -fx-font-size:20; -fx-text-fill: darkgreen");
		button_viewPicture.setOnAction(new EventHandler <ActionEvent>() {
			public void handle(ActionEvent ae) {
				if(bufferedImage!=null) {
					Stage stage = new Stage();
					FlowPane fp = new FlowPane();
					fp.setVgap(10);
					ScrollPane sp = new ScrollPane(fp);
					Scene scene = new Scene(sp,450,500);
					stage.setResizable(false);
					stage.setScene(scene);
					stage.setTitle("Images");
					System.out.println("DEBUG"+bufferedImageList.size());
					ArrayList<Button> buttonList = new ArrayList<Button>();
				
					for(int x = 0; x < bufferedImageList.size();x++) {
						buttonList.add(new Button(userNameList.get(x)+ "'s picture "));
					}
					for(int x = 0; x < buttonList.size(); x++) {
						buttonList.get(x).setStyle("-fx-font-size: 20");
						
					}
					
					for(int x = 0; x < bufferedImageList.size();x++) {
						BufferedImage bufferedImage = bufferedImageList.get(x);
						buttonList.get(x).setOnAction(new EventHandler<ActionEvent> () {
							@Override
							public void handle(ActionEvent ae) {
								PictureView pv = new PictureView(bufferedImage);
								pv.start(new Stage());
							}
						});
						fp.getChildren().add(buttonList.get(x));
						
						
					}
					stage.show();
				}else {
					alert_NoPicture.setContentText("No user had sent any picture");
					alert_NoPicture.show();
				}	
			}
		});
		
		mainStageRoot.getChildren().add(label_title);
		mainStageRoot.getChildren().add(textField_message_title);
		mainStageRoot.getChildren().add(textArea_message);
		mainStageRoot.getChildren().add(textField_friends_title);
		mainStageRoot.getChildren().add(textArea_friends);
		mainStageRoot.getChildren().add(textField_input);
		mainStageRoot.getChildren().add(button_send);
		mainStageRoot.getChildren().add(button_sendPicture);
		mainStageRoot.getChildren().add(button_viewPicture);

		
		AnchorPane.setTopAnchor(label_title, 20.0);
		AnchorPane.setLeftAnchor(label_title, 510.0);
							
		AnchorPane.setTopAnchor(textField_message_title, 80.0);
		AnchorPane.setLeftAnchor(textField_message_title, 30.0);
		AnchorPane.setRightAnchor(textField_message_title, 630.0);
							
		AnchorPane.setTopAnchor(textArea_message, 135.0);
		AnchorPane.setLeftAnchor(textArea_message, 30.0);
		AnchorPane.setRightAnchor(textArea_message, 630.0);
		AnchorPane.setBottomAnchor(textArea_message, 100.0);
		
		AnchorPane.setTopAnchor(textField_friends_title, 80.0);
		AnchorPane.setLeftAnchor(textField_friends_title, 600.0);
		AnchorPane.setRightAnchor(textField_friends_title, 30.0);
		
		AnchorPane.setTopAnchor(textArea_friends, 135.0);
		AnchorPane.setLeftAnchor(textArea_friends, 600.0);
		AnchorPane.setRightAnchor(textArea_friends, 30.0);
		AnchorPane.setBottomAnchor(textArea_friends, 450.0);
		
		AnchorPane.setLeftAnchor(textField_input, 30.0);
		AnchorPane.setRightAnchor(textField_input, 30.0);
		AnchorPane.setBottomAnchor(textField_input, 30.0);
		
		AnchorPane.setLeftAnchor(button_send, 1070.0);
		AnchorPane.setRightAnchor(button_send,30.0);
		AnchorPane.setBottomAnchor(button_send, 100.0);
		
		AnchorPane.setLeftAnchor(button_sendPicture, 910.0);
		AnchorPane.setRightAnchor(button_sendPicture,140.0);
		AnchorPane.setBottomAnchor(button_sendPicture, 100.0);
		
		AnchorPane.setLeftAnchor(button_viewPicture, 910.0);
		AnchorPane.setRightAnchor(button_viewPicture,140.0);
		AnchorPane.setBottomAnchor(button_viewPicture, 150.0);
	}
	
	/* Method to display the Main chat GUI */
	public void showMainStageGUI() {
			
		Stage primaryStage = new Stage();
		primaryStage.initStyle(StageStyle.UNDECORATED);
		try {
							
			mainStageRoot.setStyle(" -fx-background-color: darkgreen;");
			makeTitleBar(primaryStage,mainStageRoot);				
			addMainGUIContents();
							
			mainScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setTitle("Clype 2.0");
			primaryStage.setScene(mainScene);
			primaryStage.setOpacity(0.8);
			primaryStage.show();
							
		} catch(Exception e) {
				e.printStackTrace();
		}
	}
		
	/* Method to get user Input through the main chat box */
	public String sendInput() {
		 	
		return textField_input.getText();
		 
	}
	 
	/* Method to start the Login GUI */
	 @Override
	public void start(Stage logInStage) {
			
			logInStage.initStyle(StageStyle.UNDECORATED);
			
			loginRoot.setStyle(" -fx-background-color: darkgreen");
	        makeTitleBar(logInStage,loginRoot);				
			addLoginGUIContents(logInStage);
			
			logInStage.setTitle("Clype 2.0 Log In");
			logInStage.setScene(loginScene);
			logInStage.setOpacity(1.0); 
			logInStage.show();
			
		}
	
	/** 
	 * Main Method 
	 * 
	 * Launches ClypeApp
	 * @param args command line arguments
	 *
	 */
	public static void main(String[] args) {
		
		launch(args);	
	}
}