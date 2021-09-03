package ServerLogic;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.Thread;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

import application.GameInfo;
import application.GameInfo.MessageType;
import application.GameInfo.Result;

public class Server {
    Consumer<Serializable> matchViewCallback;
    Consumer<Serializable> listCallback;
    int port;
    ServerThread serverThread;
    //ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
    HashMap<Integer, ClientThread> clients = new HashMap<Integer, Server.ClientThread>();
    HashMap<Integer, WeakReference<Observer>> observerColl = new HashMap<>();
    HashMap<Integer, String[]> matches = new HashMap<>();
    FindNextMove minMaxAlgr; // instance of a class that finds the next move for each client

    GameInfo masterGInfo; // master GameInfo obj if needed | can store the leaderboard

    public Server(Consumer<Serializable> mVCallback, Consumer<Serializable> listCallback, int port) {
        matchViewCallback = mVCallback;
        this.listCallback = listCallback;
        this.port = port;
        serverThread = new ServerThread();
        masterGInfo = new GameInfo();      
        serverThread.start();
        minMaxAlgr = new FindNextMove();
        minMaxAlgr.start();
    }

    public void setMatchVCallback(Consumer<Serializable> callback) {
        this.matchViewCallback = callback;
    }

    public void setListCallback(Consumer<Serializable> callback) {
        this.listCallback = callback;
    }

    public void registerObserver(int clientID, Observer obs) {
        synchronized (observerColl) {
            observerColl.put(clientID, new WeakReference<Observer>(obs));
        }
    }

    public void unregisterObserver(int clientID) {
        synchronized (observerColl) {
            observerColl.remove(clientID);
        }
    }

    public synchronized void notifyObservers() {
        synchronized(observerColl) { // In case if some clients join/disconnect amid notifying
            observerColl.forEach((k, wkR) -> {					
                Observer obs = (Observer) wkR.get();
                if (obs != null)
                    obs.update(masterGInfo);			
            });					
		}
    }

    class ServerThread extends Thread {
        int clientCount = 1;

        public void run() {

            try (ServerSocket socket = new ServerSocket(port)) {
                while (true) {
                    ClientThread newClient = new ClientThread(socket.accept(), clientCount);
                    clients.put(clientCount,newClient);
                    registerObserver(clientCount, newClient);
                    listCallback.accept("Client #" + clientCount + " connected to the server");
                    clientCount++;                    
                    newClient.start();
                }

            } catch (Exception e) {
                System.out.println("Failed to start server on port: " + port);
                e.printStackTrace();
            }
        }
    } // end of ServerThread

    class ClientThread extends Thread implements Observer {
        Socket conn;
        int id;
        int points;
        ObjectInputStream in;
        ObjectOutputStream out;
        GameInfo localGInfo;
        String username;

        ClientThread(Socket s, int id) {
            conn = s;
            this.id = id;
            localGInfo = new GameInfo();
            points = 0;
        }

        @SuppressWarnings("unchecked")
		public void run() {
            try {
                in = new ObjectInputStream(conn.getInputStream());
                out = new ObjectOutputStream(conn.getOutputStream());
                conn.setTcpNoDelay(true);
            } catch (Exception e) {
                System.out.println("Failed to create object i/o streams");
                e.printStackTrace();
            }
            
            localGInfo.messageID = MessageType.NOTIFY;
            localGInfo.leaderboard = (ArrayList<Integer>) masterGInfo.leaderboard.clone();
            send();
            
            while (true) {
                try {
                    localGInfo = (GameInfo) in.readObject();
                    interpretMsg(localGInfo); //interpret msg
                } catch (Exception e) {
                    listCallback.accept("Client #" + id + " has disconnected from the server");
                    clients.remove(id);
                    unregisterObserver(id);
                    if (matches.get(id) != null) // if client disconnects amid a match
                        matches.remove(id);
                    matchViewCallback.accept(matches);
                    updateLeaderBoard(); // in case this client is on the leaderboard
                    try {
                        conn.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;
                }
            }
        }

        // interpret the purpose of the message from client
        public void interpretMsg(GameInfo gInfo) {
            switch (gInfo.messageID) {
                case NEW_GAME:
                    newGame();
                    break;
                case PLAY:
                    //gamePlay();
                	synchronized(listCallback) {
                    	listCallback.accept("Client#" + id + "played");
                	}
					try {
						gamePlay2();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    break;
                default:
            }
        }

        // method to create a new game for client
        public void newGame() {
            synchronized(listCallback) {
                listCallback.accept("Client #" + id + " started a new game in " + localGInfo.diff.toString() + " mode");
            }
            localGInfo.board = new String[] {"b","b","b","b","b","b","b","b","b"}; // reset board
            matches.put(id, localGInfo.board); // maps client id to the board
            matchViewCallback.accept(matches);
            localGInfo.result = null;
            localGInfo.messageID = MessageType.NEW_GAME;
            send();
        }
        
        public void updateLeaderBoard() {
        	points++;
        	synchronized(masterGInfo){
        		int i = 2;
        		boolean leaderboardChanged = true;
        		
        		//Leaderboard not full yet
        		if(masterGInfo.leaderboard.size() < 3 ) {
        			if(masterGInfo.leaderboard.indexOf(id) == -1) { //if not in list yet
        				masterGInfo.leaderboard.add(this.id);
        			} else if(masterGInfo.leaderboard.get(0) == id){ //if this client is first place do nothing
        				return;
        			} else {
        				if(clients.get(masterGInfo.leaderboard.get(0)).points < points) { //if this client is second place, and has more points now
        					masterGInfo.leaderboard.set(1, masterGInfo.leaderboard.get(0));
        					masterGInfo.leaderboard.set(0, this.id);
        				} else {
        					return;
        				}
        				
        			}
            		listCallback.accept("Updating clients on leaderboard changes");
            		masterGInfo.messageID = MessageType.NOTIFY;
        			notifyObservers();
        			return;
        		}
        			
        		//find new player's position
            	while( i > 0 && (
            		   points > clients.get(masterGInfo.leaderboard.get(i)).points || //higher score
            		   (points == masterGInfo.leaderboard.get(i) && this.id <= masterGInfo.leaderboard.get(i)) //same score, but lower/same id
            			)
            		) {
            		
            		i--;
            	}
            	
            	switch(i) {
	            	case -1: //first place	       	
	            		masterGInfo.leaderboard.set(2, masterGInfo.leaderboard.get(1));
	            		masterGInfo.leaderboard.set(1, masterGInfo.leaderboard.get(0));
	            		masterGInfo.leaderboard.set(0, this.id);
	            		break;
	            	case 0: //second place
	            		masterGInfo.leaderboard.set(2, masterGInfo.leaderboard.get(1));
	            		masterGInfo.leaderboard.set(1, this.id);
	            		break;
	            	case 1: //third place

	            		masterGInfo.leaderboard.set(2, this.id);
	            		break;
	            	default: //didn't make it
	            		leaderboardChanged = false;
	            		break;
            	}
            	
            	if(leaderboardChanged) {
            		listCallback.accept("Updating clients on leaderboard changes");
            		masterGInfo.messageID = MessageType.NOTIFY;
            		notifyObservers();
            	}
            	
        	}
        }
        
        
//        public void manageLeaderBoard(){
//            int top_ID = localGInfo.leaderboard[0];
//            int mid_ID = localGInfo.leaderboard[1];
//            int bottom_ID = localGInfo.leaderboard[2];
//                
//            if ((top_ID != -999) && (clients.get(top_ID).points <= this.points)){
//                // Change the leaderboard - 1st Position
//                localGInfo.leaderboard[0] = this.id;
//                localGInfo.leaderboard[1] = top_ID;
//                localGInfo.leaderboard[2] = mid_ID;
//            }
//            else { 
//                if (top_ID == -999){ localGInfo.leaderboard[0]=this.id;} // Check if 1st Position is vacant
//                else {
//                    if ((mid_ID != -999) && (clients.get(mid_ID).points <= this.points)){
//                    // Change the leaderboard - 2nd Position 
//                        localGInfo.leaderboard[1] = this.id;
//                        localGInfo.leaderboard[2] = mid_ID;
//                    }
//                    else {
//                        if (mid_ID == -999){ localGInfo.leaderboard[1]=this.id;} // Check if 2nd Position is vacant
//                        else {
//                            if ((bottom_ID != -999) && (clients.get(bottom_ID).points <= this.points)){
//                                // Change the leaderboard - 3rd Position
//                                localGInfo.leaderboard[2] = this.id;
//                            }
//                            else {
//                                if (bottom_ID == -999){ localGInfo.leaderboard[2]=this.id;} // Check if 3rd Position is vacant
//                            }
//                        }
//                    }
//                }
//            }
//        }


        public void gamePlay2() throws InterruptedException {
        	String[] tempBoard;
            GameInfo.Result matchResult = GameLogic.evaluateMatch( localGInfo.board);

            synchronized(matches) {
	            matches.replace(id, matches.get(id), localGInfo.board);
	            matchViewCallback.accept(matches);
	            if(matchResult != null){
	                listCallback.accept("Client #" + id + "'s match resulted in a " + matchResult.toString());
	                localGInfo.result = matchResult;
	                localGInfo.aiMove = -1;
	                localGInfo.messageID = MessageType.END_GAME;
	                send();
                    if (matchResult.toString().equals("WIN")){
                        updateLeaderBoard();
                    }
	                return;
	        	}
	        	
	        	synchronized(this) {
		    		synchronized(minMaxAlgr) {
		                minMaxAlgr.setCurrState(localGInfo.board);
		                minMaxAlgr.currState = localGInfo.board;
		                minMaxAlgr.diff = localGInfo.diff;
		                minMaxAlgr.caller = this;
		                minMaxAlgr.notify();
		    		}
		    		this.wait();
	                tempBoard = minMaxAlgr.boardWithAIMove;
	        	}
	        	
	        	//Set aiMove
	            for(int i = 0; i < 9; i++){
	                if(!tempBoard[i].equals(localGInfo.board[i])){
	                    localGInfo.aiMove = i;
	                    break;
	                }
	            }
	            
	            localGInfo.board = tempBoard;
	            localGInfo.messageID = MessageType.PLAY;
	            
	
	            matches.replace(id, matches.get(id), tempBoard);
	            matchViewCallback.accept(matches);
	
	            matchResult = GameLogic.evaluateMatch(localGInfo.board);
	        
	            //test if AI's move ended game
	            if(matchResult != null){
	                listCallback.accept("Client #" + id + "'s match resulted in a " + matchResult.toString());
	                localGInfo.result = matchResult;
	                localGInfo.messageID = MessageType.END_GAME;
	                send();
                    if (matchResult.toString().equals("WIN")){
                        updateLeaderBoard();
                    }
	                return;
	        	}
	        
	            // add CPU result to board
	            //check gameover
	            //localGInfo.messageID = MessageType.PLAY; // if game continues: MessageType.PLAY | else: MessageType.END_GAME and remove match from matches (HashMap)
	            
	            send(); 
            }
        }

        // notify observers the current leaderboard
        @Override
        public void update(GameInfo masterGInfo) {
            try {
                synchronized(masterGInfo) {
                    localGInfo.leaderboard = masterGInfo.leaderboard;
                }
                localGInfo.messageID = MessageType.NOTIFY;
				send();
			}
			catch(Exception e) {
				System.out.println("Failed to update client #" + id + ": " + e);
				e.printStackTrace();
			}
        }

        // method to send GameInfo object to client
        public void send() {
            try {
                out.flush();
                out.reset();
                out.writeObject(localGInfo);
            } catch (Exception e) {
                System.out.println("Failed to send GameInfo to client #" + id);
                e.printStackTrace();
            }
        }
    } // end of ClientThread
}
