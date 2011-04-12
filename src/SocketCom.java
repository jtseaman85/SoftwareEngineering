import java.awt.Point;
import java.io.IOException;
import java.net.UnknownHostException;

/**
 * @author John Seaman
 *
 */
public class SocketCom {

	private static SocketConnection myConn = null;
	
	private static enum Command {
		MOVE(0), NEW(1), RESTORE(2), RESIGN(3), SAVE(4), DRAW(5);
		
		private int code;
		
		private Command(int i) {
			code = i;
		}

		@Override
		public String toString() {
			return Integer.toString(code);
		}
	}
	
	private static enum Parameter {
		DECLINE(0), ACCEPT(1), REQUEST(2), COORVAL(3);
		
		private int code;
		
		private Parameter(int i) {
			code = i;
		}
		
		@Override
		public String toString() {
			return Integer.toString(code);
		}
	}
	
	/**
	 * Return true if the socket connection is currently connected.
	 * @return true if socket is connected.
	 */
	public boolean isConnected() {
		return myConn.isConnected();
	}
	
	/**
	 * Attempts to send a request to the server for a new game. A subsequent call to 
	 * {@link #recNewGameReply()} should be made. 
	 * @param ip - IP address of server computer.
	 */
	public void sendNewGameReq(String ip) {
		try {
			myConn = new SocketConnection(false, ip, 5678);
		} catch (UnknownHostException e) {
			System.out.println("Error: ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("sending new game request");
	}
	
	/**
	 * Listens for an incoming request from the client for a new game. A subsequent call to 
	 * {@link #sendNewGameReply(boolean)} should be made. 
	 * @throws UnknownHostException - The client IP address could not be resolved.
	 * @throws IOException - An I/O error occurred between the connection.
	 */
	public void recNewGameReq() {
		try {
			myConn = new SocketConnection(true, "", 5678);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends an accept/decline reply to the client for a new game request.
	 * @param choice - Accept/Decline boolean decision.
	 * @throws IOException - An I/O error occurred when sending the data.
	 */
	public String sendNewGameReply(boolean choice) throws IOException {
		String str = Command.NEW.toString(); 
		
		if (choice) {
			str += Parameter.ACCEPT.toString();
			sendData(str);
		} else {
			str += Parameter.DECLINE.toString();
			sendData(str);
			myConn.closeConnection();
		}
		System.out.println("sending new game reply: " + str);
		return str;
	}
	
	/**
	 * Listens for an accept/decline reply from the server for a new game request.
	 * @return true if the server accepted the request, false if declined.
	 * @throws IOException - An I/O error occurred when receiving the data.
	 */
	public boolean recNewGameReply() {
		while(!myConn.hasIncoming());
		String str = myConn.receive();
		System.out.println("recieve new game reply: " + str);
		
		int cmd = getCommand(str);
		
		if (cmd != Command.NEW.code) {
			// Invalid command code, throw error
		}
		
		int param = getReply(str);
		
		if (param < 0 || param > 1) {
			// Invalid reply parameter, throw error
		}
		
		if (param == 1) {
			return true;
		} else {
			myConn.closeConnection();
			return false;
		}
	}
	
	/**
	 * Send data of a moved piece.
	 * @precondition - The piece move has been validated on the current computer.
	 * @param p - Array of Point objects where p[0] are the coordinates where the piece is moving from
	 * 				and p[1] are the coordinates where the piece is moving to.
	 * @throws IOException  - An I/O error occurred when sending the data.
	 */
	public String sendPieceMove(Point[] p) throws IOException {
		
		String str = Command.MOVE.toString();
		
		// TODO: If statement to check valid coordinate values for board
		
		str += String.valueOf(p[0].x) + String.valueOf(p[0].y);
		str += String.valueOf(p[1].x) + String.valueOf(p[1].y);
		
		sendData(str);
		System.out.println("send piece move: " + str);
		return str;
	}
	
	/**
	 * Listen for data of the next piece move. The validity of the move is implied as the sender
	 * is responsible for validating the piece move.
	 * @return An array of Point objects where p[0] are the coordinated where the piece is moving from
	 * 			and p[1] are the coordinates where the piece is moving to.
	 * @throws IOException - An I/O error occurred when receiving the data.
	 */
	public Point[] recPieceMove() throws IOException {
		while(!myConn.hasIncoming());
		String str = myConn.receive();
		
		/*
		int cmd = getCommand(str);
		
		if (cmd != Command.MOVE.code) {
			// Invalid command code, throw error
		}
		*/
		
		System.out.println("recieve piece move: " + str);
		Point move[] = getCoordinates(str);
		
		// TODO: If statement to check valid coordinate values for board
		
		return move;
	}
	
	/**
	 * Stops the server socket from listening for a connection request. Must be called
	 * before re-instantiating this object.
	 */
	public void stopListening() {
		myConn.stopListening();
	}
	
	public void closeConnection() throws IOException {
		myConn.closeConnection();
	}
	
	private int getCommand(String s) {
		return Integer.valueOf(s.charAt(0));
	}
	
	private int getReply(String s) {
		return Integer.valueOf(s.charAt(1));
	}
	
	private Point[] getCoordinates(String s) {
		Point from = new Point(Integer.valueOf(s.charAt(1)), Integer.valueOf(s.charAt(2)));
		Point to = new Point(Integer.valueOf(s.charAt(3)), Integer.valueOf(s.charAt(4)));

		Point[] coors = new Point[2];
		
		coors[0] = from;
		coors[1] = to;
		
		return coors;
	}
	
	private void sendData(String str) throws IOException {
		myConn.send(str);
	}
	
	private void printError(String s) {
    	System.out.println("SocketConnection Error: " + s);
    }
}
