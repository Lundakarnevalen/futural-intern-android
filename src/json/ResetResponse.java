package json;

public class ResetResponse {
	public boolean success;
	public String[] errors;
	
	public String toString() {
		return "Success: " + success + "  Errors: " + errors;
	}
}
