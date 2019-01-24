package GoogleAPI;

import java.util.List;

import com.google.api.services.admin.directory.model.Buildings;
import com.google.api.services.admin.directory.model.CalendarResources;
import com.google.api.services.admin.directory.model.Group;
import com.google.api.services.admin.directory.model.Groups;
import com.google.api.services.admin.directory.model.Members;
import com.google.api.services.admin.directory.model.User;
import com.google.api.services.admin.directory.model.Users;

public interface DirectoryGoogleApi {

	public Group getGroupByEmail(String executionGoogleUser, String email, String fields);
	
	public Groups getDomainGroupsList(String executionGoogleUser, String domain, String nextPageToken, String fields);
	
	public Users getDomainUsersList(String executionGoogleUser, String domain, String nextPageToken, String fields);
	
	public List<Group> listUserGroups(String executionGoogleUser, String userId, String fields);
	
	public Members getGroupUsersList(String executionGoogleUser, String groupId, String nextPageToken, String fields);
	
	public CalendarResources getCalendarResources(String executionGoogleUser, String customerId, String nextPageToken, String fields);

	public Buildings getResourceBuildingList(String executionGoogleUser, String customerId, String nextPageToken, String fields);
	
	public List<User> getUsersDetails(String executionGoogleUser, List<String> userids, String fields);
	
}
