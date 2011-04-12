import java.awt.Point;
import java.io.IOException;
import java.net.UnknownHostException;

public class BareClient {
	public static void main(String[] args) {
		String a = "test from client";

		print("Client:");
		
		SocketCom sc = new SocketCom();
		
		Point[] p = new Point[2];
		p[0] = new Point(5,7);
		p[1] = new Point(6,6);
		
		try {
			sc.sendNewGameReq("192.168.1.104");
			while(!sc.isConnected());
			sc.recNewGameReply();
			sc.recPieceMove();
			
			// Supposed to sendPieceMove, but request Draw instead
			sc.sendDrawReq();
			sc.recDrawReply();
			
			sc.closeConnection();
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void print(String s) {
		System.out.println(s);
	}
}
