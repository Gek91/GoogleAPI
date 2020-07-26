package main.GoogleAPI.auth;

import java.io.FileInputStream;
import java.util.Collection;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

import main.GoogleAPI.base.AbstractBaseGoogleAuthentication;

public class BaseGoogleApiServiceAccountAuth extends AbstractBaseGoogleAuthentication {
	
	private String jsonFilePath; 

	//Constructor
	public BaseGoogleApiServiceAccountAuth(String serviceAccountJsonFilePath) {
		jsonFilePath = serviceAccountJsonFilePath;
	}
	
	@Override
	protected Credential authorize(String executionGoogleUser, HttpTransport httpTransport, JsonFactory jsonFactory, Collection<String> scopes) throws Exception {
		
		getLogger().info("Creating credential for user {}", executionGoogleUser);
		
		GoogleCredential jsonCredential = GoogleCredential.fromStream(new FileInputStream(new java.io.File(jsonFilePath)), httpTransport, jsonFactory).createScoped(scopes);
		
		GoogleCredential credential = new GoogleCredential.Builder()
			.setTransport(jsonCredential.getTransport())
			.setJsonFactory(jsonCredential.getJsonFactory())
			.setServiceAccountId(jsonCredential.getServiceAccountId())
			.setServiceAccountUser(executionGoogleUser)
			.setServiceAccountScopes(jsonCredential.getServiceAccountScopes())
			.setServiceAccountPrivateKey(jsonCredential.getServiceAccountPrivateKey())
			.build();
		
		getLogger().info("Credential created for user {}", executionGoogleUser);
		
		return credential;
	}
}
