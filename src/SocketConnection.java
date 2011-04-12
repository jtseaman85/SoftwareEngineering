import java.net.*;
import java.nio.channels.ClosedByInterruptException;
import java.util.NoSuchElementException;
import java.io.*;

/**
 * @author John Seaman
 *
 */
public class SocketConnection implements Runnable {

	private boolean isHost = false;
	private String ip;
	private int port;
	private Socket socket = null;
	private volatile Thread dataThread = null;
	private StringBuffer toSend = new StringBuffer("");
	private StringBuffer toReceive = new StringBuffer("");
	private BufferedReader in = null;
	private PrintWriter out = null;
	private final String TERMINATE_CODE = "!";
	private boolean terminate = false;
	
	public boolean isHost() { return isHost; }
	public boolean isConnected() { return (socket != null && socket.isConnected()); }
	public boolean isTerminated() { return terminate; }
	
	/**
	 * Constructor for a SocketConnection. If intended to be a server socket, pass in
	 * a blank string for the IP address. 
	 * @param b - true if this instance intended to be a server socket, false if client
	 * @param s - IP address of server. Leave blank if intended to be a server socket
	 * @param i - TCP port of socket connection
	 * @throws IOException
	 */
	public SocketConnection(boolean b, String s, int i) throws IOException {
		isHost = b;
		ip = s;
		port = i;
		// Start new dedicated thread to handle send/receive data
		dataThread = new Thread(this);
		dataThread.start();
	}
	
	/**
	 * To determine whether there is arriving data to be processed
	 * @return - true if there is incoming socket data
	 */
	public boolean hasIncoming() {
		return (toReceive.length() > 0);
	}
	
	/**
	 * Closes the open socket connection.
	 */
	public void closeConnection() {
		terminate = true;
	}
	
	/**
	 * Gets the incoming data from the socket connection. If the data contains
	 * more than one command, extracts the first command and leaves the rest in the buffer.
	 * @return A string of the incoming data in text format excluding its newline char
	 * @throws NoSuchElementException - when the incoming data buffer is empty
	 */
	public String receive() throws NoSuchElementException {
		synchronized (toReceive) {
			if (toReceive.length() == 0) {
				throw new NoSuchElementException("incoming data buffer is empty");
			}
			String rest;
			
			int nextEndline = toReceive.indexOf("\n");
			String s = toReceive.substring(0, nextEndline);
			
			if (nextEndline < toReceive.length() - 1) {
				rest = toReceive.substring(nextEndline + 1);
				toReceive.setLength(0);
				toReceive.append(rest);
			} else {
				toReceive.setLength(0);
			}
			return s;
		}
	}
	
	/**
	 * Sends outgoing data via the socket connection.
	 * @param s - String to be sent out via socket connection
	 * @throws IOException - Failed to write data to socket output
	 */
	public void send(String s) throws IOException {
		synchronized (toSend) {
			toSend.append(s + "\n");
		}
	}
	
	/**
	 * Stops the server socket from listening for a connection request. Must be called
	 * before re-instantiating this object.
	 */
	public void stopListening() {
		dataThread.interrupt();
	}

    @Override
    /**
     * The dedicated thread of the SocketConnection object. Attempts to make a socket connection
     * using either a client or server socket. If a server socket, can be killed prior to connection
     * using the SocketConnection.stopListening method. After connection loops infinitely, 
     * sending and receiving data from the socket in/out streams.
     */
    public void run() {
    	try {
    		if (isHost) {
	    		ServerSocket hostSocket = new ServerSocket(port);
	    		hostSocket.setSoTimeout(10);
	    		while (true) {
	    			try {
	    				socket = hostSocket.accept();
	    				//System.out.println("connected");
	    				break;
	    			} catch (SocketTimeoutException e) {
	    				//System.out.println("timed out, checking interrupt");
	    				if (Thread.interrupted()) {
	    					//System.out.println("interrupted...");
	    					terminate = true;
	    					hostSocket.close();
	    					break;
	    				}
	    			}
	    		}
	    	} else {
	    		socket = new Socket(ip, port);
	    		//System.out.println("connected");
	    	}
    	} catch (IOException e) {
			System.out.println("Error occurred during socket connection");
			//e.printStackTrace();
		}
    	
    	if (!terminate) {
    		//System.out.println("inside !terminate");
    		try {
    			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    			out = new PrintWriter(socket.getOutputStream(), true);
    			String s;
    			
    			while(!terminate || (terminate && ((toSend.length() != 0) || in.ready()))) {
    				// Send data
    				if (toSend.length() != 0) {
    					s = toSend.toString();
    					out.print(s);
    					out.flush();
    					if (s.contains(TERMINATE_CODE)) {
    						terminate = true;
    					}
    					toSend.setLength(0);
    				}

    				// Receive data
    				if (in.ready()) {
    					s = in.readLine();
    					if ((s != null) &&  (s.length() != 0)) {
    						// Check if it is the end of a transmission
    						if (s.contains(TERMINATE_CODE)) {
    							terminate = true;
    						}
    						// Otherwise, receive what text
    						else {
    							appendToReceive(s + "\n");
    						}
    					}
    				}
    			}
    			in.close();
    			out.close();
    			socket.close();
    		} catch (IOException e) {
    			System.out.println("I/O stream error in socket thread");
    			System.exit(1);
    		}
    	}
    }
    
    /**
     * Thread safe way to dump socket input stream data into a 
     * StringBuffer outside of the running thread
     * @param s - input string to append to the StringBuffer
     */
    private void appendToReceive(String s) {
    	synchronized (toReceive) {
    		toReceive.append(s);
    	}
    }
    
    
}