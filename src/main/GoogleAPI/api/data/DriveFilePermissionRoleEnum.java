package main.GoogleAPI.api.data;

public enum DriveFilePermissionRoleEnum {

	OWNER("owner"),
	ORGANIZER("organizer"),
	FILE_ORGANIZER("fileOrganizer"),
	WRITE("write"),
	READER("reader");
	
	private String value;
	
	private DriveFilePermissionRoleEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
}
