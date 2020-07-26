package main.GoogleAPI.base;

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

public abstract class AbstractBaseGoogleApi<T extends AbstractGoogleJsonClient> {

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
	protected HttpTransport httpTransport;
	protected JsonFactory jsonFactory;
	//User - service instance map
	protected Map<String, T> userServiceMap = new ConcurrentHashMap<>();
	//Authentication service
	private AbstractBaseGoogleAuthentication authenticationService;
	
	/*
	 * Constructor
	 */
	public AbstractBaseGoogleApi(AbstractBaseGoogleAuthentication authenticationService) {
		this(authenticationService, new NetHttpTransport(), new JacksonFactory());
	}
	
	public AbstractBaseGoogleApi(AbstractBaseGoogleAuthentication authenticationService, HttpTransport httpTransport, JsonFactory jsonFactory) {
		this.authenticationService = authenticationService;
		this.httpTransport = httpTransport;
		this.jsonFactory = jsonFactory;	
	}
	
	/*
	 * Abstract Methods
	 */
	protected abstract T buildGoogleService(HttpTransport httpTransport, JsonFactory jacksonFactory, HttpRequestInitializer requestInitializer);
	protected abstract Collection<String> getScopes();
	protected abstract AbstractGoogleServiceBatch<T> getBatchBuilder(String executionGoogleUser);
	protected abstract AbstractGoogleServiceBatch<T> getBatchBuilder(String executionGoogleUser, int operationsInBatch);
	
	/*
	 * Implemented Methods
	 */
	//Call authentication service creating credential
	private Credential getCredential(String executionGoogleUser) throws Exception {
		return authenticationService.authorize(executionGoogleUser, this.httpTransport, this.jsonFactory, getScopes());			
	}
	
	//Retrieve o create specific google service
	protected T getGoogleService(String executionGoogleUser) {
		
		T service = this.userServiceMap.get(executionGoogleUser);
		
		//not in the user-service map
		if(service == null) {
			
			//build the service
			try {
				
				//Exponential backoff
				HttpRequestInitializer requestInitializer = new RetryHttpInitializer(getCredential(executionGoogleUser));
				
				service = buildGoogleService(this.httpTransport, this.jsonFactory, requestInitializer);
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
