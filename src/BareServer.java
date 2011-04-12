import java.awt.Point;
import java.io.IOException;
import java.net.UnknownHostException;


public class BareServer {
	public static void main(String[] args) {
		
		String a = "test from server";

		print("Server:");
		
		SocketCom sc = new SocketCom();
		
		Point[] p = new Point[2];
		p[0] = new Point(1,1);
		p[1] = new Point(2,2);
		
		
		sc.recNewGameReq();
		
		sc.stopListening();
		
		//while(!sc.isConnected());
		/*System.out.println("recieved new game request");
		sc.sendNewGameReply(true);
		sc.sendPieceMove(p);
		sc.recPieceMove();
		sc.closeConnection();*/
		
	}
	
	public static void print(String s) {
		System.out.println(s);
	}
}
