import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.api.services.gmail.model.ModifyMessageRequest;


public class GMailTest {
	
	private static Logger logger = LoggerFactory.getLogger(GMailTest.class);

	// Parametri autorizzazione
//	private static final String SERVICE_ACCOUNT_ID = "default-portal-test@menarini-farmacovigilanza-dev.iam.gserviceaccount.com";
	//private static final String SERVICE_ACCOUNT_EMAIL = "uniupo-prod-indaco-project@indaco-project.iam.gserviceaccount.com";
	private static final String SERVICE_ACCOUNT_ID = "default-portal-prod@menarini-farmacovigilanza-prod.iam.gserviceaccount.com";	
//	private static final String P12_FILENAME = "menarini-farmacovigilanza-dev-test-service-account.p12";
	private static final String P12_FILENAME = "prod-default-service-account.p12";

	//private static final String P12_FILENAME = "uniupo-prod-indaco-project.p12";
	//private static final String SERVICE_ACCOUNT_USER = "elena@exbag.info";
	//private static final String SERVICE_ACCOUNT_USER = "admin@injdev.com";
//	private static final String SERVICE_ACCOUNT_USER = "test2@injdev.com";
	private static final String SERVICE_ACCOUNT_USER = "phv_audit_report@menarini.com";

	//private static final String SERVICE_ACCOUNT_USER = "demo@exbag.it";
	//private static final String SERVICE_ACCOUNT_USER = "demo.crm@exbag.it";
	private Map<String, Gmail> gmailServicesMap;
	
	private static final List<String> SCOPES = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add(GmailScopes.GMAIL_MODIFY);
		}
	};

	private static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static JacksonFactory JSON_FACTORY = new JacksonFactory();

	
	public static void main(String[] args) throws Exception {
		GMailTest test = new GMailTest();
		
		Date now = new Date();
		
		/*
		 * Recupero labels per poter visualizzare l'id (necessario per utilizzarle nella list emails e nella modify)
		 */
//		logger.info("Labels: {}", test.listLabels("test2@injdev.com"));
		/*
		 * Recupero lista id mail in intervallo temporale
		 */
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		Date d = sdf.parse("16/05/2018 23:59:00");
		
		List<String> emailIds = test.listEmails("central.drug.safety.unit@menarini.it", DateUtils.addDays(d, -1), d, null);
		
		logger.info("Retrieved emails: {}", emailIds);
		
		/*
		 * Recupero dettaglio messaggi contenente messageUID (univoco tra tutte le caselle mail)
		 */
		Map<String, Message> messages = test.retrieveEmailDetails("central.drug.safety.unit@menarini.it", emailIds, "id, payload(headers)");
		
		/*
		 * Costruisco mappa <id, messageUID>
		 */
		Map<String, String> messageUids = new HashMap<String, String>();
		for(Message message : messages.values()) {
			if(message.getPayload() != null) {
				
				for(MessagePartHeader header : message.getPayload().getHeaders()) {
					
					if(header.getName().toUpperCase().equals("Subject".toUpperCase())) {
						System.out.print(header.getValue() + " - ");
					}		
				}		
				for(MessagePartHeader header : message.getPayload().getHeaders()) {
					
					if(header.getName().toUpperCase().equals("Date".toUpperCase())) {
						System.out.println(header.getValue());
					}		
				}
			}
//			messageUids.put(message.getId(), findMessageIdHeader(message.getPayload().getHeaders()).getValue());
		}
		
//		logger.info("MessageIds: {}", messageUids);
		
		/*
		 * Cerco i messageUIDs in una seconda mailbox
		 */
//		Map<String, Message> searchResult = test.searchEmailsByMessageId("test3@injdev.com", new ArrayList<String>(messageUids.values()));
//		
//		logger.info("SearchResult: {}", searchResult);
		
		/*
		 * Marco messaggi mailbox1 come elaborati (NB: necessario usare id label)
		 */
//		test.modifyMessages("test2@injdev.com", (emailIds), Arrays.asList("Label_1"), null);
		
	}
	
	private List<Label> listLabels(String executionGoogleUser) throws IOException, Exception {
		ListLabelsResponse response = getGmailService(executionGoogleUser).users().labels().list("me").execute();
		
		return response.getLabels();
	}

	private void modifyMessages(String executionGoogleUser, List<String> emailIds, List<String> addLabelIds, List<String> removeLabelIds) throws Exception, IOException {
		
		BatchRequest batchRequest = getGmailService(executionGoogleUser).batch();
		
		ModifyMessageRequest modifyRequest = new ModifyMessageRequest();
		modifyRequest.setAddLabelIds(addLabelIds);
		modifyRequest.setRemoveLabelIds(removeLabelIds);
		
		int batchCounter = 0;
		for(final String emailId : emailIds) {
			getGmailService(executionGoogleUser).users().messages().modify("me", emailId, modifyRequest)
				.queue(batchRequest, new JsonBatchCallback<Message>() {
					@Override
					public void onSuccess(Message message, HttpHeaders headers) throws IOException {
						logger.info("Message {} modified successfully", message.getId());
					}
					@Override
					public void onFailure(GoogleJsonError error, HttpHeaders headers) throws IOException {
						logger.error("Error modifying file {}: {}", emailId, error.getMessage());
					}});
			
			batchCounter++;
			
			//Limite 100 chiamate in una singola richiesta
			if(batchCounter == 99) {
				//Esecuzione chiamata e reset batch request
				batchRequest.execute();
				batchRequest = getGmailService(executionGoogleUser).batch();
				batchCounter = 0;
			}
		}
		batchRequest.execute();
		
	}
	
	private Map<String, Message> searchEmailsByMessageId(String executionGoogleUser, List<String> messageIds) throws IOException, Exception {
		final Map<String, Message> result = new HashMap<String, Message>();
		
		BatchRequest batchRequest = getGmailService(executionGoogleUser).batch();
		
		logger.info("Searching messages for messageIds {}", messageIds);
		
		int batchCounter = 0;
		
		for(final String messageId : messageIds) {
			
			String query = String.format("rfc822msgid:%s", messageId);
			
			getGmailService(executionGoogleUser).users().messages()
				.list("me")
				.setQ(query)
				.queue(batchRequest, new JsonBatchCallback<ListMessagesResponse>() {
					@Override
					public void onSuccess(ListMessagesResponse response, HttpHeaders headers) throws IOException {
						if(response.getMessages()!=null && !response.getMessages().isEmpty()) {
							result.put(messageId, response.getMessages().iterator().next());
						} else {
							logger.info("Message {} not found", messageId);
						}
					}

					@Override
					public void onFailure(GoogleJsonError error, HttpHeaders headers) throws IOException {
						logger.error("Error retrieving file list: {}", error.getMessage());
					}
				});
		
			batchCounter++;
			
			//Limite 100 chiamate in una singola richiesta
			if(batchCounter == 99) {
				//Esecuzione chiamata e reset batch request
				batchRequest.execute();
				batchRequest = getGmailService(executionGoogleUser).batch();
				batchCounter = 0;
			}
			
		}
		batchRequest.execute();
		
		return result;
	}

	private Map<String, Message> retrieveEmailDetails(String executionGoogleUser, List<String> emailIds, String fields) throws IOException, Exception {
		final Map<String, Message> messages = new HashMap<String, Message>();
		
		int batchCounter = 0;
		
		BatchRequest batchRequest = getGmailService(executionGoogleUser).batch();
		for(final String emailId : emailIds) {
			getGmailService(executionGoogleUser).users().messages().get("me", emailId)
			.setFields(fields)
			.queue(batchRequest, new JsonBatchCallback<Message>() {
				@Override
				public void onSuccess(Message message, HttpHeaders headers) throws IOException {
					messages.put(emailId, message);
				}
				
				@Override
				public void onFailure(GoogleJsonError error, HttpHeaders headers) throws IOException {
					logger.error("Error retrieving file details: {}", error.getMessage());
				}
			});
			
			batchCounter++;
			
			//Limite 100 chiamate in una singola richiesta
			if(batchCounter == 99) {
				//Esecuzione chiamata e reset batch request
				batchRequest.execute();
				batchRequest = getGmailService(executionGoogleUser).batch();
				batchCounter = 0;
			}
			
		}
		batchRequest.execute();
		return messages;
	}

	private static MessagePartHeader findMessageIdHeader(List<MessagePartHeader> headers) {
		for(MessagePartHeader header : headers) {
			if(header.getName().equals("Message-ID")) {
				return header;
			}
		}
		return null;
	}

	private List<String> listEmails(String executionGoogleUser, Date after, Date before, List<String> labelIds) throws IOException, Exception {
		List<String> emailIds = new ArrayList<String>();
		logger.info("Retrieving email for user {} for time interval [{}, {}] and labelIds {}", executionGoogleUser, after, before, labelIds);
		String query = String.format("after:%d before:%d", millisToSeconds(after.getTime()), millisToSeconds(before.getTime()));
		logger.info("q={}", query);
		String nextPageToken = null;
		do {
			ListMessagesResponse response = getGmailService(executionGoogleUser).users().messages()
					.list("me")
					.setQ(query)
					.setLabelIds(labelIds)
					.execute();
			
			if(response.getMessages()!=null) {
				for(Message message : response.getMessages()) {
					emailIds.add(message.getId());
				}
			}
			
			nextPageToken = response.getNextPageToken();
		} while (nextPageToken!=null);
		
		return emailIds;
	}
	
	private long millisToSeconds(long millis) {
		return millis/1000;
	}

//	private void doExecuteEMailDispatch(Gmail gmailService, String sender, List<String> to, List<String> cc, List<String> bcc, String subject, String body, boolean isHtml) throws IOException, MessagingException {
//		
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		buildMimeMessage(sender, to, cc, bcc, subject, body, isHtml).writeTo(baos);
//		Message message = new Message();
//		message.setRaw(Base64.encodeBase64URLSafeString(baos.toByteArray()));
//		
//		gmailService.users().messages().send(sender, message).execute();
//		
//	}

//	public MimeMessage buildMimeMessage(String sender, List<String> to, List<String> cc, List<String> bcc, String subject, String body, boolean isHtml) throws MessagingException {
//		
//		Address[] toAddresses = buildAddresses(to);
//		Address[] ccAddresses = buildAddresses(cc);
//		Address[] bccAddresses = buildAddresses(bcc);
//		
//		Properties props = new Properties();
//		Session session = Session.getDefaultInstance(props, null);
//
//		MimeMessage mimeMessage = new MimeMessage(session);
//
//		mimeMessage.setFrom(new InternetAddress(sender));
//		
//		if (toAddresses != null) {
//			mimeMessage.addRecipients(javax.mail.Message.RecipientType.TO, toAddresses);
//		}
//		
//		if (ccAddresses != null) {
//			mimeMessage.addRecipients(javax.mail.Message.RecipientType.CC, ccAddresses);
//		}
//		
//		if (bccAddresses != null) {
//			mimeMessage.addRecipients(javax.mail.Message.RecipientType.BCC, bccAddresses);
//		}
//		
//		mimeMessage.setSubject(subject, "UTF-8");
//		
//		if (isHtml) {
//			mimeMessage.setContent(body, "text/html; charset=utf-8");
//		} else {
//			mimeMessage.setText(body, "UTF-8");
//		}
//		
//		return mimeMessage;
//		
//	}
//	
//	public Address[] buildAddresses(List<String> textAddresses) throws AddressException {
//		
//		Address[] addresses = null;
//		
//		if (!textAddresses.isEmpty()) {
//			
//			addresses = new Address[textAddresses.size()];
//			
//			for (int i = 0; i < textAddresses.size(); i++) {
//				addresses[i] = new InternetAddress(textAddresses.get(i));
//			}
//			
//		}
//		
//		return addresses;
//	}
	
	public GMailTest() throws Exception {
		this.gmailServicesMap = new HashMap<String, Gmail>();
	}

	private Credential authorize(String executionGoogleUser) throws Exception {
		return new GoogleCredential.Builder()
				.setTransport(HTTP_TRANSPORT)
				.setJsonFactory(JSON_FACTORY)
				.setServiceAccountId(SERVICE_ACCOUNT_ID)
				.setServiceAccountUser(executionGoogleUser)
				.setServiceAccountScopes(SCOPES)
				.setServiceAccountPrivateKeyFromP12File(new java.io.File(P12_FILENAME))
				.build();
	}
	
	private Gmail buildGmailService(String executionGoogleUser) throws Exception {
		Credential credential = authorize(executionGoogleUser);
		return new Gmail(HTTP_TRANSPORT, JSON_FACTORY, credential);
	}
	
	private Gmail getGmailService(String executionGoogleUser) throws Exception {
		if(!gmailServicesMap.containsKey(executionGoogleUser)) {
			gmailServicesMap.put(executionGoogleUser, buildGmailService(executionGoogleUser));
		}
		return gmailServicesMap.get(executionGoogleUser);
	}
}
