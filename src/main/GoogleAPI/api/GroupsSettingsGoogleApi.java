package main.GoogleAPI.api;

import java.util.List;

import com.google.api.services.groupssettings.model.Groups;

public interface GroupsSettingsGoogleApi {

	public List<Groups> getGroupSettings(String executionGoogleUser, List<String> emails, String fields, boolean ignoreEntityFailure);

}
