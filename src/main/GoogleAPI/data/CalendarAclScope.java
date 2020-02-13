package main.GoogleAPI.data;

public enum CalendarAclScope {

	PUBLIC("default"),
	USER("user"),
	GROUP("group"),
	DOMAIN("domain");
	
	private String value;
	
	private CalendarAclScope(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
}
