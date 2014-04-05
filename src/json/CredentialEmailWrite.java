package json;

public class CredentialEmailWrite {
	CredentialEmail user;
	
	public CredentialEmailWrite(String email) {
		user = new CredentialEmail();
		user.email = email;
	}
}
