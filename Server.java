import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

	private static Service service = null;

    	public static void main(String args[]) {
		
		if(args.length != 1) {
			System.out.println("Usage:  server <port>");
			return;
		}
		
        	int portNumber = Integer.parseInt(args[0]);

		// report the localhost IP address
		try{
			InetAddress host = InetAddress.getLocalHost();
			String hostName = host.getHostName();
			System.out.println("Local host is " + host.toString());
			System.out.println("Local host name is " + hostName);
        	}catch (UnknownHostException e) {
			System.out.println("Couldn't get local host address");
			return;
		}

		start_server(portNumber);

	}

	public static void start_server(int portNum){
		service = new Service(portNum);
		Thread thread = new Thread(service);
		thread.start();
	}

	
}
