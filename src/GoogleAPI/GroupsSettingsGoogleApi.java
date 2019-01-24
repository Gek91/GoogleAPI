package GoogleAPI;

import java.util.List;

import com.google.api.services.groupssettings.model.Groups;

public interface GroupsSettingsGoogleApi {

	public List<Groups> getGroupSettingsbyEmails(String executionGoogleUser, List<String> emails, String fields);

}
