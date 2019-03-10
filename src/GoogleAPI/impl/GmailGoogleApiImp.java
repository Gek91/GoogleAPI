package GoogleAPI.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
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
import GoogleAPI.util.BasicBatchCallBack;

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
	protected AbstractGoogleJsonClient buildGoogleService(HttpTransport httpTransport, JsonFactory jacksonFactory, HttpRequestInitializer requestInitializer) {
		return new Gmail(httpTransport, jacksonFactory, requestInitializer);
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
	public ListMessagesResponse getMessages(String executionGoogleUser, Date after, Date before, List<String> labelIds, List<String> labelsToIgnore, List<String> senderToIgnore, String nextPageToken) {
		
		ListMessagesResponse response = null;
		
		try {
		
			getLogger().info("GMail APIs - START getMessages | after:{} before:{} nextPageToken:{}", after, before, nextPageToken );
				
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
		
		
			response = getGmailGoogleService(executionGoogleUser)
					.users()
					.messages()
					.list("me")
					.setQ(query.toString())
					.setLabelIds(labelIds)
					.setPageToken(nextPageToken)
					.execute();
			
			getLogger().info("GMail APIs - END getMessages");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("GMail APIs - Google service error in getMessages.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GMail - Critical error in getMessages.", e);
			throw new RuntimeException(e);
		}
							
		return response;
	}
		
	@Override
	public List<Message> getMessagesDetail(String executionGoogleUser, List<String> emailIds, String fields) {
		if(emailIds == null || emailIds.isEmpty()) {
			return null;
		}
		
		BasicBatchCallBack<Message> callback = new BasicBatchCallBack<>(new ArrayList<Message>());
		
		try {
			
			getLogger().info("GMail APIs - START BATCH getMessagesDetail");
			
			List<List<String>> listOfList =  ListUtils.partition(emailIds, 10);

			int i = 0;
			for(List<String> list : listOfList) {
				
				BatchRequest batchRequest = getGmailGoogleService(executionGoogleUser).batch();

				for(final String emailId : list) {
					
					getGmailGoogleService(executionGoogleUser)
						.users()
						.messages()
						.get("me", emailId)
						.setFields(fields) 
						.queue(batchRequest, callback);
									
				}
				
				getLogger().info("GMail APIs - Executing {}° batch request", (i+1)+".");
				batchRequest.execute();
				i++;
			}
			
			getLogger().info("GMail APIs - END BATCH getMessagesDetail");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("GMail APIs - Google service error in getMessagesDetail.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GMail - Critical error in getMessagesDetail.", e);
			throw new RuntimeException(e);
		}
		
		return (List<Message>) callback.getEntities();
	}

	@Override
	public List<Message> getMessagesByUid(String executionGoogleUser, List<String> messageUids, String fields) {
		if(messageUids == null || messageUids.isEmpty()) {
			return null;
		}
		
		BasicBatchCallBack<ListMessagesResponse> callback = new BasicBatchCallBack<>(new ArrayList<ListMessagesResponse>());

		try {
			
			getLogger().info("GMail APIs - START BATCH getMessagesByUid ");
			
			List<List<String>> listOfList =  ListUtils.partition(messageUids, 10);
			
			int i = 0;
			for(List<String> list : listOfList) {
				
				BatchRequest batchRequest = getGmailGoogleService(executionGoogleUser).batch();

				for(final String messageId : list) {
					
					String query = String.format("rfc822msgid:%s", messageId);
										
					getGmailGoogleService(executionGoogleUser)
						.users()
						.messages()
						.list("me")
						.setQ(query)
						.setFields(fields)
						.queue(batchRequest, callback);
				}
				
				getLogger().info("GMail APIs - Executing {}° batch request", (i+1)+".");
				batchRequest.execute();
				i++;
			}
			
			getLogger().info("GMail APIs - END BATCH getMessagesByUid");

		} catch(GoogleJsonResponseException e) {
			getLogger().error("GMail APIs - Google service error in getMessagesByUid.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GMail - Critical error in getMessagesByUid.", e);
			throw new RuntimeException(e);
		}
			
		return callback.getEntities().stream()
				.filter(elem -> elem.getMessages() != null && !elem.getMessages().isEmpty())
				.map(elem -> elem.getMessages().iterator().next())
				.collect(Collectors.toList());
	}

	@Override
	public void editMessagesLabels(String executionGoogleUser, List<String> emailIds, List<String> addLabelIds, List<String> removeLabelIds) {
		
		BasicBatchCallBack<Message> callback = new BasicBatchCallBack<>();
		
		try {
			
			getLogger().info("GMail APIs - START BATCH editMessagesLabels ");

			ModifyMessageRequest modifyRequest = new ModifyMessageRequest();
			modifyRequest.setAddLabelIds(addLabelIds);
			modifyRequest.setRemoveLabelIds(removeLabelIds);
			
			List<List<String>> listOfList =  ListUtils.partition(emailIds, 10);
	
			for(List<String> list : listOfList) {
				
				BatchRequest batchRequest = getGmailGoogleService(executionGoogleUser).batch();
	
				for(final String messageId : list) {
					
					getGmailGoogleService(executionGoogleUser)
						.users()
						.messages()
						.modify("me", messageId, modifyRequest)
						.queue(batchRequest, callback);
				}
				
				batchRequest.execute();
			}

			getLogger().info("GMail APIs - END BATCH editMessagesLabels");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("GMail APIs - Google service error in editMessagesLabels.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GMail - Critical error in editMessagesLabels.", e);
			throw new RuntimeException(e);
		}
		
	}
	
	@Override
	public ListLabelsResponse getLabels(String executionGoogleUser) {
		
		ListLabelsResponse response = null;
		
		try {
			
			getLogger().info("GMail APIs - START getLabels");
			
			response = getGmailGoogleService(executionGoogleUser)
					.users()
					.labels()
					.list("me")
					.execute();
			
			getLogger().info("GMail APIs - END getLabels");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("GMail APIs - Google service error in getLabels.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GMail - Critical error in getLabels.", e);
			throw new RuntimeException(e);
		}
				
		return response;
	}
	
	@Override
	public Label createLabel(String executionGoogleUser, String labelName) {

		Label label = null;
		
		try {
			
			getLogger().info("GMail APIs - createLabel | labelName:{}", labelName);
			
			label = new Label();
			
			label.setName(labelName);
			label.setLabelListVisibility("labelShow");
			label.setMessageListVisibility("show");
			
			label = getGmailGoogleService(executionGoogleUser)
					.users()
					.labels()
					.create(executionGoogleUser, label)
					.execute();
			
			getLogger().info("GMail APIs - END createLabel");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("GMail APIs - Google service error in createLabel.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GMail - Critical error in createLabel.", e);
			throw new RuntimeException(e);
		}

		return label;
	}
	
	public void editLabel(String executionGoogleUser, String labelId, String newLabelName) {
				
		try {
			
			getLogger().info("GMail APIs - START editLabel | labelId:{} newLabelName:{}", labelId, newLabelName);

			Label label = new Label();

			
			label.setName(newLabelName);
			label.setLabelListVisibility("labelShow");
			label.setMessageListVisibility("show");
			label.setId(labelId);
			
			getGmailGoogleService(executionGoogleUser)
					.users()
					.labels()
					.update(executionGoogleUser, labelId, label)
					.execute();
			
			getLogger().info("GMail APIs - END editLabel");

		} catch(GoogleJsonResponseException e) {
			getLogger().error("GMail APIs - Google service error in editLabel.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GMail - Critical error in editLabel.", e);
			throw new RuntimeException(e);
		}	
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//PRIVATE
	
	private long millisToSeconds(long millis) {
		return millis/1000;
	}
}
