package main.GoogleAPI.api.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public Group getGroup(String executionGoogleUser, String email, String fields) {
		
		Group result = null;
		
		try {
			
			getLogger().info("GSuite Admin APIs - START getGroup | email:{}.", email);

			result = getDirectoryGoogleService(executionGoogleUser)
					.groups()
					.get(email)
					.setFields(fields)
					.execute();
			
			getLogger().info("GSuite Admin APIs - END getGroup.");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("GSuite Admin APIs - Google service error in getGroup.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GSuite Admin APIs - Critical error in getGroup.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}


	@Override
	public Groups getDomainGroups(String executionGoogleUser, String domain, String nextPageToken, String fields) {
		
		Groups result = null;

		try {
			
			getLogger().info("GSuite Admin APIs - START getDomainGroups | domain:{} nextPageToken:{}.", domain, nextPageToken);
									
			result = getDirectoryGoogleService(executionGoogleUser)
					.groups()
					.list()
					.setDomain(domain)
					.setPageToken(nextPageToken)
					.setFields(fields)
					.execute();
					
			getLogger().info("GSuite Admin APIs - END getDomainGroups.");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("GSuite Admin APIs - Google service error in getDomainGroups.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GSuite Admin APIs - Critical error in getDomainGroups.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}

	@Override
	public Users getDomainUsers(String executionGoogleUser, String domain, String nextPageToken, String fields) {
		
		Users result = null;
		
		try {
			
			getLogger().info("GSuite Admin APIs - START getDomainUsers | domain:{} nextPageToken:{}.", domain, nextPageToken);
				
			result = getDirectoryGoogleService(executionGoogleUser)
					.users()
					.list()					
					.setDomain(domain)
					.setPageToken(nextPageToken)
					.setFields(fields)
					.execute();
			
			getLogger().info("GSuite Admin APIs - END getDomainUsers.");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("GSuite Admin APIs - Google service error in getDomainUsers.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GSuite Admin APIs - Critical error in getDomainUsers.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}
	
	@Override
	public Groups getUserGroups(String executionGoogleUser, String userId, String nextPageToken, String fields) {
		
		Groups result = null;
		
		try {
		
			getLogger().info("GSuite Admin APIs - START getUserGroups | userId:{}.", userId);
					
			result = getDirectoryGoogleService(executionGoogleUser)
					.groups()
					.list()
					.setUserKey(userId)
					.setPageToken(nextPageToken)
					.setFields(fields)
					.execute();
		
			getLogger().info("GSuite Admin APIs - END getUserGroups.");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("GSuite Admin APIs - Google service error in getUserGroups.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GSuite Admin APIs - Critical error in getUserGroups.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}
	
	@Override
	public Members getGroupMembers(String executionGoogleUser, String groupId, String nextPageToken, String fields) {
		
		Members result = null;
		
		try {
			
			getLogger().info("GSuite Admin APIs - START getGroupMembers | group:{} nextPageToken:{}.", groupId, nextPageToken);
						
			result = getDirectoryGoogleService(executionGoogleUser)
					.members()
					.list(groupId)
					.setFields(fields)
					.setPageToken(nextPageToken)
					.execute();
						
			getLogger().info("GSuite Admin APIs - END getGroupMembers.");

		} catch(GoogleJsonResponseException e) {
			getLogger().error("GSuite Admin APIs - Google service error in getGroupMembers.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GSuite Admin APIs - Critical error in getGroupMembers.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}
	
	@Override 
	public CalendarResources getCalendarResources(String executionGoogleUser, String customerId, String nextPageToken, String fields) {
		
		CalendarResources result = null;
		
		try {
			
			getLogger().info("GSuite Admin APIs - START getCalendarResources | customerId:{} nextPageToken:{}.", customerId, nextPageToken);

			result = getDirectoryGoogleService(executionGoogleUser)
					.resources()
					.calendars()
					.list(customerId)
					.setFields(fields)
					.setPageToken(nextPageToken)
					.execute();
			
			getLogger().info("GSuite Admin APIs - END getCalendarResources.");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("GSuite Admin APIs - Google service error in getCalendarResources.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GSuite Admin APIs - Critical error in getCalendarResources.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}

	@Override
	public Buildings getResourceBuildings(String executionGoogleUser, String customerId, String nextPageToken, String fields) {
		
		Buildings result = null;
		
		try {
			
			getLogger().info("GSuite Admin APIs - START getResourceBuildings | customerId:{} nextPageToken:{}.", customerId, nextPageToken);

			result = getDirectoryGoogleService(executionGoogleUser)
					.resources()
					.buildings()
					.list(customerId)
					.setFields(fields)
					.setPageToken(nextPageToken)
					.execute();
			
			getLogger().info("GSuite Admin APIs - END getResourceBuildings.");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("GSuite Admin APIs - Google service error in getResourceBuildings.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("GSuite Admin APIs - Critical error in getResourceBuildings.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}
	
	@Override
	public List<User> getUsersDetail(String executionGoogleUser, List<String> userIds, String fields, boolean ignoreEntityFailure) {
//		if(userIds == null || userIds.isEmpty())
			return null;
		
//		BasicBatchCallBack<User> callback = new BasicBatchCallBack<>(new ArrayList<User>(), ignoreEntityFailure);
//		
//		try {
//			
//			getLogger().info("GSuite Admin APIs - START BATCH getUsersDetail");
//
//			List<List<String>> listOfList = ListUtils.partition(userIds, 50);
//			
//			int i = 0;
//			for(List<String> list : listOfList) {
//				
//				BatchRequest batchRequest = getDirectoryGoogleService(executionGoogleUser).batch();
//
//				for(String id : list) {
//										
//					getDirectoryGoogleService(executionGoogleUser)
//						.users()
//						.get(id)
//						.setFields(fields)
//						.queue(batchRequest, callback);
//				}
//				
//				getLogger().info("GSuite Admin APIs - Executing {}° batch request", (i+1)+"");
//				batchRequest.execute();
//				i++;
//			}
//			
//			getLogger().info("GSuite Admin APIs - END BATCH getUsersDetail.");
//			
//		} catch(GoogleJsonResponseException e) {
//			getLogger().error("GSuite Admin APIs - Google service error in getUsersDetail.");
//			handleServiceException(e);
//			
//		}  catch(Exception e) {
//			getLogger().error("GSuite Admin APIs - Critical error in getUsersDetail.", e);
//			throw new RuntimeException(e);
//		}
//		
//		return (List<User>) callback.getEntities();
	}

	@Override
	protected AbstractGoogleServiceBatch<Directory> getBatchBuilder(String executionGoogleUser) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AbstractGoogleServiceBatch<Directory> getBatchBuilder(String executionGoogleUser, int operationsInBatch) {
		// TODO Auto-generated method stub
		return null;
	}	
}
