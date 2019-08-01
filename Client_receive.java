import java.io.*;
import java.net.*;
import java.util.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Client_receive implements Runnable{

	private Socket socket = null;
	private DataInputStream inputStream = null; 
	
	public Client_receive(Socket socket){
		this.socket = socket;
		try {
			inputStream = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run(){
		String receive = null;
		try{
			while(true){
				receive = inputStream.readUTF();
				
				//json praser
				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(receive);
				int type = (int)(long)json.get("type");
				switch (type){
					//0->receive the message from server
					case 0: System.out.println((String)json.get("message"));
						break;
					//1->receive berf activity from server 
					case 1: Client.activity_li.add(new Activity(json));
						break;
					

				}
			}
		}catch (IOException e) {
			e.printStackTrace();
		}catch (ParseException e) {
            		e.printStackTrace();
        	}

	}

}
