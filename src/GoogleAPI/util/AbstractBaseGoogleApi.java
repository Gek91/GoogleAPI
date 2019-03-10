package GoogleAPI.util;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

public abstract class AbstractBaseGoogleApi {

	/*
	 * Logger
	 */
	private transient Logger logger = LoggerFactory.getLogger(this.getClass());

	protected Logger getLogger() {
		return logger;
	}
	
	/*
	 * Fields
	 */
	protected static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	protected static JsonFactory JSON_FACTORY = new JacksonFactory();
	//User - service instance map
	protected Map<String, AbstractGoogleJsonClient> userServiceMap = new ConcurrentHashMap<>();
	//Authentication service
	private AbstractBaseGoogleAuthentication authenticationService;
	
	/*
	 * Constructor
	 */
	public AbstractBaseGoogleApi(AbstractBaseGoogleAuthentication authenticationService) {
		this.authenticationService = authenticationService;
	}
	
	/*
	 * Abstract Methods
	 */
	protected abstract AbstractGoogleJsonClient buildGoogleService(HttpTransport httpTransport, JsonFactory jacksonFactory, HttpRequestInitializer requestInitializer);
	protected abstract Collection<String> getScopes();
		
	/*
	 * Implemented Methods
	 */
	//Call authentication service creating credential
	private Credential getCredential(String executionGoogleUser) throws Exception {
		return authenticationService.authorize(executionGoogleUser, HTTP_TRANSPORT, JSON_FACTORY, getScopes());			
	}
	
	//Retrieve o create specific google service
	protected AbstractGoogleJsonClient getGoogleService(String executionGoogleUser) {
		
		AbstractGoogleJsonClient service = this.userServiceMap.get(executionGoogleUser);
		
		//not in the user-service map
		if(service == null) {
			
			//build the service
			try {
				
				//Exponential backoff
				HttpRequestInitializer requestInitializer = new RetryHttpInitializer(getCredential(executionGoogleUser));
				
				service = buildGoogleService(HTTP_TRANSPORT, JSON_FACTORY, requestInitializer);
				this.userServiceMap.put(executionGoogleUser, service);
				
			} catch (Exception e) {
				getLogger().error("Authorization error", e);
				throw new RuntimeException(e);
			}
		}
		
		return service;
	}
	
	//Errors handler
	protected void handleServiceException(GoogleJsonResponseException e) {
		
		switch (e.getStatusCode()) {
		
		case 400: //Bad request
			logger.error("400 - Bad Request:" + e.getMessage());
			break;

		case 401: //Invalid credential
			logger.error("401 - Invalid Credential:" + e.getMessage()); 
			break;
			
		case 403: //Daily limit/user rate exceed/insufficient permission
			logger.error("403 - Insufficient permission:" + e.getMessage()); 
			break;
			
		case 404: //Not found
			logger.error("404 - Not found:" + e.getMessage()); 
			break;
	
		case 409 : //requested identifier alreay exists
			logger.error("409 - Already Exists:" + e.getMessage()); 
			break;
			
		case 410: //gone: synchronization data no longer valid/entity already deleted
			logger.error("410 - Gone:" + e.getMessage());
			break;
			
		case 412: //precondition failed
			logger.error("412 - Preconditions failed:" + e.getMessage());
			break;
			
		case 429: //too many request
			logger.error("412 - Too many requests:" + e.getMessage());
			break;
		
		case 500: //backend error
			logger.error("500 - Backend error:" + e.getMessage());
			break;
			
		default:
			break;
		}

		
	}
}
