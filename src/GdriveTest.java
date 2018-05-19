import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.gmail.Gmail;

public class GdriveTest {

	// Parametri autorizzazione
	private static final String SERVICE_ACCOUNT_ID = "default-portal-test@menarini-farmacovigilanza-dev.iam.gserviceaccount.com";
	private static final String P12_FILENAME = "menarini-farmacovigilanza-dev-test-service-account.p12";
	//private static final String P12_FILENAME = "uniupo-prod-indaco-project.p12";
	//private static final String SERVICE_ACCOUNT_USER = "elena@exbag.info";
	//private static final String SERVICE_ACCOUNT_USER = "admin@injdev.com";
	private static final String SERVICE_ACCOUNT_USER = "test2@injdev.com";
	//private static final String SERVICE_ACCOUNT_USER = "demo@exbag.it";
	//private static final String SERVICE_ACCOUNT_USER = "demo.crm@exbag.it";
	private Map<String, Drive> gDriveServicesMap;
	
	private static final List<String> SCOPES = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add(DriveScopes.DRIVE);
			add(DriveScopes.DRIVE_FILE);
			add(DriveScopes.DRIVE_METADATA);
		}
	};

	private static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static JacksonFactory JSON_FACTORY = new JacksonFactory();
	
	public GdriveTest() throws Exception {
		this.gDriveServicesMap = new HashMap<String, Drive>();
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
	
	private Drive buildDriveService(String executionGoogleUser) throws Exception {
		
		Credential credential = authorize(executionGoogleUser);
		
		return new Drive(HTTP_TRANSPORT, JSON_FACTORY, credential);
	}
	
	private Drive getDriveService(String executionGoogleUser) throws Exception {
		
		if(!gDriveServicesMap.containsKey(executionGoogleUser)) {
			gDriveServicesMap.put(executionGoogleUser, buildDriveService(executionGoogleUser));
		}
		return gDriveServicesMap.get(executionGoogleUser);
	}
}
