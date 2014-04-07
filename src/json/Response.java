package json;

import android.util.Log;

import com.google.gson.Gson;

public class Response {
	/**
	 * Json when form is submited. 
	 * @author alexander
	 *
	 */
	public static class SubmitRegistrationForm{
		public String status, token, id;
	}
	
	public static class GetKarnevalist{
		public String status;
		public User karnevalist;
	}
	
	public static class GetKarnevalistSpecial{
		public String status;
		public String token;
		public User karnevalist;
	}
	
	public static class PostKarnevalist{
		public String status, token, message;
		public int id;
	}
	
	public static class ErrorResponse{
		public String message, status;
	}
	
	public static class StdResponse{
		public String message, status;
	}
	
	public static class TokenReg{
		public String status, token;
	}
	
	public static class Message {
		public String status, notifications;
		public int id;
	}
	
	public static class Notifications {
		public String status;
		public int records;
		public boolean remaining;
		public String notifications;
		public Notification[] messages;
		
		public void parseMessages() {
			Log.d("Notification", notifications);
			Gson gson = new Gson();
			this.messages = gson.fromJson(notifications, Notification[].class);
		}
	}
	
	
	
	public static class GetGCM{
	}
}
