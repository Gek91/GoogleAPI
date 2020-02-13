package main.GoogleAPI;

import java.util.List;

import com.google.api.services.admin.directory.model.Buildings;
import com.google.api.services.admin.directory.model.CalendarResources;
import com.google.api.services.admin.directory.model.Group;
import com.google.api.services.admin.directory.model.Groups;
import com.google.api.services.admin.directory.model.Members;
import com.google.api.services.admin.directory.model.User;
import com.google.api.services.admin.directory.model.Users;

public interface DirectoryGoogleApi {

	public Group getGroup(String executionGoogleUser, String email, String fields);
	
	public Groups getDomainGroups(String executionGoogleUser, String domain, String nextPageToken, String fields);
	
	public Users getDomainUsers(String executionGoogleUser, String domain, String nextPageToken, String fields);
	
	public Groups getUserGroups(String executionGoogleUser, String userId, String nextPageToken, String fields);
	
	public Members getGroupMembers(String executionGoogleUser, String groupId, String nextPageToken, String fields);
	
	public CalendarResources getCalendarResources(String executionGoogleUser, String customerId, String nextPageToken, String fields);

	public Buildings getResourceBuildings(String executionGoogleUser, String customerId, String nextPageToken, String fields);
	
	public List<User> getUsersDetail(String executionGoogleUser, List<String> userids, String fields, boolean ignoreEntityFailure);
	
}
