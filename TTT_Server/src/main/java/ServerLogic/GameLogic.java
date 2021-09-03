package ServerLogic;
import application.GameInfo;
import application.GameInfo.Result;

public class GameLogic {
	
	//Brute force approach to win checking
	public static GameInfo.Result evaluateMatch(String [] board) {
		// printBoard(board);
		//Vertical win checker
		for(int i = 0; i < 3; i++) {
			String temp = board[i];
			
			if(temp.equals("b"))
				continue;
			
			int j;
			for(j = 1; j < 3; j++) {
				if(!board[i+(j*3)].equals(temp)) 
					break;
			}
			
			if(j == 3) {
				if(temp.equals("O"))
					return Result.WIN;
				else
					return Result.LOSE;
			}
			
		}
		
		//Horizontal win checker
		for(int i = 0; i < 7; i+=3) {
			String temp = board[i];
			
			if(temp.equals("b"))
				continue;
			
			int j;
			for(j = 1; j < 3; j++) {
				if(!board[i+j].equals(temp)) 
					break;
			}
			
			if(j == 3) {
				if(temp.equals("O"))
					return Result.WIN;
				else
					return Result.LOSE;
				
			}
			
			
		}
		
		//Diagonal checker
		if( (board[0].equals(board[4]) && board[4].equals(board[8])) ||
			(board[2].equals(board[4]) && board[4].equals(board[6])) )
			if(!board[4].equals("b")) {
				if(board[4].equals("O"))
					return Result.WIN;
				else
					return Result.LOSE;
			}
			
		//Game not done checker
		for(int i = 0; i < 9; i++) {
			if(board[i].equals("b"))
				return null;
		}
		
		return Result.DRAW;
	}
	
	//for debugging only, please delete
	public static void printBoard(String[] board) {
		for(int i = 0; i < 7; i+=3) {
			for(int j = 0; j < 3; j++) {
				System.out.print(board[i+j] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
}
