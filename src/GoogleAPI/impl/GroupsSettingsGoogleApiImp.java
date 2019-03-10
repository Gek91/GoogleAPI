package GoogleAPI.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.ListUtils;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.groupssettings.Groupssettings;
import com.google.api.services.groupssettings.GroupssettingsScopes;
import com.google.api.services.groupssettings.model.Groups;

import GoogleAPI.GroupsSettingsGoogleApi;
import GoogleAPI.util.AbstractBaseGoogleApi;
import GoogleAPI.util.AbstractBaseGoogleAuthentication;
import GoogleAPI.util.BasicBatchCallBack;

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
	protected AbstractGoogleJsonClient buildGoogleService(HttpTransport httpTransport, JsonFactory jacksonFactory, HttpRequestInitializer requestInitializer) {
		return new Groupssettings(httpTransport, jacksonFactory, requestInitializer);
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

	public List<Groups> getGroupSettings(String executionGoogleUser, List<String> emails, String fields) {
		if(emails == null || emails.isEmpty()) {
			return null;
		}
				
		BasicBatchCallBack<Groups> callback = new BasicBatchCallBack<>(new ArrayList<Groups>());
		
		try {
			
			getLogger().info("GSuite GroupsSettings APIs - START BATCH getGroupSettings.");
			
			List<List<String>> listOfList = ListUtils.partition(emails, 50);
			
			int i = 0;
			for(List<String> list : listOfList) {
				
				BatchRequest batchRequest = getGroupsSettingsGoogleService(executionGoogleUser).batch();

				for(String email : list) {
										
					getGroupsSettingsGoogleService(executionGoogleUser)
						.groups()
						.get(email)
						.setFields(fields)
						.queue(batchRequest, callback);
				}
				
				getLogger().info("GSuite GroupsSettings APIs - Executing {}° batch request", (i+1)+".");
				batchRequest.execute();
				i++;
			}
			
			getLogger().info("GSuite GroupsSettings APIs - END BATCH getGroupSettings.");

		} catch(GoogleJsonResponseException e) {
			getLogger().error("GSuite GroupsSettings APIs - Google service error in getGroupSettings.");
			handleServiceException(e);
		
		} catch(Exception e) {
			getLogger().error("GSuite GroupsSettings APIs - Critical error in getGroupSettingsbyEmails.", e);
			throw new RuntimeException(e);
		}
		
		return (List<Groups>) callback.getEntities();
	}
	
}
