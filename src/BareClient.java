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
			sc.sendNewGameReq("129.89.25.125");
			while(!sc.isConnected());
			sc.recNewGameReply();
			sc.sendPieceMove(p);
			sc.recPieceMove();
			sc.closeConnection();
			
			/*SocketConnection sc = new SocketConnection(false, "129.89.25.125", 5678);
			//print("connected");
			sc.closeConnection();*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void print(String s) {
		System.out.println(s);
	}
}
