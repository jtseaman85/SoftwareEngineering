import java.awt.Point;
import java.io.IOException;
import java.net.UnknownHostException;

/**
 * @author John Seaman
 *
 */
public class SocketCom {

	private static SocketConnection myConn = null;
	private Point[] coordinates;
	private boolean reply;
	private String message;
	private boolean draw = false;
	private boolean resign = false;
	private boolean save = false;
	
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
	
	public boolean isDraw() {
		return draw;
	}
	
	public boolean isResign() {
		return resign;
	}
	
	public boolean isSave() {
		return save;
	}
	
	public void resetInterrupts() {
		draw = false;
		resign = false;
		save = false;
	}
	
	/**
	 * Return true if the socket connection is currently connected.
	 * @return true if socket is connected.
	 */
	public boolean isConnected() {
		return myConn.isConnected();
	}
	
	//TODO: Detailed error handling
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
		debug_print("sending new game request");
	}
	
	//TODO: Detailed error handling
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
	
	//TODO: Detailed error handling
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
		}
		debug_print("sending new game reply: " + str);
		return str;
	}
	
	/**
	 * Listens for an accept/decline reply from the server for a new game request.
	 * @return true if the server accepted the request, false if declined or error occurs.
	 * @throws IOException - An I/O error occurred when receiving the data.
	 */
	public boolean recNewGameReply() {
		// Wait for something to be received
		while(!myConn.hasIncoming());
		
		String str = myConn.receive();
		debug_print("recieve new game reply: " + str);
		
		int cmd = getCommand(str);
		
		if (cmd != Command.NEW.toInt()) {
			printError("invalid command in recNewGameReply. expected command: " + Command.NEW.toInt() + " but got: " + cmd);
			//return false;
		}
		
		int param = getReply(str);
		if (param == 1) {
			return true;
		} else if (param == 0) {
			return false;
		} else {
			// Invalid reply value
			printError("invalid reply value in recNewGameReply. expected: {0,1} but got " + param);
			return false;
		}
	}
	
	//TODO: Detailed error handling
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
		debug_print("send piece move: " + str);
		return str;
	}
	
	//TODO: Detailed error handling
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
		
		int cmd = getCommand(str);
		
		if (handleUnexpectedCommand(cmd)) {
			// Command is not a piece move command
			return null;
		}
		
		if (cmd != Command.MOVE.toInt()) {
			// Invalid command code, throw error
		}
		
		debug_print("recieve piece move: " + str);
		Point move[] = getCoordinates(str);
		
		// TODO: If statement to check valid coordinate values for board
		
		return move;
	}
	
	public String sendDrawReq() {
		
		String str = Command.DRAW.toString();
		
		try {
			sendData(str);
			debug_print("send draw request: " + str);
		} catch (IOException e) {
			printError("IOException occurred in sendDrawReq");
		}

		return str;
	}
	
	public String sendDrawReply(boolean choice) throws IOException {
		
		String str = Command.DRAW.toString(); 
		
		if (choice) {
			str += Parameter.ACCEPT.toString();
			sendData(str);
		} else {
			str += Parameter.DECLINE.toString();
			sendData(str);
		}
		debug_print("sending draw reply: " + str);
		return str;
	}
	
	public boolean recDrawReply() {
		
		// Wait for something to be received
		while(!myConn.hasIncoming());
		
		String str = myConn.receive();
		debug_print("recieve draw reply: " + str);
		
		int cmd = getCommand(str);
		
		if (cmd != Command.DRAW.toInt()) {
			printError("invalid command in recDrawReply. expected command: " + Command.DRAW.toInt() + " but got: " + cmd);
			return false;
		}
		
		int param = getReply(str);
		if (param == 1) {
			return true;
		} else if (param == 0) {
			return false;
		} else {
			// Invalid reply value
			printError("invalid reply value in recDrawReply. expected: {0,1} but got " + param);
			return false;
		}
	}
	
	/**
	 * Checks for any of the game interrupt commands that can be sent at any time.
	 * Should be checked inside every receive method except recNewGameReply.
	 * @param i - Command integer value to check
	 * @return true if an interrupt is detected, false if not.
	 */
	private boolean handleUnexpectedCommand(int i) {
		
		if (i == Command.DRAW.toInt()) {
			System.out.println("received a draw interrupt");
			draw = true;
			return true;
		} else if (i == Command.RESIGN.toInt()) {
			System.out.println("received a resign interrupt");
			resign = true;
			return true;
		} else if (i == Command.SAVE.toInt()) {
			System.out.println("received a save interrupt");
			save = true;
			return true;
		} else {
			return false;
		}
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
		//return Integer.valueOf(s.charAt(0));
		s = s.substring(0, 1);
		return Integer.valueOf(s);
	}
	
	private int getReply(String s) {
		//return Integer.valueOf(s.charAt(1));
		s = s.substring(1, 2);
		return Integer.valueOf(s);
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
    	System.out.println("SocketCom Error: " + s);
    }
	
	private void debug_print(String s) {
		System.out.println(s);
	}
}
