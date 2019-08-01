import java.io.*;
import java.net.*;
import java.util.*;
import org.json.simple.JSONObject;

class Message{

	public String content;
	public int id;
	public String sender;
		
	public Message(){
	}

	public Message(JSONObject obj){
		this.id = (int)(long)obj.get("mid");
		this.sender = (String)obj.get("sender");
		this.content = (String)obj.get("content");
	}
	
	public Message(String content, int id, String sender){
		this.content = content;
		this.id = id;
	}

	public JSONObject toJson(){
		JSONObject obj = new JSONObject();
		obj.put("content",content);
		obj.put("mid",id);
		obj.put("sender",sender);
		
		return obj;
	}
}



