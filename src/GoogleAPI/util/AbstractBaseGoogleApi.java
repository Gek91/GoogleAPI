package GoogleAPI.util;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

public abstract class AbstractBaseGoogleApi {

	private transient Logger logger = LoggerFactory.getLogger(this.getClass());

	protected Logger getLogger() {
		return logger;
	}
	
	protected static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	protected static JacksonFactory JSON_FACTORY = new JacksonFactory();
	
	//User - service instance
	protected Map<String, AbstractGoogleJsonClient> userServiceMap = new ConcurrentHashMap<>();
	
	private AbstractBaseGoogleAuthentication authenticationService;
	
	//AbstractMethods
	protected abstract AbstractGoogleJsonClient buildGoogleService(HttpTransport httpTransport, JacksonFactory jacksonFactory, Credential credential);
	protected abstract Collection<String> getScopes();
	
	//Costructor
	public AbstractBaseGoogleApi(AbstractBaseGoogleAuthentication authenticationService) {
		this.authenticationService = authenticationService;
	}
	
	/*
	 * Google service
	 */

	//Call authentication service
	private Credential getCredential(String executionGoogleUser) throws Exception {
		
		return authenticationService.authorize(executionGoogleUser, HTTP_TRANSPORT, JSON_FACTORY, getScopes());			
	}
	
	protected AbstractGoogleJsonClient getGoogleService(String executionGoogleUser) {
		
		AbstractGoogleJsonClient service = this.userServiceMap.get(executionGoogleUser);
		
		//not in the user-service map
		if(service == null) {
			
			//build the service
			try {
				
				service = buildGoogleService(HTTP_TRANSPORT, JSON_FACTORY, getCredential(executionGoogleUser));
				this.userServiceMap.put(executionGoogleUser, service);
				
			} catch (Exception e) {
				getLogger().error("Authorization error", e);
				throw new RuntimeException(e);
			}
		}
		
		return service;
	}
}
