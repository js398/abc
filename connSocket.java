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
				System.out.println(uname + " send request type: " + type);
				switch ((int)type){
					case 0: handle_message((String)json.get("message"),(int)(long)json.get("aid"));
						break;
					//1->login 
					case 1:	uname = (String)json.get("name");
						do_login();
						break;
					//2->doActCreate					
					case 2: doActCreate(new Activity(json));
						break;
					//3->response full
					case 3: responseFull((int)(long)json.get("aid"));
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

	public void handle_message(String m, int aid){
		try {
			int id = 0;
			query = "SELECT ID FROM `318User` WHERE Username="+uname;
			ResultSet rs = ju.doSelect(query);
			if(rs.next()){
				id = rs.getInt("ID");
			}

			query = "INSERT INTO `318Message`( `UID`, `Content`, `AID`) VALUES ("+id+",'"+m+"',"+aid+")";
			ju.doUpdate(query);

			JSONObject aj = new JSONObject();
			aj.put("type",new Integer(0));
			aj.put("aid",new Integer(aid));
			ClientMannager.sendAll(this,aj.toString());
			send(aj.toString());

		
		}catch (Exception e) {
            		e.printStackTrace();
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

	public void doActCreate(Activity a){
		try {
		System.out.println(uname + " try to add an new Activity...");

		System.out.println(uname + "'s Activity insert into database...");
		//insert int database
		query = "INSERT INTO `318Activity`(`Title`, `Description`, `Time`, `Difficulty`, `Active`, `latitude`, `longitude`) VALUES ('"+a.title+"','"+a.desc+"','"+a.time+"','"+a.level+"', 1,"+a.lat+","+a.lon+")";
		ju.doUpdate(query);

		query = "SELECT Title, ID, latitude, longitude FROM `318Activity` where latitude="+a.lat+"AND longitude="+a.lon+"AND Active='1'";
		ResultSet rs = ju.doSelect(query);
			if(rs.next()){
				Activity ac = new Activity();
				ac.title = rs.getString("Title");
				ac.id = rs.getInt("ID");
				ac.lat = rs.getDouble("latitude");
				ac.lon = rs.getDouble("longitude");
				JSONObject aj = ac.toJson();
				aj.put("type",new Integer(1));
				
				ClientMannager.sendAll(this,aj.toString());
				send(aj.toString());
			}
			System.out.println("Ask all online clients renew their activities list...");
		}catch (Exception e) {
            			e.printStackTrace();
        	}
	}

	public void responseFull(int uid){
		try {
			System.out.println(uname + " try to request Activity "+uid+"...");
			query = "SELECT 318User.ID,318User.Username, Content FROM 318User , 318Message WHERE 318Message.AID="+uid+" && 318Message.UID=318User.ID";
			ResultSet rs = ju.doSelect(query);
			Activity act = new Activity();
			List<Message> mgs = new ArrayList<>();
			List<User> urs = new ArrayList<>();
			while(rs.next()){
				Message m = new Message();
				m.id = rs.getInt("ID");
				m.sender = rs.getString("Username");
				m.content = rs.getString("Content");
				mgs.add(m);
			}
			query = "SELECT 318User.Username, 318User.ID  FROM 318User, 318UandA, 318Activity WHERE 318UandA.AID="+uid+"&& 318User.ID=318UandA.UID";
			rs = ju.doSelect(query);
			while(rs.next()){
				User u = new User();
				u.id = rs.getInt("ID");
				u.username = rs.getString("Username");
				urs.add(u);
			}

			query = "SELECT * from 318Activity WHERE ID="+uid;
			rs = ju.doSelect(query);
			while(rs.next()){
				act.title = rs.getString("Title");
				act.id = rs.getInt("ID");
				act.lat = rs.getDouble("latitude");
				act.lon = rs.getDouble("longitude");
				act.desc = rs.getString("Description");
				act.level = rs.getString("Difficulty");
				act.time = rs.getString("Time");
			}

			act.users=urs;
			act.messages=mgs;
		
			JSONObject aj = act.toJson();
			aj.put("type",new Integer(2));
			send(act.toJson().toString());
		
		}catch (Exception e) {
            		e.printStackTrace();
        	}
	}

	

}
