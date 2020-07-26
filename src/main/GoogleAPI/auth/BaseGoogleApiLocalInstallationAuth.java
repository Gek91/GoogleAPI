package main.GoogleAPI.auth;
import java.io.FileReader;
import java.util.Collection;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import main.GoogleAPI.base.AbstractBaseGoogleAuthentication;
	
public class BaseGoogleApiLocalInstallationAuth extends AbstractBaseGoogleAuthentication {

	/*
	 * Local account variables
	 */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/drive-java-quickstart");
    private static FileDataStoreFactory DATA_STORE_FACTORY;
    	
    static {
        try {
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
    
	private String jsonFilePath; 
	
	//Constructor
	public BaseGoogleApiLocalInstallationAuth(String clientSecretJsonFilePath) {
		jsonFilePath = clientSecretJsonFilePath;
	}
    

	protected Credential authorize(String executionGoogleUser, HttpTransport httpTransport, JsonFactory jsonFactory, Collection<String> scopes) throws Exception {
		
		getLogger().info("Creating local credential for user {}", executionGoogleUser);
		
		java.io.File clientFile = new java.io.File(jsonFilePath);
	   	 
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new FileReader(clientFile));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = 
       		new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, clientSecrets, scopes)
   	                .setDataStoreFactory(DATA_STORE_FACTORY)
   	                .setAccessType("offline") //installed application. online for webapplication
   	                .build();
       
		Credential credential = new AuthorizationCodeInstalledApp ( flow, new LocalServerReceiver()).authorize(executionGoogleUser);
       
//		System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
		
		getLogger().info("Creating local credential for user {}", executionGoogleUser);
              
		return credential;
   }
		
}
