package GoogleAPI.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.ListUtils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.groupssettings.Groupssettings;
import com.google.api.services.groupssettings.GroupssettingsScopes;
import com.google.api.services.groupssettings.model.Groups;

import GoogleAPI.GroupsSettingsGoogleApi;
import GoogleAPI.util.AbstractBaseGoogleApi;
import GoogleAPI.util.AbstractBaseGoogleAuthentication;

public class GroupsSettingsGoogleApiImp extends AbstractBaseGoogleApi implements GroupsSettingsGoogleApi {

	
	private static final List<String> SCOPES = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add(GroupssettingsScopes.APPS_GROUPS_SETTINGS);
		}
	};
	
	@Override
	protected Collection<String> getScopes() {
		return SCOPES;
	}

	@Override
	protected AbstractGoogleJsonClient buildGoogleService(HttpTransport httpTransport, JacksonFactory jacksonFactory,
			Credential credential) {
		return new Groupssettings(httpTransport, jacksonFactory, credential);
	}
	
	private Groupssettings getGroupsSettingsGoogleService(String executionGoogleUser) {
		return (Groupssettings) getGoogleService(executionGoogleUser);
	}
	
	//constructor
	public GroupsSettingsGoogleApiImp(AbstractBaseGoogleAuthentication authenticationService) {
		super(authenticationService);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override

	public List<Groups> getGroupSettingsbyEmails(String executionGoogleUser, List<String> emails, String fields) {
		if(emails == null || emails.isEmpty()) {
			return null;
		}
		
		List<BatchGroupsGoogleCallback> callbacks = new ArrayList<BatchGroupsGoogleCallback>();
		
		try {
			
			getLogger().info("GSuite GroupsSettings APIs - retrieve groups detail batch...");
			
			List<List<String>> listOfList = ListUtils.partition(emails, 50);
			
			int i = 0;
			for(List<String> list : listOfList) {
				
				BatchRequest batchRequest = getGroupsSettingsGoogleService(executionGoogleUser).batch();

				for(String email : list) {
					
					BatchGroupsGoogleCallback callback = new BatchGroupsGoogleCallback();
					
					getGroupsSettingsGoogleService(executionGoogleUser).groups()
					.get(email).setFields(fields).queue(batchRequest, callback);
					
					callbacks.add(callback);
				}
				
				getLogger().info("Executing {}° batch request", (i+1)+"");
				batchRequest.execute();
				i++;
			}
			
			getLogger().info("GSuite GroupsSettings APIs - end retrieving groups detail batch.");

		
		} catch(Exception e) {
			getLogger().error("Error in retrieving groups settings", e);
			throw new RuntimeException(e);
		}
		
		List<Groups> result = new ArrayList<Groups>();
		for(BatchGroupsGoogleCallback callback : callbacks) {
			
			Groups value = callback.getResult();
			if(value != null) {
				result.add(value);
			}
		}
		
		return result;
	}
	

	class BatchGroupsGoogleCallback extends JsonBatchCallback<Groups> {
		
		private Groups result;
		
		public Groups getResult() {
			return result;
		}
		
		@Override
		  public void onSuccess(Groups obj, HttpHeaders responseHeaders) { 
			
			this.result = obj;
		}
		
		@Override
	    public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
			getLogger().error("GDrive APIs - Error Message in Batch operation : " + e.getMessage());
	    }
	}

}
