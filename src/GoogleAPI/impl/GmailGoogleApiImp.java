package GoogleAPI.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.ListUtils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.ModifyMessageRequest;

import GoogleAPI.GmailGoogleApi;
import GoogleAPI.util.AbstractBaseGoogleApi;
import GoogleAPI.util.AbstractBaseGoogleAuthentication;

public class GmailGoogleApiImp extends AbstractBaseGoogleApi implements GmailGoogleApi{

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
	protected AbstractGoogleJsonClient buildGoogleService(HttpTransport httpTransport, JacksonFactory jacksonFactory,
			Credential credential) {
		return new Gmail(httpTransport, jacksonFactory, credential);
	}
	
	private Gmail getGmailGoogleService(String executionGoogleUser) {
		return (Gmail) getGoogleService(executionGoogleUser);
	}
	
	//constructor
	public GmailGoogleApiImp(AbstractBaseGoogleAuthentication authenticationService) {
		super(authenticationService);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public ListMessagesResponse listMessages(String executionGoogleUser, Date after, Date before, List<String> labelIds, List<String> labelsToIgnore, List<String> senderToIgnore, String nextPageToken) {
		
		ListMessagesResponse response = null;
		
		getLogger().info("GMail APIs - Retrieving email for user {} for time interval [{}, {}], labelIds {}, labelsToIgnore {}, senderToIgnore {}", executionGoogleUser, after, before, labelIds, labelsToIgnore, senderToIgnore);
			
		StringBuffer query = new StringBuffer();
		
		if(after != null) {
			
			query.append(String.format(" after:%d ",  millisToSeconds(after.getTime())));
		}
		
		if(before != null) {
				
			query.append(String.format(" before:%d ",  millisToSeconds(before.getTime())));
		}
		
		if(labelsToIgnore != null) {
			
			for(String label : labelsToIgnore) {
				query.append(" -label:" + label);
			}
			
		}
		
		if(senderToIgnore != null) {
			
			for(String sender : senderToIgnore) {
				query.append(" -from:" + sender);
			}
		}
		
		getLogger().info("q={}", query);
		
		try {
			response = getGmailGoogleService(executionGoogleUser).users().messages()
					.list("me")
					.setQ(query.toString())
					.setLabelIds(labelIds)
					.setPageToken(nextPageToken)
					.execute();
			
			getLogger().info("GMail APIs - end retrieving emails");
			
		} catch(Exception e) {
			getLogger().error("Error retrieving messages", e);
			throw new RuntimeException(e);
		}
							
		return response;
	}
		
	@Override
	public Map<String, Message> retrieveMessagesDetails(String executionGoogleUser, List<String> emailIds, String fields) {
		
		getLogger().info("GMail APIs - retrieve messages detail");
		
		final Map<String, Message> messages = new HashMap<String, Message>();
		
		try {
			
			List<List<String>> listOfList =  ListUtils.partition(emailIds, 10);

			for(List<String> list : listOfList) {
				
				BatchRequest batchRequest = getGmailGoogleService(executionGoogleUser).batch();

				for(final String emailId : list) {
					
					getGmailGoogleService(executionGoogleUser).users().messages()
						.get("me", emailId)
						.setFields(fields)
						.queue(batchRequest, new JsonBatchCallback<Message>() {
							@Override
							public void onSuccess(Message message, HttpHeaders headers) throws IOException {
								messages.put(emailId, message);
							}
							
							@Override
							public void onFailure(GoogleJsonError error, HttpHeaders headers) throws IOException {
								getLogger().error("Error retrieving file details: {}", error.getMessage());
							}
						});
									
				}
				
				batchRequest.execute();
			}
			
			getLogger().info("GMail APIs - end retrieveing messages detail");
			
		} catch(Exception e) {
			getLogger().error("Error retrieving messages detail", e);
			throw new RuntimeException(e);
		}
		
		return messages;
	}

	@Override
	public Map<String, Message> searchMessagesByUid(String executionGoogleUser, List<String> messageUids) {
		
		final Map<String, Message> result = new HashMap<String, Message>();
		
		getLogger().info("GMail APIs - Searching messages for messageIds");
		
		try {
			
			List<List<String>> listOfList =  ListUtils.partition(messageUids, 10);
			
			for(List<String> list : listOfList) {
				
				BatchRequest batchRequest = getGmailGoogleService(executionGoogleUser).batch();

				for(final String messageId : list) {
					
					String query = String.format("rfc822msgid:%s", messageId);
					
					getGmailGoogleService(executionGoogleUser).users().messages()
						.list("me")
						.setQ(query)
						.queue(batchRequest, new JsonBatchCallback<ListMessagesResponse>() {
							@Override
							public void onSuccess(ListMessagesResponse response, HttpHeaders headers) throws IOException {
								if(response.getMessages()!=null && !response.getMessages().isEmpty()) {
									result.put(messageId, response.getMessages().iterator().next());
								} else {
									getLogger().info("Message {} not found", messageId);
								}
							}

							@Override
							public void onFailure(GoogleJsonError error, HttpHeaders headers) throws IOException {
								getLogger().error("Error retrieving file list: {}", error.getMessage());
							}
						});
							
				}
				
				batchRequest.execute();
			}
			
			getLogger().info("GMail APIs - End searching messages for messageIds");

		}  catch(Exception e) {
			getLogger().error("Error retrieving messages detail", e);
			throw new RuntimeException(e);
		}
			
		return result;
	}

	@Override
	public void modifyMessagesLabels(String executionGoogleUser, List<String> emailIds, List<String> addLabelIds, List<String> removeLabelIds) {
		
		getLogger().info("GMail APIs - Modify messages labels");
		
		try {
		
			ModifyMessageRequest modifyRequest = new ModifyMessageRequest();
			modifyRequest.setAddLabelIds(addLabelIds);
			modifyRequest.setRemoveLabelIds(removeLabelIds);
			
			List<List<String>> listOfList =  ListUtils.partition(emailIds, 10);
	
			for(List<String> list : listOfList) {
				
				BatchRequest batchRequest = getGmailGoogleService(executionGoogleUser).batch();
	
				for(final String messageId : list) {
					
					getGmailGoogleService(executionGoogleUser).users().messages().modify("me", messageId, modifyRequest)
					.queue(batchRequest, new JsonBatchCallback<Message>() {
						@Override
						public void onSuccess(Message message, HttpHeaders headers) throws IOException {
							getLogger().info("Message {} modified successfully", message.getId());
						}
						@Override
						public void onFailure(GoogleJsonError error, HttpHeaders headers) throws IOException {
							getLogger().error("Error modifying file {}: {}", messageId, error.getMessage());
						}});
				}
				
				batchRequest.execute();
			}

			getLogger().info("GMail APIs - end Modifying messages labels");
			
		} catch(Exception e) {
			getLogger().error("Error retrieving messages detail", e);
			throw new RuntimeException(e);
		}
		
	}
	
	@Override
	public List<Label> listLabels(String executionGoogleUser) {
		
		getLogger().info("GMail APIs - list user label");
		
		ListLabelsResponse response = null;
		
		try {
			
			response = getGmailGoogleService(executionGoogleUser).users().labels().list("me").execute();
			
		} catch(Exception e) {
			getLogger().error("Error retrieving messages detail", e);
			throw new RuntimeException(e);
		}
		
		getLogger().info("GMail APIs - end list user label");
		
		return response.getLabels();
	}
	
	private long millisToSeconds(long millis) {
		return millis/1000;
	}


	@Override
	public String createGmailLabel(String executionGoogleUser, String labelName) {

		getLogger().info("GMail APIs - create label");
		
		Label label = new Label();
		
		try {
			
			label.setName(labelName);
			label.setLabelListVisibility("labelShow");
			label.setMessageListVisibility("show");
			
			label = getGmailGoogleService(executionGoogleUser).users().labels().create(executionGoogleUser, label).execute();
			
			getLogger().info("GMail APIs - end creating label");
			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}

		return label.getId();
	}
	
	public String updateGmailLabel(String executionGoogleUser, String labelId, String newLabelName) {
		
		getLogger().info("GMail APIs - update label");
		
		Label label = new Label();

		try {
			
			label.setName(newLabelName);
			label.setLabelListVisibility("labelShow");
			label.setMessageListVisibility("show");
			label.setId(labelId);
			
			label = getGmailGoogleService(executionGoogleUser).users().labels().update(executionGoogleUser, labelId, label).execute();
			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}

		getLogger().info("GMail APIs - end updating label");
		
		return label.getId();
	}
}
