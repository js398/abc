import java.io.*;
import java.net.*;
import java.util.*;
import org.json.simple.JSONObject;

class User{

	public String username;
	public int id;
	public int active; 
		
	public User(){
	}

	public User(JSONObject obj){
		this.id = (int)(long)obj.get("uid");
		this.username = (String)obj.get("username");
		this.active = (int)(long)obj.get("uactive");
	}
	
	public User(String username, int id, int active){
		this.username = username;
		this.id = id;
		this.active = active;
	}

	public JSONObject toJson(){
		JSONObject obj = new JSONObject();
		obj.put("username",username);
		obj.put("uid",id);
		obj.put("uactive",active);
		
		return obj;
	}
}



