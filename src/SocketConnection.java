import java.net.*;
import java.io.*;

/**
 * @author John Seaman
 *
 */
public class SocketConnection {
	
	private boolean isHost = false;
	private Socket socket = null;
	private ServerSocket hostSocket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	
	/**
	 * @param ishost - if this is the server or client socket.
	 */
	public SocketConnection(boolean ishost) {
		this.isHost = ishost;
	}
	
	public boolean isHost() { return isHost; }
	
	public void setIsHost(boolean b) {
		isHost = b;
	}
	
	/**
	 * Attempts to make a socket connection.
	 * @param ip - IP address of the partner socket
	 * @param port - TCP port value
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void connect(String ip, int port) throws UnknownHostException, IOException {
		// Create socket connection
		if (isHost) {
			hostSocket = new ServerSocket(port);
			
			// Blocks until a connection request has been made
			socket = hostSocket.accept();
		} else {
			 socket = new Socket(ip, port);
		}
		
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	
	/**
	 * Says if the socket connection has incoming data to be received.
	 * @return True if the socket has incoming data to be received, false otherwise.
	 * @throws IOException
	 */
	public boolean hasIncoming() throws IOException {
		return in.ready();
	}
	
	/**
	 * Gets the incoming data from the socket connection.
	 * @return A string of the incoming data in the text format
	 * @throws IOException
	 */
	public String receive() throws IOException {
		if (in.ready()) {
			return in.readLine();
		}
		
		return "";
	}
	
	/**
	 * Sends outgoing data via the socket connection.
	 * @param outgoing - String to be sent out via socket connection
	 * @throws IOException
	 */
	public void send(String outgoing) throws IOException {
		out.print(outgoing);
		out.flush();
	}
}

