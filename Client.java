import java.io.*;
import java.net.*;
import java.util.*;
import org.json.simple.JSONObject;

public class Client {

	private static DataOutputStream dOut;
	private static DataInputStream dIn;
	private static Socket socket;
	public static List<Activity> activity_li;

    	public static void main(String[] args) {
		activity_li = new ArrayList<>();
		if (args.length != 2) {
	    		System.out.println("Usage:  client <host> <port>");
	    		return;
		}
		
        	String hostName = args[0];
        	int portNumber = Integer.parseInt(args[1]);

		try {
			socket = new Socket(hostName, portNumber);
			dOut = new DataOutputStream(socket.getOutputStream());
			Client_receive cr = new Client_receive(socket);
			Thread thread = new Thread(cr);
			thread.start();

			login("ZhenSao");

	    		while(true){
		    		Scanner myObj = new Scanner(System.in);
		    		String message = myObj.nextLine();
				sendMessage(message);		
				System.out.println("receive: " + activity_li.size() + " activities...");	
				System.out.println("activity 1 title: " + activity_li.get(0).id);		    
			}

		}catch (IOException e) {
	    		e.printStackTrace();
		}
    	}

	public static void sendJson(JSONObject obj){
		try {
			String message = obj.toString();
      			dOut.writeUTF(message);
			dOut.flush();
		}catch (IOException e) {
	    		e.printStackTrace();
		}
	}

	public static void login(String uname){	
		JSONObject obj = new JSONObject();
      		obj.put("type",new Integer(1));
      		obj.put("name",uname);

		sendJson(obj);
	}

	public static void sendMessage(String message){	
		JSONObject obj = new JSONObject();
      		obj.put("type",new Integer(0));
      		obj.put("message",message);

		sendJson(obj);
	}
}
