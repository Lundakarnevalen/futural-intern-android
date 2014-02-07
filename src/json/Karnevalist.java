package json;

public class Karnevalist {
	String token;
	User karnevalist;
	public Karnevalist(User karnevalist){
		this.karnevalist = karnevalist;
		this.token = karnevalist.token;
	}
}