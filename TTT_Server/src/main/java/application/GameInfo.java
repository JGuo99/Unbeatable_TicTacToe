package application;

import java.io.Serializable;
import java.util.ArrayList;

public class GameInfo implements Serializable {
	public ArrayList<Integer> leaderboard;
	public String[] board;
	public Difficulty diff;
	public Result result;
	public MessageType messageID;
	public int aiMove; //stores last move made by AI
	public String username;
	
	public enum MessageType {
		NOTIFY, NEW_GAME, PLAY, END_GAME, SET_USERNAME;
	}
	
	public enum Difficulty {
		EASY, MEDIUM, HARD, IMPOSSIBLE;
	}
	
	public enum Result {
		WIN, DRAW, LOSE;
	}
	
	public GameInfo() {
		leaderboard = new ArrayList<Integer>();
		board = new String[9]; // b: blank | O: Player | X: CPU
		aiMove = -1; //default to handle error cases
		result = null;
	}
	
	
	public void setDiff(Difficulty diff) {
		this.diff = diff;		
	}

	public void setMessageID(MessageType mT){
		this.messageID = mT;
	}
	
	public void setBoard(String[] board) {
		this.board = board;
	}

	public MessageType getMessageID(){
		return messageID;
	}

	public String[] getBoard() {
		return board;
	}
}
