import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import ServerLogic.Server;
import application.GameInfo;
import application.GameInfo.Difficulty;
import application.GameInfo.MessageType;
import application.GameInfo.Result;

class ServerTest {
    static Server theServer;
    static GameInfo myGInfo;

    @BeforeAll
    static void init() {
        theServer = new Server(null, null, 5555);
        myGInfo = new GameInfo();
    }

    @Test
    void initServerTest() {
        assertNotNull(theServer);    
    }

    @Test
	void gameInfoTest() {
		assertEquals("application.GameInfo", myGInfo.getClass().getName(), "GameInfo class consructor not working correctly");
    }
    
    @ParameterizedTest
    @EnumSource(value = Difficulty.class)
    void setDiffTest(Difficulty d) {
        myGInfo.setDiff(d);
        assertEquals(d, myGInfo.diff, "Failed to use setDiff() method in GameInfo to set difficulty");
    }

    @ParameterizedTest
    @EnumSource(value = MessageType.class)
    void setMessageIDTest(MessageType m) {
        myGInfo.setMessageID(m);
        assertEquals(m, myGInfo.messageID, "Failed to use setMessageID() method in GameInfo to set message type");
    }

    @ParameterizedTest
    @EnumSource(value = MessageType.class)
    void getMessageIDTest(MessageType m) {
        myGInfo.setMessageID(m);
        assertEquals(m, myGInfo.getMessageID(), "Failed to use getMessageID() method in GameInfo to get message type");
    }

    @Test
    void setBoardTest() {
        String[] board = {"b", "b", "b", "b", "b", "b", "b", "b", "b"};
        myGInfo.setBoard(board);
        assertArrayEquals(board, myGInfo.board, "Failed to use setBoard() method to set the board");
    }

    @Test
    void getBoardTest() {
        String[] board = {"b", "b", "b", "b", "b", "b", "b", "b", "b"};
        myGInfo.setBoard(board);
        assertArrayEquals(board, myGInfo.getBoard(), "Failed to use getBoard() method to get the board");
    }

    @ParameterizedTest
    @EnumSource(value = Result.class)
    void setResultTest(Result r) {
        myGInfo.result = r;
        assertEquals(r, myGInfo.result, "Failed to set result in GameInfo");
    }
}