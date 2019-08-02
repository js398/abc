import java.io.*;
import java.net.*;
import java.util.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Activity{

	public int id;
	public String time;
	public String level;
	public double lat;
	public double lon;
	public String title;
	public String desc;
	
	public List<User> users;
	public List<Message> messages;

	
	public Activity(int id,String time,String level,double lat,double lon,String title,String desc){
		this.id = id;
		this.time=time;
		this.level=level;
		this.lat=lat;
		this.lon=lon;
		this.title=title;
		this.desc=desc;
		users = new ArrayList<>();
		messages = new ArrayList<>();
	}
	
	public Activity(){
		users = new ArrayList<>();
		messages = new ArrayList<>();
	}

	public Activity(JSONObject obj){
		this.id = (int)(long)obj.get("id");
		this.time = (String)obj.get("time");
		this.level = (String)obj.get("level");
		this.lat = (double)obj.get("lat");
		this.lon = (double)obj.get("lon");
		this.title = (String)obj.get("title");
		this.desc = (String)obj.get("desc");

		users = new ArrayList<>();
		messages = new ArrayList<>();

		if(!((String)obj.get("users")).equals("NULL")){
			JSONArray jarray = (JSONArray)obj.get("users");
			for (int i=0; i < jarray.size(); i++) {
    				User u = new User((JSONObject)jarray.get(i));
				users.add(u);
			}
		} 

		if(!((String)obj.get("messages")).equals("NULL")){
			JSONArray jarray = (JSONArray)obj.get("messages");
			for (int i=0; i < jarray.size(); i++) {
    				Message m = new Message((JSONObject)jarray.get(i));
				messages.add(m);
			}
		} 
		
		
	}
	
	public JSONObject toJson(){
		JSONObject obj = new JSONObject();
		obj.put("id",id);
		obj.put("time",time);
		obj.put("level",level);
		obj.put("lat",lat);
		obj.put("lon",lon);
		obj.put("title",title);
		obj.put("desc",desc);
		
		if(users.size()>0){

			JSONArray userJ = new JSONArray();
			for(User u : users){
				userJ.add(u.toJson());
			}
			obj.put("users",userJ);
		}else{
			obj.put("users","NULL");
		}

		if(messages.size()>0){
			JSONArray messageJ = new JSONArray();
			for(Message m : messages){
				messageJ.add(m.toJson());
			}
			obj.put("messages",messageJ);
		}else{
			obj.put("messages","NULL");
		}

		return obj;
	}

}
