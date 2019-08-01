import java.io.*;
import java.net.*;
import java.util.*;

public class Service implements Runnable {
	private ServerSocket serverSocket = null;
	private int portNum;
	
	public Service(int portnumber){
		portNum = portnumber;
	}

	@Override
	public void run(){
		try{
			serverSocket = new ServerSocket(portNum);
			while(true){
				Socket socket = serverSocket.accept();
				connSocket conn = new connSocket(socket);
				ClientMannager.sockets.add(conn);
				Thread thread = new Thread(conn);
				thread.start();
			}
		}catch (IOException e) {
			e.printStackTrace();
			System.out.println("Server down!");
			
		}

	}

}

