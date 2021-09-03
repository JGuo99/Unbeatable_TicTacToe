import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ServerLogic.FindNextMove;
import ServerLogic.MinMax;
import ServerLogic.Node;

public class MinMaxTest {
    MinMax minMax;
    FindNextMove minMaxImpl;

    @BeforeEach
    void init() {
        String[] board = {"b", "b", "b", "b", "O", "b", "b", "b", "b"};
        minMax = new MinMax(board);
    }

    @Test
    void initMinMaxTest() {    
        assertEquals("ServerLogic.MinMax", minMax.getClass().getName(), "Failed to properly initialize MinMax");
    }

    @Test
    void initFindNextMoveTest() {
        minMaxImpl = new FindNextMove();
        assertEquals("ServerLogic.FindNextMove", minMaxImpl.getClass().getName(), "Failed to properly initialize FindNextMove");
    }

    @Test
    void findMovesNotNullTest() {
        assertNotNull(minMax.findMoves(), "findMoves() failed to return a non null arraylist");
    }

    @Test
    void findTwoMovesTest() {
        String[] board = {"O", "X", "X", "X", "O", "b", "O", "O", "b"};
        minMax = new MinMax(board);
        assertEquals(2, minMax.findMoves().size(), "findMoves() failed to return only two moves");
    }

    @Test
    void setStateList_MinMaxNegValTest() {
        String[] board = {"O", "X", "X", "X", "O", "b", "O", "O", "b"};
        minMax = new MinMax(board);
        ArrayList<Node> movesList = minMax.findMoves();
        minMax.setStateList_MinMaxValues(movesList, 1);
        assertEquals(-10, movesList.get(0).getMinMax(), "Failed to set state list minMax values to -10 properly");
    }

    @Test
    void setStateList_MinMaxZeroValTest() {
        String[] board = {"O", "X", "X", "b", "b", "O", "O", "O", "X"};
        minMax = new MinMax(board);
        ArrayList<Node> movesList = minMax.findMoves();
        minMax.setStateList_MinMaxValues(movesList, 1);
        assertEquals(0, movesList.get(0).getMinMax(), "Failed to set state list minMax values to 0 properly");
    }

    @Test
    void setStateList_MinMaxPosValTest() {
        String[] board = {"O", "X", "X", "O", "b", "O", "X", "O", "b"};
        minMax = new MinMax(board);
        ArrayList<Node> movesList = minMax.findMoves();
        minMax.setStateList_MinMaxValues(movesList, 1);
        assertEquals(10, movesList.get(0).getMinMax(), "Failed to set state list minMax values to 10 properly");
    }

    @Test
    void minTest() {
        String[] board = {"O", "X", "X", "O", "b", "O", "X", "O", "b"};
        minMax = new MinMax(board);
        ArrayList<Node> movesList = minMax.findMoves();
        minMax.Min(movesList.get(0));
        assertEquals(10, movesList.get(0).getMinMax(), "Failed to return the proper min value for this node");
    }

    @Test
    void maxTest() {
        String[] board = {"O", "X", "X", "O", "b", "O", "X", "O", "b"};
        minMax = new MinMax(board);
        ArrayList<Node> movesList = minMax.findMoves();
        minMax.Min(movesList.get(0));
        assertEquals(10, movesList.get(0).getMinMax(), "Failed to return the proper max value for this node");
    }

}