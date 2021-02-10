package main.GoogleAPI.api.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.DirectoryScopes;
import com.google.api.services.admin.directory.model.Buildings;
import com.google.api.services.admin.directory.model.CalendarResources;
import com.google.api.services.admin.directory.model.Group;
import com.google.api.services.admin.directory.model.Groups;
import com.google.api.services.admin.directory.model.Members;
import com.google.api.services.admin.directory.model.User;
import com.google.api.services.admin.directory.model.Users;

import main.GoogleAPI.api.DirectoryGoogleApi;
import main.GoogleAPI.base.AbstractBaseGoogleApi;
import main.GoogleAPI.base.AbstractBaseGoogleAuthentication;
import main.GoogleAPI.base.AbstractGoogleServiceBatch;
import main.GoogleAPI.base.BasicBatchCallBack;



public class DirectoryGoogleApiImpl extends AbstractBaseGoogleApi<Directory> implements DirectoryGoogleApi {

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
	protected Directory buildGoogleService(HttpTransport httpTransport, JsonFactory jacksonFactory, HttpRequestInitializer requestInitializer) {
		return new Directory(httpTransport, jsonFactory, requestInitializer);
	}
	
	private Directory getDirectoryGoogleService(String executionGoogleUser) {
		return getGoogleService(executionGoogleUser);
	}
	
	//constructor
	public DirectoryGoogleApiImpl(AbstractBaseGoogleAuthentication authenticationService) {
		super(authenticationService);
	}
	
	@Override
	protected AbstractGoogleServiceBatch<Directory> getBatchBuilder(String executionGoogleUser) {
		return new DirectoryBatchBuilder(executionGoogleUser, getDirectoryGoogleService(executionGoogleUser));
	}

	@Override
	protected AbstractGoogleServiceBatch<Directory> getBatchBuilder(String executionGoogleUser, int operationsInBatch) {
		return new DirectoryBatchBuilder(executionGoogleUser, getDirectoryGoogleService(executionGoogleUser), operationsInBatch);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public Group getGroup(String executionGoogleUser, String email, String fields) {
		
		Group result = null;
		
		try {
			
			getLogger().info("Directory APIs - START getGroup | email:{}.", email);

			result = getGroupOperation(executionGoogleUser, email, fields).execute();
			
			getLogger().info("Directory APIs - END getGroup.");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Directory APIs - Google service error in getGroup.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Directory APIs - Critical error in getGroup.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}


	@Override
	public Groups getDomainGroups(String executionGoogleUser, String domain, String nextPageToken, String fields) {
		
		Groups result = null;

		try {
			
			getLogger().info("Directory APIs - START getDomainGroups | domain:{} nextPageToken:{}.", domain, nextPageToken);
									
			result = getDirectoryGoogleService(executionGoogleUser)
					.groups()
					.list()
					.setDomain(domain)
					.setPageToken(nextPageToken)
					.setFields(fields)
					.execute();
					
			getLogger().info("Directory APIs - END getDomainGroups.");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Directory APIs - Google service error in getDomainGroups.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Directory APIs - Critical error in getDomainGroups.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}

	@Override
	public Users getDomainUsers(String executionGoogleUser, String domain, String nextPageToken, String fields) {
		
		Users result = null;
		
		try {
			
			getLogger().info("Directory APIs - START getDomainUsers | domain:{} nextPageToken:{}.", domain, nextPageToken);
				
			result = getDirectoryGoogleService(executionGoogleUser)
					.users()
					.list()					
					.setDomain(domain)
					.setPageToken(nextPageToken)
					.setFields(fields)
					.execute();
			
			getLogger().info("Directory APIs - END getDomainUsers.");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Directory APIs - Google service error in getDomainUsers.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Directory APIs - Critical error in getDomainUsers.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}
	
	@Override
	public Groups getUserGroups(String executionGoogleUser, String userId, String nextPageToken, String fields) {
		
		Groups result = null;
		
		try {
		
			getLogger().info("Directory APIs - START getUserGroups | userId:{}.", userId);
					
			result = getDirectoryGoogleService(executionGoogleUser)
					.groups()
					.list()
					.setUserKey(userId)
					.setPageToken(nextPageToken)
					.setFields(fields)
					.execute();
		
			getLogger().info("Directory APIs - END getUserGroups.");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Directory APIs - Google service error in getUserGroups.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Directory APIs - Critical error in getUserGroups.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}
	
	@Override
	public Members getGroupMembers(String executionGoogleUser, String groupId, String nextPageToken, String fields) {
		
		Members result = null;
		
		try {
			
			getLogger().info("Directory APIs - START getGroupMembers | group:{} nextPageToken:{}.", groupId, nextPageToken);
						
			result = getDirectoryGoogleService(executionGoogleUser)
					.members()
					.list(groupId)
					.setFields(fields)
					.setPageToken(nextPageToken)
					.execute();
						
			getLogger().info("Directory APIs - END getGroupMembers.");

		} catch(GoogleJsonResponseException e) {
			getLogger().error("Directory APIs - Google service error in getGroupMembers.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Directory APIs - Critical error in getGroupMembers.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}
	
	@Override 
	public CalendarResources getCalendarResources(String executionGoogleUser, String customerId, String nextPageToken, String fields) {
		
		CalendarResources result = null;
		
		try {
			
			getLogger().info("Directory APIs - START getCalendarResources | customerId:{} nextPageToken:{}.", customerId, nextPageToken);

			result = getDirectoryGoogleService(executionGoogleUser)
					.resources()
					.calendars()
					.list(customerId)
					.setFields(fields)
					.setPageToken(nextPageToken)
					.execute();
			
			getLogger().info("Directory APIs - END getCalendarResources.");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Directory APIs - Google service error in getCalendarResources.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Directory APIs - Critical error in getCalendarResources.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}

	@Override
	public Buildings getResourceBuildings(String executionGoogleUser, String customerId, String nextPageToken, String fields) {
		
		Buildings result = null;
		
		try {
			
			getLogger().info("Directory APIs - START getResourceBuildings | customerId:{} nextPageToken:{}.", customerId, nextPageToken);

			result = getDirectoryGoogleService(executionGoogleUser)
					.resources()
					.buildings()
					.list(customerId)
					.setFields(fields)
					.setPageToken(nextPageToken)
					.execute();
			
			getLogger().info("Directory APIs - END getResourceBuildings.");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Directory APIs - Google service error in getResourceBuildings.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GDirectory APIs - Critical error in getResourceBuildings.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}
	
	@Override
	public User getUserDetail(String executionGoogleUser, String userId, String fields) {
	
		User result = null;
		
		try {
			
			getLogger().info("Directory APIs - START getUsersDetail | email: {}", userId);

			result = getUserOperation(executionGoogleUser, userId, fields).execute();			
					
			getLogger().info("Directory APIs - END getUsersDetail.");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Directory APIs - Google service error in getUsersDetail.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Directory APIs - Critical error in getUsersDetail.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Batch builder
	
	public class DirectoryBatchBuilder extends AbstractGoogleServiceBatch<Directory> {
	
		public DirectoryBatchBuilder(String executionGoogleUser, Directory service) {
			super(executionGoogleUser, service);
		}
		
		public DirectoryBatchBuilder(String executionGoogleUser, Directory service, int operationInBatch) {
			super(executionGoogleUser, service, operationInBatch);
		}
		
		public DirectoryBatchBuilder queueGetGroupOperation(String groupId, String fields, BatchGroupGoogleCallback callback) {
		
			queueOperation(getGroupOperation(this.executionGoogleUser, groupId, fields), callback);
		
			return this;
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Operations
	
	private Directory.Groups.Get getGroupOperation(String executionGoogleUser, String groupId, String fields) {
	
		try {
			return getDirectoryGoogleService(executionGoogleUser).groups().get(groupId).setFields(fields);
		
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Directory.Users.Get getUserOperation(String executionGoogleUser, String userId, String fields) {
		
		try {
			return getDirectoryGoogleService(executionGoogleUser).users().get(userId).setFields(fields);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//CallBacks
	
	public static class BatchGroupGoogleCallback extends BasicBatchCallBack<Group> {
	
		Group group;
		
		public Group getGroup() {
			return group;
		}
		
		@Override
		public Group onSuccessLogic(Group entity) {
			getLogger().info("Directory APIs - Success batch call group: {}", entity.getId());
		
			this.group = entity;
		
			return entity;
		}
		
		@Override
		public void onFailureLogic(GoogleJsonError e) {
			getLogger().error("Directory APIs - Error Message in Batch operation on group: {}", e.getMessage());
		}
	
	}
}
