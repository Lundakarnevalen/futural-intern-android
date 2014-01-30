package json;

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
	
	public static class PostKarnevalist{
		public String status, token, message;
		public int id;
	}
	
	public static class ErrorResponse{
		public String message, status;
	}
	
	public static class GetGCM{
		
	}
}
