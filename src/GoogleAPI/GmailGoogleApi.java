package GoogleAPI;

import java.util.Date;
import java.util.List;

import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

public interface GmailGoogleApi {

	/*
	 * Messagge
	 */
	public ListMessagesResponse getMessages(String executionGoogleUser, Date after, Date before, List<String> labelIds, List<String> labelsToIgnore, List<String> senderToIgnore, String nextPageToken);

	public List<Message> getMessagesDetail(String executionGoogleUser, List<String> emailIds, String fields);
	
	public List<Message> getMessagesByUid(String executionGoogleUser, List<String> messageUids, String fields);
	
	/*
	 * Label
	 */
	public void editMessagesLabels(String executionGoogleUser, List<String> emailIds, List<String> addLabelIds, List<String> removeLabelIds);
	
	public ListLabelsResponse getLabels(String executionGoogleUser);
	
	public Label createLabel(String executionGoogleUser, String labelName) ;
	
	public void editLabel(String executionGoogleUser, String labelId, String newLabelName);
}
