import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class connSocket implements Runnable{
	private Socket socket = null;
	private String uname = "";
	private String query = "";
	private DataOutputStream outputStream = null; 
	private DataInputStream inputStream = null; 
	private JDBC_Utility ju;

	public connSocket(Socket socket){
		ju = new JDBC_Utility();
		this.socket = socket;
		try {
			outputStream = new DataOutputStream(socket.getOutputStream());
			inputStream = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(String message){
		try {
			outputStream.writeUTF(message);
			outputStream.flush();
		
		}catch (IOException e) {
	    		System.out.println(e);
		}
	}

	@Override
	public void run(){
		String accept = null;
		
		while(true){
			try {
				accept = inputStream.readUTF();

				//json praser
				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(accept);
				long type = (long)json.get("type");
				System.out.println(type);
				switch ((int)type){
					case 0: ClientMannager.sendAll(this, accept);
						break;
					//1->login 
					case 1:	uname = (String)json.get("name");
						do_login();
						break;
					

				}
				
				

				
		
			}catch (IOException e) {
	    			ClientMannager.sockets.remove(this);
				query = "UPDATE 318User SET `Active`=0 WHERE Username='"+uname+"'";
				ju.doUpdate(query);
				System.out.println(uname + " logged out...");
				return;
			}catch (ParseException e) {
            			e.printStackTrace();
        		}
		}

	}

	public void do_login(){
		try {
			System.out.println(uname + " try to login...");
			query = "SELECT * FROM 318User WHERE Username='"+uname+"'";
			ResultSet rs = ju.doSelect(query);
			if(rs.next()){
				query = "UPDATE 318User SET `Active`=1 WHERE Username='"+uname+"'";
				ju.doUpdate(query);
				System.out.println(uname + " logged in...");

			}else{
				query = "INSERT INTO 318User(`Active`, `Username`) VALUES (1,'"+uname+"')";
				ju.doUpdate(query);
				System.out.println(uname + " resgisted...");
			}	
		
			//send activities to user
			//ask database for activities
			query = "SELECT Title, ID, latitude, longitude FROM `318Activity` where Active='1'";
			rs = ju.doSelect(query);
			System.out.println("Sending active activities to: "+uname);
			while (rs.next()){
				Activity a = new Activity();
				a.title = rs.getString("Title");
				a.id = rs.getInt("ID");
				a.lat = rs.getDouble("latitude");
				a.lon = rs.getDouble("longitude");
				JSONObject aj = a.toJson();
				aj.put("type",new Integer(1));
				
				send(aj.toString());
			}
			System.out.println("Complete sending active activities to: "+uname);	
		}catch (Exception e) {
            			e.printStackTrace();
        	}
	}

	

}
