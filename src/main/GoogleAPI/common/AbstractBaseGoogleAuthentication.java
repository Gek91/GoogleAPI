package main.GoogleAPI.common;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

public abstract class AbstractBaseGoogleAuthentication {

	private transient Logger logger = LoggerFactory.getLogger(this.getClass());

	protected Logger getLogger() {
		return logger;
	}
	
	//AbstractMethods
	protected abstract Credential authorize(String executionGoogleUser, HttpTransport httpTransport, JsonFactory jsonFactory, Collection<String> scopes) throws Exception;

}
