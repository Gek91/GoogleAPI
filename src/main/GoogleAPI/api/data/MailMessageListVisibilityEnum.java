package main.GoogleAPI.api.data;

public enum MailMessageListVisibilityEnum {
	
	LABEL_HIDE("labelHide"),
	LABEL_SHOW("labelShow"),
	LABEL_SHOW_IF_UNREAD("labelShowIfUnread");
	
	private String value;
	
	private MailMessageListVisibilityEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	
}
