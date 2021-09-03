package ServerLogic;

import java.util.ArrayList;

import ServerLogic.Server.ClientThread;
import application.GameInfo;
import application.GameInfo.Difficulty;

public class FindNextMove extends Thread {
    MinMax minMax;
    public String[] currState;
    public String[] boardWithAIMove;
    ArrayList<Node> movesList;
    ClientThread caller;
    public Difficulty diff;
    

    public FindNextMove() {
        String[] blankBoard = {"b", "b", "b", "b", "b", "b", "b", "b", "b"};
        //minMax = new MinMax(blankBoard);
    }

    public void run() {
//        minMax = new MinMax(currState);
//        movesList = minMax.findMoves();
//        minMax.setStateList_MinMaxValues(movesList, 1);
        // try {
        //    wait();
        // } catch(Exception e) {
        //     e.printStackTrace();
        // }
    	while(true) {
    		try {
    			synchronized(this) {
    				wait();
	    			boardWithAIMove = findMove();
    			}
    			synchronized(caller) {
    				caller.notify();
    			}
    		} catch(Exception e) {
    			e.printStackTrace();
        	}
    	}
    }

    @SuppressWarnings("incomplete-switch")
	public String[] findMove(){
        minMax = new MinMax(currState);
        movesList = minMax.findMoves();
        minMax.setStateList_MinMaxValues(movesList,1);

        ArrayList<Node> easyList = new ArrayList<>();
        ArrayList<Node> medList = new ArrayList<>();
        ArrayList<Node> hardList = new ArrayList<>();
        
        for (Node n : movesList) {
            switch(n.getMinMax()) {
                case -10:
                    easyList.add(n);
                    break;
                case 0:
                    medList.add(n);
                    break;
                case 10:
                    hardList.add(n);              
            }
        }

        Node chosenMove = null;
        switch (diff) {
            case EASY:
                if (easyList.size() != 0)
                    chosenMove = easyList.remove(0);
                else if (medList.size() != 0)
                    chosenMove = medList.remove(0);
                else 
                    chosenMove = hardList.remove(0);
                break;
            case MEDIUM:
                if (medList.size() != 0)
                    chosenMove = medList.remove(0);
                else if (easyList.size() != 0)
                    chosenMove = easyList.remove(0);
                else 
                    chosenMove = hardList.remove(0);
                break;
            case HARD:
                if (hardList.size() != 0)
                    chosenMove = hardList.remove(0);
                else if (medList.size() != 0)
                    chosenMove = medList.remove(0);
                else 
                    chosenMove = easyList.remove(0);
        }
        
        return chosenMove.getInitStateString();
    }

    public void setCurrState(String[] state) {
        currState = state;
    }

    // public void nextMove() {
    //     movesList = minMax.findMoves();
    // }

}