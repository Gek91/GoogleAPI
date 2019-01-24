package GoogleAPI;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

public interface GmailGoogleApi {

	/*
	 * Messagge
	 */
	public ListMessagesResponse listMessages(String executionGoogleUser, Date after, Date before, List<String> labelIds, List<String> labelsToIgnore, List<String> senderToIgnore, String nextPageToken);

	public Map<String, Message> retrieveMessagesDetails(String executionGoogleUser, List<String> emailIds, String fields);
	
	public Map<String, Message> searchMessagesByUid(String executionGoogleUser, List<String> messageUids);
	
	/*
	 * Label
	 */
	public void modifyMessagesLabels(String executionGoogleUser, List<String> emailIds, List<String> addLabelIds, List<String> removeLabelIds);
	
	public List<Label> listLabels(String executionGoogleUser);
	
	public String createGmailLabel(String executionGoogleUser, String labelName) ;
	
	public String updateGmailLabel(String executionGoogleUser, String labelId, String newLabelName);
}
