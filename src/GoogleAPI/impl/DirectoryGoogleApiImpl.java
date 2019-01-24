package GoogleAPI.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.ListUtils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.DirectoryScopes;
import com.google.api.services.admin.directory.model.Buildings;
import com.google.api.services.admin.directory.model.CalendarResources;
import com.google.api.services.admin.directory.model.Group;
import com.google.api.services.admin.directory.model.Groups;
import com.google.api.services.admin.directory.model.Members;
import com.google.api.services.admin.directory.model.User;
import com.google.api.services.admin.directory.model.Users;

import GoogleAPI.DirectoryGoogleApi;
import GoogleAPI.util.AbstractBaseGoogleApi;
import GoogleAPI.util.AbstractBaseGoogleAuthentication;



public class DirectoryGoogleApiImpl extends AbstractBaseGoogleApi implements DirectoryGoogleApi {

	private static final List<String> SCOPES = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add(DirectoryScopes.ADMIN_DIRECTORY_GROUP);
			add(DirectoryScopes.ADMIN_DIRECTORY_USER);
			add(DirectoryScopes.ADMIN_DIRECTORY_RESOURCE_CALENDAR);
		}
	};
	
	@Override
	protected Collection<String> getScopes() {
		return SCOPES;
	}

	@Override
	protected Directory buildGoogleService(HttpTransport httpTransport, JacksonFactory jacksonFactory,
			Credential credential) {
		return new Directory(HTTP_TRANSPORT, JSON_FACTORY, credential);
	}
	
	private Directory getDirectoryGoogleService(String executionGoogleUser) {
		return (Directory) getGoogleService(executionGoogleUser);
	}
	
	//constructor
	public DirectoryGoogleApiImpl(AbstractBaseGoogleAuthentication authenticationService) {
		super(authenticationService);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public Group getGroupByEmail(String executionGoogleUser, String email, String fields) {
		
		Group result = null;
		
		try {
			
			getLogger().info("GSuite Admin APIs - retrieve group by email {}...", email);

			result = getDirectoryGoogleService(executionGoogleUser).groups()
					.get(email)
					.setFields(fields)
					.execute();
			
			getLogger().info("GSuite Admin APIs - end retrieving group by email {}.", email);

		}  catch(Exception e) {
			getLogger().error("Error in retrieving group", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}


	@Override
	public Groups getDomainGroupsList(String executionGoogleUser, String domain, String nextPageToken, String fields) {
		
		Groups result = null;

		try {
			
			getLogger().info("GSuite Admin APIs - retrieve groups list for domain {} and nextPageToken {}..", domain, nextPageToken);
									
			result = getDirectoryGoogleService(executionGoogleUser).groups().list()
					.setDomain(domain)
					.setPageToken(nextPageToken)
					.setFields(fields)
					.execute();
					
			getLogger().info("GSuite Admin APIs - end retrieving groups list for domain {}.", domain);
			
		} catch(Exception e) {
			getLogger().error("Error retrieving groups list", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}

	@Override
	public Users getDomainUsersList(String executionGoogleUser, String domain, String nextPageToken, String fields) {
		
		Users result = null;
		
		try {
			
			getLogger().info("GSuite Admin APIs - retrieve users list for domain {} and nextPageToken {}...", domain, nextPageToken);
				
			result = getDirectoryGoogleService(executionGoogleUser).users().list()
						.setDomain(domain)
						.setPageToken(nextPageToken)
						.setFields(fields)
						.execute();
			
			getLogger().info("GSuite Admin APIs - end retrieving users list for domain {}.", domain);
			
		} catch(Exception e) {
			getLogger().error("Error retrieving groups list", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}
	
	@Override
	public List<Group> listUserGroups(String executionGoogleUser, String userId, String fields) {
		
		List<Group> result = new ArrayList<Group>();
		
		try {
		
			getLogger().info("GSuite Admin APIs - retrieve groups list for user {}...", userId);
			
			Groups groups = null;
			
			String nextPageToken = null;
			
			do {
				groups = getDirectoryGoogleService(executionGoogleUser).groups().list()
						.setUserKey(userId)
						.setPageToken(nextPageToken)
						.setFields(fields)
						.execute();
	
				result.addAll(groups.getGroups());
				
			} while(groups.getNextPageToken() != null) ;
		
			getLogger().info("GSuite Admin APIs - end retrieving groups list for user {}.", userId);
			
		} catch(Exception e) {
			getLogger().error("Error retrieving groups list", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}
	
	@Override
	public Members getGroupUsersList(String executionGoogleUser, String groupId, String nextPageToken, String fields) {
		
		Members result = null;
		
		try {
			
			getLogger().info("GSuite Admin APIs - retrieve group {} members and nextPageToken {}...", groupId, nextPageToken);
						
			result = getDirectoryGoogleService(executionGoogleUser).members()
					.list(groupId)
					.setFields(fields)
					.setPageToken(nextPageToken)
					.execute();
						
			getLogger().info("GSuite Admin APIs - end retrieving group {} members.", groupId);

		} catch(Exception e) {
			getLogger().error("Error retrieving group members", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}
	
	@Override 
	public CalendarResources getCalendarResources(String executionGoogleUser, String customerId, String nextPageToken, String fields) {
		
		CalendarResources result = null;
		
		try {
			
			getLogger().info("GSuite Admin APIs - retrieve calendar resources with customer id {} and nextPageToken {}...", customerId, nextPageToken);

			result = getDirectoryGoogleService(executionGoogleUser).resources().calendars()
					.list(customerId)
					.setFields(fields)
					.setPageToken(nextPageToken)
					.execute();
			
			getLogger().info("GSuite Admin APIs - end retrieving calendar resources with customer id {}.", customerId);
			
		} catch(Exception e) {
			getLogger().error("Error retrieving calendar resources", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}

	@Override
	public Buildings getResourceBuildingList(String executionGoogleUser, String customerId, String nextPageToken, String fields) {
		
		Buildings result = null;
		
		try {
			
			getLogger().info("GSuite Admin APIs - retrieve building resources with customer id {} and nextPageToken {}...", customerId, nextPageToken);

			result = getDirectoryGoogleService(executionGoogleUser).resources().buildings()
					.list(customerId)
					.setFields(fields)
					.setPageToken(nextPageToken)
					.execute();
			
			getLogger().info("GSuite Admin APIs - end retrieving building resources with customer id {}.", customerId);
			
		} catch(Exception e) {
			getLogger().error("Error retrieving building resources", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}
	
	@Override
	public List<User> getUsersDetails(String executionGoogleUser, List<String> userids, String fields) {
		if(userids == null)
			return null;
		
		List<BatchUserGoogleCallback> callbacks = new ArrayList<BatchUserGoogleCallback>();
		
		try {
			
			getLogger().info("GSuite Admin APIs - retrieve users detail batch...");

			List<List<String>> listOfList = ListUtils.partition(userids, 50);
			
			int i = 0;
			for(List<String> list : listOfList) {
				
				BatchRequest batchRequest = getDirectoryGoogleService(executionGoogleUser).batch();

				for(String id : list) {
					
					BatchUserGoogleCallback callback = new BatchUserGoogleCallback();
					
					getDirectoryGoogleService(executionGoogleUser).users()
						.get(id)
						.setFields(fields)
						.queue(batchRequest, callback);

					callbacks.add(callback);
				}
				
				getLogger().info("Executing {}° batch request", (i+1)+"");
				batchRequest.execute();
				i++;
			}
			
			getLogger().info("GSuite Admin APIs - end retrieving users detail batch.");
			
		} catch(Exception e) {
			getLogger().error("Error retrieving group members", e);
			throw new RuntimeException(e);
		}
		
		List<User> result = new ArrayList<User>();
		for(BatchUserGoogleCallback callback : callbacks) {
			
			if(callback.getResult() != null){
				result.add(callback.getResult());
			}
		}
		
		return result;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Callback

	class BatchUserGoogleCallback extends JsonBatchCallback<User> {
		
		private User result;
				
		public User getResult() {
			return result;
		}

		public void setResult(User result) {
			this.result = result;
		}

		@Override
		  public void onSuccess(User obj, HttpHeaders responseHeaders) { 
			
			this.result = obj;
		}
		
		@Override
	    public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
			getLogger().error("GDrive APIs - Error Message in Batch operation : " + e.getMessage());
	    }
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
