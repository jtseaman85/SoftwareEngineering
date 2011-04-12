import java.awt.Point;
import java.io.IOException;
import java.net.UnknownHostException;


public class BareServer {
	public static void main(String[] args) {
		
		String a = "test from server";

		print("Server:");
		
		SocketCom sc = new SocketCom();
		
		Point[] q = new Point[2];
		Point[] p = new Point[2];
		p[0] = new Point(1,1);
		p[1] = new Point(2,2);
		
		sc.recNewGameReq();
		
		while(!sc.isConnected());
		System.out.println("recieved new game request");
		try {
			sc.sendNewGameReply(true);
			sc.sendPieceMove(p);
			
			q = sc.recPieceMove();
			// If recPieceMove returns null, an interrupt command occurred
			if (q == null) {
				// Check to see if the interrupt was a Draw request
				if (sc.isDraw()) {
					// Handle the draw request accordingly
					sc.sendDrawReply(true);
					// Reset all of the interrupt flags in SocketCom
					sc.resetInterrupts();
				}
			}
			sc.closeConnection();
		} catch (IOException e) {
			print("Error");
		}
		
		
	}
	
	public static void print(String s) {
		System.out.println(s);
	}
}
