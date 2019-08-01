import java.io.*;
import java.net.*;
import java.util.*;

public class ClientMannager{

	private ClientMannager(){

	}

	public static Vector<connSocket> sockets = new Vector<>();

	public static void sendAll(connSocket cs, String message){
		for (connSocket socket : sockets){
			if(!cs.equals(socket)){
				socket.send(message);
			}
		}
	}


}
