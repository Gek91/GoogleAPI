package main.GoogleAPI.data;

public enum DriveFilePermissionTypeEnum {

	USER("user"),
	GROUP("group"),
	DOMAIN("domain"),
	ANYONE("anyone");
	
	private String value;
	
	private DriveFilePermissionTypeEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
}
