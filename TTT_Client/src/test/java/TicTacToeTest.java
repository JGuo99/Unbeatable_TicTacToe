import static org.junit.jupiter.api.Assertions.*;

import ClientLogic.Client;
import application.GameInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TicTacToeTest {
	static Client c;
	static GameInfo gInfo;
	@BeforeAll
	static void setup(){
		c = new Client("0.0.0.0", 1);
		gInfo = new GameInfo();
	}
	@Test
	void constructorTest(){
		assertEquals(c.getClass(), Client.class);
	}
	@Test
	void eBoardTest(){
		String[] emptyBoard = new String[] {"b","b","b","b","b","b","b","b","b"};
		gInfo.setBoard(emptyBoard);
		assertEquals(emptyBoard, gInfo.getBoard(), "Not an empty board!");
	}
	@Test
	void fBoardTest(){
		String[] emptyBoard = new String[] {"O","X","X","O","X","O","O","X","X"};
		gInfo.setBoard(emptyBoard);
		assertEquals(9, gInfo.getBoard().length, "Wrong Board Size!");
	}
	

}
