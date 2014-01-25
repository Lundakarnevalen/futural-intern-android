package json;

public class Response {
	/**
	 * Json when form is submited. 
	 * @author alexander
	 *
	 */
	public class SubmitRegistrationForm{
		public String status, token, id;
	}
	
	public class GetKarnevalist{
		public String status;
		public User karnevalist;
	}
}
