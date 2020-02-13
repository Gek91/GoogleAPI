package main.GoogleAPI.data;

public enum CalendarAclRoleEnum {

	NONE("none"),
	FREE_BUSY_READER("freeBusyReader"),
	READER("reader"),
	WRITER("writer"),
	OWNER("owner");
	
	private String value;
	
	private CalendarAclRoleEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	
}
