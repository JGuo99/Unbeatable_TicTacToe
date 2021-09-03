package ClientLogic;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

import application.GameInfo;

public class Client extends Thread{
    //Connection to server related stuff
    String ip;
	int port;
	Socket socketClient;
	public ArrayList<Integer> localLeaderBoard;
	
	
	volatile public GameInfo localGInfo;
	// public int clientID;
	
	//Object Streams for interaction with server
	ObjectOutputStream out;
	ObjectInputStream in;

	//Constructor
    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
        localLeaderBoard = new ArrayList<Integer>();
    }

	public Consumer<Serializable> startCallback; //start connection
	public void setStartCallback(Consumer<Serializable> call) {
	    this.startCallback = call;
	}

	public Consumer<Serializable> updateCallback; //update scoreboard
	public void setUpdateCallback(Consumer<Serializable> call){
		this.updateCallback = call;
	}
	
	public Consumer<Serializable> gameCallback; //Game related
	public void setGameCallback(Consumer<Serializable> call){
		this.gameCallback = call;
	}
	
	public Consumer<Serializable> shutDownCallback; //Game related
	public void setShutDownCallback(Consumer<Serializable> call){
		this.shutDownCallback = call;
	}
	
	
    public void run() {
    	Boolean serverStarted;
	    try {
			//Connection to server
			socketClient = new Socket(ip, port);
			out = new ObjectOutputStream(socketClient.getOutputStream());
			in = new ObjectInputStream(socketClient.getInputStream());
			socketClient.setTcpNoDelay(true);
			
			GameInfo gInfo =  (GameInfo) in.readObject();
			localGInfo = gInfo;
			for(Integer i : gInfo.leaderboard) {
				localLeaderBoard.add(i);
			}
			serverStarted = true;
			startCallback.accept(serverStarted);
		}catch(Exception e) {
			serverStarted = false;
			startCallback.accept(false);
		}
		while(true){
			try{
				GameInfo gInfo = (GameInfo) in.readObject();
				localGInfo = gInfo;
				interpretMessage();
			}catch(Exception e){
				System.out.println("Did not get input from server: " + e);			
				e.printStackTrace();				
				try {
					socketClient.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				if(serverStarted)
					shutDownCallback.accept(null);
				break;
			}
		}
	}
	
	//get result from server
	public void recieveResult(GameInfo gInfo) {
		try{
			updateCallback.accept(gInfo.result);
			//callback.accept(gInfo);
		}catch(Exception e) {
			System.out.println("Did not recieve result from server.");
		}
	}
	//Used to update Score board, Leaderboard is array size of 3.
	public void updateLeaderboard(GameInfo gInfo) {
		updateCallback.accept(gInfo);
		localLeaderBoard.clear();
		for(Integer i : gInfo.leaderboard) {
			localLeaderBoard.add(i);
		}
		//callback.accept(gInfo);
	}

	public synchronized void interpretMessage(){
		switch(localGInfo.messageID){
			case NEW_GAME:
			case END_GAME:
			case PLAY:
				gameCallback.accept(localGInfo);
				break;
			case NOTIFY:
				//add method to update message
				updateLeaderboard(localGInfo);
				break;

			default:
				System.out.println("Message did not get interpreted!");
				break;
		}
	}

	public void send(){
		try {
			out.flush();
			out.reset();
			out.writeObject(localGInfo);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
