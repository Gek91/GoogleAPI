package main.GoogleAPI.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.Gmail.Users.Messages.Get;
import com.google.api.services.gmail.Gmail.Users.Messages.Modify;
import com.google.api.services.gmail.Gmail.Users.Messages.Send;
import com.google.api.services.gmail.Gmail.Users.Messages.Trash;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Draft;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.ListThreadsResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.ModifyMessageRequest;
import com.google.api.services.gmail.model.ModifyThreadRequest;
import com.google.api.services.gmail.model.Thread;

import main.GoogleAPI.GmailGoogleApi;
import main.GoogleAPI.common.AbstractBaseGoogleApi;
import main.GoogleAPI.common.AbstractBaseGoogleAuthentication;
import main.GoogleAPI.common.AbstractGoogleServiceBatchRequest;
import main.GoogleAPI.data.MailLabelListVisibilityEnum;
import main.GoogleAPI.data.MailMessageListVisibilityEnum;

public class GmailGoogleApiImp extends AbstractBaseGoogleApi<Gmail> implements GmailGoogleApi{

	private static final List<String> SCOPES = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add(GmailScopes.GMAIL_MODIFY);
			add(GmailScopes.GMAIL_SEND);
		}
	};
	
	@Override
	protected Collection<String> getScopes() {
		return SCOPES;
	}

	@Override
	protected Gmail buildGoogleService(HttpTransport httpTransport, JsonFactory jacksonFactory, HttpRequestInitializer requestInitializer) {
		return new Gmail(httpTransport, jacksonFactory, requestInitializer);
	}
	
	private Gmail getGmailGoogleService(String executionGoogleUser) {
		return getGoogleService(executionGoogleUser);
	}
	
	//constructor
	public GmailGoogleApiImp(AbstractBaseGoogleAuthentication authenticationService) {
		super(authenticationService);
	}
	
	@Override
	public GmailBatchBuilder getBatchBuilder(String executionGoogleUser) {
		
		return new GmailBatchBuilder(executionGoogleUser, getGmailGoogleService(executionGoogleUser));
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Methods
	
	@Override
	public ListMessagesResponse getMessageList(String executionGoogleUser, String query,  List<String> labelIds, String nextPageToken) {
		
		ListMessagesResponse response = null;
		
		try {
		
			getLogger().info("GMail APIs - START getMessageList | query:{} | nextPageToken:{}", query, nextPageToken );
				
			response = getMessageListOperation(executionGoogleUser, query, labelIds, nextPageToken).execute();
			
			getLogger().info("GMail APIs - END getMessageList");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("GMail APIs - Google service error in getMessageList.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GMail - Critical error in getMessageList.", e);
			throw new RuntimeException(e);
		}
							
		return response;
	}
		
	@Override
	public Message getMessage(String executionGoogleUser, String messageId, String fields) {
			
		Message result = null;
		
		try {
			getLogger().info("Gmail APIs - START getMessage | messageId:{}.", messageId);

			result = getMessageOperation(executionGoogleUser, messageId, fields).execute();
			
			getLogger().info("Gmail APIs - END getMessage | messageId:{}.");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Gmail APIs - Google service error in getMessage.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Gmail APIs - Critical error in getMessage.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}
	
	@Override
	public Message sendMessage(String executionGoogleUser, MimeMessage message) {

		Message result = null;
		
		try {
			getLogger().info("Gmail APIs - START sendMessage.");

			result = sendMessaggeOperation(executionGoogleUser, message).execute();
			
			getLogger().info("Gmail APIs - END sendMessage.");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Gmail APIs - Google service error in sendMessage.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Gmail APIs - Critical error in sendMessage.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}

	@Override
	public Message updateMessageLabels(String executionGoogleUser, String messageId, List<String> addLabelIds, List<String> removeLabelIds) {
		
		Message result = null;
		
		try {
			
			getLogger().info("GMail APIs - START editMessageLabels | messageId:{}", messageId);

			result = updateMessageLabelsOperation(executionGoogleUser, messageId, addLabelIds, removeLabelIds).execute();
			
			getLogger().info("Gmail APIs - END editMessageLabels | messageId:{}.");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("GMail APIs - Google service error in editMessageLabels.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GMail APIs - Critical error in editMessageLabels.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}
	
	@Override
	public void deleteMessage(String executionGoogleUser, String messageId) {
		
		try {
			getLogger().info("GMail APIs - START deleteMessage | messageId:{}", messageId);

			trashMessageOperation(executionGoogleUser, messageId).execute();
			
			getLogger().info("GMail APIs - END deleteMessage | messageId:{}", messageId);
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("GMail APIs - Google service error in editMessageLabels.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GMail APIs - Critical error in editMessageLabels.", e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Draft getDraft(String executionGoogleUser, String draftId, String fields) {
		
		Draft response = null;
		
		try {
			
			getLogger().info("GMail APIs - START getDraft");
			
			response = getDraftOperation(executionGoogleUser, draftId, fields).execute();
			
			getLogger().info("GMail APIs - END getDraft");
	
		} catch(GoogleJsonResponseException e) {
			getLogger().error("GMail APIs - Google service error in getDraft.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GMail APIs - Critical error in getDraft.", e);
			throw new RuntimeException(e);
		}
		
		return response;
	}
	
	@Override
	public Draft createDraft(String executionGoogleUser, MimeMessage message) {
		
		Draft response = null;
		
		try {
			
			getLogger().info("GMail APIs - START createDraft");

			response = createDraftOperation(executionGoogleUser, message).execute();
			
			getLogger().info("GMail APIs - END createDraft");
	
		} catch(GoogleJsonResponseException e) {
			getLogger().error("GMail APIs - Google service error in createDraft.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GMail APIs - Critical error in createDraft.", e);
			throw new RuntimeException(e);
		}
		
		return response;
	}

	@Override
	public Draft updateDraft(String executionGoogleUser, String draftId, MimeMessage message) {
		
		Draft response = null;
		
		try {
			
			getLogger().info("GMail APIs - START updateDraft | draftId:{}", draftId);

			response = updateDraftOperation(executionGoogleUser, draftId, message).execute();
			
			getLogger().info("GMail APIs - END updateDraft");
	
		} catch(GoogleJsonResponseException e) {
			getLogger().error("GMail APIs - Google service error in updateDraft.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GMail APIs - Critical error in updateDraft.", e);
			throw new RuntimeException(e);
		}
		
		return response;
	}
	
	public Message sendDraft(String executionGoogleUser, String draftId) {
		
		Message response = null;
		
		try {
						
			getLogger().info("GMail APIs - START sendDraft | draftId:{}", draftId);
			
			response = sendDraftOperation(executionGoogleUser, draftId).execute();
			
			getLogger().info("GMail APIs - END sendDraft");
	
		} catch(GoogleJsonResponseException e) {
			getLogger().error("GMail APIs - Google service error in sendDraft.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GMail APIs - Critical error in sendDraft.", e);
			throw new RuntimeException(e);
		}
		
		return response;
	}
	
	@Override
	public void deleteDraft(String executionGoogleUser, String draftId) {
				
		try {
			
			getLogger().info("GMail APIs - START deleteDraft | draftId:{}", draftId);
			
			deleteDraftOperation(executionGoogleUser, draftId).execute();
			
			getLogger().info("GMail APIs - END deleteDraft");
	
		} catch(GoogleJsonResponseException e) {
			getLogger().error("GMail APIs - Google service error in deleteDraft.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GMail APIs - Critical error in deleteDraft.", e);
			throw new RuntimeException(e);
		}
		
	}
	
	@Override 
	public ListThreadsResponse getThreadList(String executionGoogleUser, String query, List<String> labelIds, String pageToken) {
		
		ListThreadsResponse response = null;
		
		try {
			
			getLogger().info("GMail APIs - START getThreadList");
			
			response = getThreadListOperation(executionGoogleUser, query, labelIds, pageToken).execute();
			
			getLogger().info("GMail APIs - END getThreadList");
	
		} catch(GoogleJsonResponseException e) {
			getLogger().error("GMail APIs - Google service error in getThreadList.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GMail APIs - Critical error in getThreadList.", e);
			throw new RuntimeException(e);
		}
		
		return response;
	}
	@Override
	public Thread getThread(String executionGoogleUser, String threadId, String fields) {
		
		Thread response = null;
		
		try {
			
			getLogger().info("GMail APIs - START getThread");
			
			response = getThreadOperation(executionGoogleUser, threadId, fields).execute();
			
			getLogger().info("GMail APIs - END getThread");
	
		} catch(GoogleJsonResponseException e) {
			getLogger().error("GMail APIs - Google service error in getThread.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GMail APIs - Critical error in getThread.", e);
			throw new RuntimeException(e);
		}
		
		return response;
	}
	
	@Override
	public void deleteThread(String executionGoogleUser, String threadId) {
		
		try {
			
			getLogger().info("GMail APIs - START deleteThread | threadId:{}", threadId);
			
			deleteThreadOperation(executionGoogleUser, threadId).execute();
			
			getLogger().info("GMail APIs - END deleteThread");
	
		} catch(GoogleJsonResponseException e) {
			getLogger().error("GMail APIs - Google service error in deleteThread.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GMail APIs - Critical error in deleteThread.", e);
			throw new RuntimeException(e);
		}
		
	}
	
	@Override
	public Thread updateThreadLabels(String executionGoogleUser, String threadId, List<String> labelToAddIds, List<String> labelsToRemoveIds) {
		
		Thread response = null;
		
		try {
						
			getLogger().info("GMail APIs - START editThreadLabels | threadId:{}", threadId);
			
			response = updateThreadLabelsOperation(executionGoogleUser, threadId, labelToAddIds, labelsToRemoveIds).execute();
			
			getLogger().info("GMail APIs - END editThreadLabels");
	
		} catch(GoogleJsonResponseException e) {
			getLogger().error("GMail APIs - Google service error in editThreadLabels.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GMail APIs - Critical error in editThreadLabels.", e);
			throw new RuntimeException(e);
		}
		
		return response;
	}
	

	
	@Override
	public ListLabelsResponse getLabelList(String executionGoogleUser) {
		
		ListLabelsResponse response = null;
		
		try {
			
			getLogger().info("GMail APIs - START getLabels");
			
			response = getLabelListOperation(executionGoogleUser).execute();
			
			getLogger().info("GMail APIs - END getLabels");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("GMail APIs - Google service error in getLabels.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GMail APIs - Critical error in getLabels.", e);
			throw new RuntimeException(e);
		}
				
		return response;
	}
	
	@Override 
	public Label getLabel(String executionGoogleUser, String labelId, String fields) {
		
		Label response = null;
		
		try {
			
			getLogger().info("GMail APIs - getLabel");

			response = getLabelOperation(executionGoogleUser, labelId, fields).execute();
			
			getLogger().info("GMail APIs - END getLabel");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("GMail APIs - Google service error in getLabel.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GMail APIs - Critical error in getLabel.", e);
			throw new RuntimeException(e);
		}
		
		return response;
	}
	
	@Override
	public Label createLabel(String executionGoogleUser, String labelName, MailLabelListVisibilityEnum labelListVisibility, MailMessageListVisibilityEnum messageListVisibility) {

		Label response = null;
		
		try {
			
			getLogger().info("GMail APIs - createLabel | labelName:{}", labelName);
			
			response = createLabelOperation(executionGoogleUser, labelName, labelListVisibility, messageListVisibility).execute();
			
			getLogger().info("GMail APIs - END createLabel");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("GMail APIs - Google service error in createLabel.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GMail APIs - Critical error in createLabel.", e);
			throw new RuntimeException(e);
		}

		return response;
	}
	
	@Override
	public Label updateLabel(String executionGoogleUser, String labelId, String newLabelName, MailLabelListVisibilityEnum labelListVisibility, MailMessageListVisibilityEnum messageListVisibility) {
				
		Label response = null;
		
		try {
			
			getLogger().info("GMail APIs - START editLabel | labelId:{} newLabelName:{}", labelId, newLabelName);

			response = updateLabelOperation(executionGoogleUser, labelId, newLabelName, labelListVisibility, messageListVisibility).execute();
			
			getLogger().info("GMail APIs - END editLabel");

		} catch(GoogleJsonResponseException e) {
			getLogger().error("GMail APIs - Google service error in editLabel.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GMail - Critical error in editLabel.", e);
			throw new RuntimeException(e);
		}	
		
		return response;
	}
	
	@Override
	public void deleteLabel(String executionGoogleUser, String labelId) {
		
		try {
			
			getLogger().info("GMail APIs - START deleteLabel | labelId:{}", labelId);

			deleteLabelOperation(executionGoogleUser, labelId).execute();
			
			getLogger().info("GMail APIs - END deleteLabel");

		} catch(GoogleJsonResponseException e) {
			getLogger().error("GMail APIs - Google service error in deleteLabel.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GMail - Critical error in deleteLabel.", e);
			throw new RuntimeException(e);
		}	
	}
		
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Operation
	
	private Gmail.Users.Messages.List getMessageListOperation(String executionGoogleUser, String query, List<String> labelIds, String nextPageToken) {
		
		try {
			
			return getGmailGoogleService(executionGoogleUser)
					.users()
					.messages()
					.list("me")
					.setQ(query.toString())
					.setLabelIds(labelIds)
					.setPageToken(nextPageToken);
			
		} catch (Exception e) {
			getLogger().error("Gmail APIs - Critical error in getMessageListOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	private Get getMessageOperation(String executionGoogleUser, String messageId, String fields) {
		
		try { 
			return getGmailGoogleService(executionGoogleUser).users().messages().get(executionGoogleUser, messageId);
		} catch (Exception e) {
			getLogger().error("Gmail APIs - Critical error in getMessageOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	private Send sendMessaggeOperation(String executionGoogleUser, MimeMessage message) {
		
		try { 
			Message messageToSend = createMessageWithEmail(message);
			return getGmailGoogleService(executionGoogleUser).users().messages().send(executionGoogleUser, messageToSend);
		} catch (Exception e) {
			getLogger().error("Gmail APIs - Critical error in sendMessaggeOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	private Modify updateMessageLabelsOperation(String executionGoogleUser, String messageId, List<String> labelToAddIds, List<String> labelsToRemoveIds) {
		
		try { 
			ModifyMessageRequest request = new ModifyMessageRequest();
			
			if(labelToAddIds != null) {
				request.setAddLabelIds(labelToAddIds);

			}
			
			if(labelsToRemoveIds != null) {
				request.setRemoveLabelIds(labelsToRemoveIds);
			}
			
			return getGmailGoogleService(executionGoogleUser).users().messages().modify(executionGoogleUser, messageId, request);
		} catch (Exception e) {
			getLogger().error("Gmail APIs - Critical error in updateMessageLabelsOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	private Trash trashMessageOperation(String executionGoogleUser, String messageId) {
		
		try {
			return getGmailGoogleService(executionGoogleUser).users().messages().trash(executionGoogleUser, messageId);
		} catch (Exception e) {
			getLogger().error("Gmail APIs - Critical error in trashMessageOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	private Gmail.Users.Drafts.Get getDraftOperation(String executionGoogleUser, String draftId, String fields) {
		try {
			 return getGmailGoogleService(executionGoogleUser)
					.users()
					.drafts()
					.get(executionGoogleUser, draftId)
					.setFields(fields);
					
		} catch (Exception e) {
			getLogger().error("Gmail APIs - Critical error in getDraftOperation.", e);
			throw new RuntimeException(e);
		}
		
	}
	
	private Gmail.Users.Drafts.Create createDraftOperation(String executionGoogleUser, MimeMessage message) {
		
		try { 
			Draft draft = new Draft();
			draft.setMessage(createMessageWithEmail(message));
			
			return getGmailGoogleService(executionGoogleUser)
						.users()
						.drafts()
						.create(executionGoogleUser, draft);
		} catch (Exception e) {
			getLogger().error("Gmail APIs - Critical error in createDraftOperation.", e);
			throw new RuntimeException(e);
		}	
	}
	
	private Gmail.Users.Drafts.Update updateDraftOperation(String executionGoogleUser, String draftId, MimeMessage message) {
		
		try {
			
			Draft draft = new Draft();
			draft.setMessage(createMessageWithEmail(message));
			
			return getGmailGoogleService(executionGoogleUser)
						.users()
						.drafts()
						.update(executionGoogleUser, draftId, draft);
			
		} catch (Exception e) {
			getLogger().error("Gmail APIs - Critical error in createDraftOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	private Gmail.Users.Drafts.Send sendDraftOperation(String executionGoogleUser, String draftId) {
	
		try {
			
			Draft draft = new Draft();
			draft.setId(draftId);
			
			return getGmailGoogleService(executionGoogleUser)
					.users()
					.drafts()
					.send(executionGoogleUser, draft);
		} catch (Exception e) {
			getLogger().error("Gmail APIs - Critical error in sendDraftOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	private Gmail.Users.Drafts.Delete deleteDraftOperation(String executionGoogleUser, String draftId) {
		
		try {
			
			return getGmailGoogleService(executionGoogleUser)
				.users()
				.drafts()
				.delete(executionGoogleUser, draftId);
			
		} catch (Exception e) {
			getLogger().error("Gmail APIs - Critical error in deleteDraftOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	private Gmail.Users.Threads.List getThreadListOperation(String executionGoogleUser, String query, List<String> labelIds, String pageToken) {
		
		try {
			
			return getGmailGoogleService(executionGoogleUser)
				.users()
				.threads()
				.list(executionGoogleUser)
				.setQ(query)
				.setLabelIds(labelIds)
				.setPageToken(pageToken);
			
		} catch (Exception e) {
			getLogger().error("Gmail APIs - Critical error in getThreadListOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	private Gmail.Users.Threads.Get getThreadOperation(String executionGoogleUser, String threadId, String fields) {
		
		try {
			
			return getGmailGoogleService(executionGoogleUser)
					.users()
					.threads()
					.get(executionGoogleUser, threadId);
			
		} catch (Exception e) {
			getLogger().error("Gmail APIs - Critical error in getThreadOperation.", e);
			throw new RuntimeException(e);
		}
		
	}
	
	private Gmail.Users.Threads.Delete deleteThreadOperation(String executionGoogleUser, String threadId) {
		try {
			
			return getGmailGoogleService(executionGoogleUser)
					.users()
					.threads()
					.delete(executionGoogleUser, threadId);
			
		} catch (Exception e) {
			getLogger().error("Gmail APIs - Critical error in deleteThreadOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	private Gmail.Users.Threads.Modify updateThreadLabelsOperation(String executionGoogleUser, String threadId, List<String> labelToAddIds, List<String> labelsToRemoveIds) {
		try {
		
			ModifyThreadRequest threadRequest = new ModifyThreadRequest();
			threadRequest.setAddLabelIds(labelToAddIds);
			threadRequest.setRemoveLabelIds(labelsToRemoveIds);
			
			return getGmailGoogleService(executionGoogleUser)
				.users()
				.threads()
				.modify(executionGoogleUser, threadId, threadRequest);
			
		} catch (Exception e) {
			getLogger().error("Gmail APIs - Critical error in updateThreadLabelsOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	private Gmail.Users.Labels.List getLabelListOperation(String executionGoogleUser) {
		
		try {
			
			return getGmailGoogleService(executionGoogleUser)
					.users()
					.labels()
					.list("me");
			
		} catch (Exception e) {
			getLogger().error("Gmail APIs - Critical error in updateThreadLabelsOperation.", e);
			throw new RuntimeException(e);
		}
		
	}
	
	private Gmail.Users.Labels.Get getLabelOperation(String executionGoogleUser, String labelId, String fields) {
		
		try {
			
			return getGmailGoogleService(executionGoogleUser)
					.users()
					.labels()
					.get(executionGoogleUser, labelId)
					.setFields(fields);
			
		} catch (Exception e) {
			getLogger().error("Gmail APIs - Critical error in getLabelOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	private Gmail.Users.Labels.Create createLabelOperation(String executionGoogleUser, String labelName, MailLabelListVisibilityEnum labelListVisibility, MailMessageListVisibilityEnum messageListVisibility) {
		
		try {
			
			Label label = new Label();
			
			label.setName(labelName);
			label.setLabelListVisibility(labelListVisibility != null ? labelListVisibility.getValue() : null);
			label.setMessageListVisibility(messageListVisibility != null ? messageListVisibility.getValue() : null);
			
			return getGmailGoogleService(executionGoogleUser)
					.users()
					.labels()
					.create(executionGoogleUser, label);
			
		} catch (Exception e) {
			getLogger().error("Gmail APIs - Critical error in createLabelOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	private Gmail.Users.Labels.Update updateLabelOperation(String executionGoogleUser, String labelId, String newLabelName, MailLabelListVisibilityEnum labelListVisibility, MailMessageListVisibilityEnum messageListVisibility) {
		
		try {
			
			Label label = new Label();

			label.setId(labelId);
			label.setName(newLabelName);
			label.setLabelListVisibility(labelListVisibility != null ? labelListVisibility.getValue() : null);
			label.setMessageListVisibility(messageListVisibility != null ? messageListVisibility.getValue() : null);
			
			return getGmailGoogleService(executionGoogleUser)
					.users()
					.labels()
					.update(executionGoogleUser, labelId, label);
			
		} catch (Exception e) {
			getLogger().error("Gmail APIs - Critical error in updateLabelOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	private Gmail.Users.Labels.Delete deleteLabelOperation(String executionGoogleUser, String labelId) {
		
		try {
			
			return getGmailGoogleService(executionGoogleUser)
				.users()
				.labels()
				.delete(executionGoogleUser, labelId);
			
		} catch (Exception e) {
			getLogger().error("Gmail APIs - Critical error in deleteLabelOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Batch builder
	
	public class GmailBatchBuilder extends AbstractGoogleServiceBatchRequest<Gmail> {
		
		public GmailBatchBuilder(String executionGoogleUser, Gmail service) {
			super(executionGoogleUser, service);
		}
	}
	
	//TODO!!
	
	//TODO!!
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Callback
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//PRIVATE
		
	private String encodeEmail(MimeMessage email) throws MessagingException, IOException {
	    
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    email.writeTo(baos);
	    return Base64.encodeBase64URLSafeString(baos.toString().getBytes());
	}
	
	private Message createMessageWithEmail(MimeMessage emailContent) throws MessagingException, IOException {
        
        String encodedEmail = encodeEmail(emailContent);
        
        Message message = new Message();
        message.setRaw(encodedEmail);
        
        return message;
	}	
		
}
