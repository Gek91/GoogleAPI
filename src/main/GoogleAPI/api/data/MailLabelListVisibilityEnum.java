package main.GoogleAPI.api.data;

public enum MailLabelListVisibilityEnum {

	HIDE("hide"),
	SHOW("show");
	
	private String value;
	
	private MailLabelListVisibilityEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	
}
