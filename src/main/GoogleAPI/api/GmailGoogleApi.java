package main.GoogleAPI.api;

import java.util.List;

import javax.mail.internet.MimeMessage;

import com.google.api.services.gmail.model.Draft;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.ListThreadsResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.Thread;

import main.GoogleAPI.api.data.MailLabelListVisibilityEnum;
import main.GoogleAPI.api.data.MailMessageListVisibilityEnum;

public interface GmailGoogleApi {

	/*
	 * Message
	 */
	//TODO: too many parameters
	public ListMessagesResponse getMessageList(String executionGoogleUser, String query, List<String> labelIds, String nextPageToken);

	public Message getMessage(String executionGoogleUser, String messageId, String fields);
		
	public Message sendMessage(String executionGoogleUser, MimeMessage message);
	
	public Message updateMessageLabels(String executionGoogleUser, String messagesId, List<String> labelToAddIds, List<String> labelsToRemoveIds);

	public void deleteMessage(String executionGoogleUser, String messagesId);
		
	/*
	 * Draft
	 */
	public Draft getDraft(String executionGoogleUser, String draftId, String fields);
	
	public Draft createDraft(String executionGoogleUser, MimeMessage message);	
	
	public Draft updateDraft(String executionGoogleUser, String draftId, MimeMessage message);
	
	public Message sendDraft(String executionGoogleUser, String draftId);
	
	public void deleteDraft(String executionGoogleUser, String draftId);
	
	/*
	 * Thread
	 */
	public ListThreadsResponse getThreadList(String executionGoogleUser, String query, List<String> labelIds, String pageToken);
	
	public Thread getThread(String executionGoogleUser, String threadId, String fields);
	
	public void deleteThread(String executionGoogleUser, String threadId);
	
	public Thread updateThreadLabels(String executionGoogleUser, String threadId, List<String> labelToAddIds, List<String> labelsToRemoveIds);
	
	/*
	 * Label
	 */
	public ListLabelsResponse getLabelList(String executionGoogleUser);
	
	public Label getLabel(String executionGoogleUser, String labelId, String fields);
	
	public Label createLabel(String executionGoogleUser, String labelName, MailLabelListVisibilityEnum labelListVisibility, MailMessageListVisibilityEnum messageListVisibility);
	
	public Label updateLabel(String executionGoogleUser, String labelId, String newLabelName, MailLabelListVisibilityEnum labelListVisibility, MailMessageListVisibilityEnum messageListVisibility);
	
	public void deleteLabel(String executionGoogleUser, String labelId);
		
}
