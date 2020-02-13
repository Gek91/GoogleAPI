package main.GoogleAPI.data;

public enum CalendarMinAccessRoleEnum {
	FREE_BUDY_READER("freeBusyReader"),
	OWNER("owner"),
	READER("reader"),
	WRITER("writer");
	
	private String value;
	
	private CalendarMinAccessRoleEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
}
