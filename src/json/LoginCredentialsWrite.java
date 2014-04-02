package json;

public class LoginCredentialsWrite {

	public LoginCredentials user;
	
	public LoginCredentialsWrite(String email, String password) {
		user = new LoginCredentials();
		user.email = email;
		user.password = password;
	}
}
