package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelperEmail {
	
	/**
	 * Validates email address
	 * @param email The address to validate
	 * @return True if it is valid. 
	 */
	public static boolean validEmail(String email){
		String regex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
}
