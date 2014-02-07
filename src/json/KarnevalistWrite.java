package json;

public class KarnevalistWrite {
	String token;
	UserWrite karnevalist;
	public KarnevalistWrite(UserWrite karnevalist){
		this.karnevalist = karnevalist;
		this.token = karnevalist.token;
	}
}