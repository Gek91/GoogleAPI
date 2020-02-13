package main.GoogleAPI;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;

import main.GoogleAPI.data.DriveFilePermissionRoleEnum;
import main.GoogleAPI.data.DriveFilePermissionTypeEnum;

public interface DriveGoogleApi {

	/*
	 * File
	 */
	public FileList getFileList(String executionGoogleUser, String fields, String orderBy, String query, Integer pageSize, String nextPageToken);
	
	File getFileMetadata(String executionGoogleUser, String fileId, String fields);
	
	//TODO: too many parameters
	public File createFile(String executionGoogleUser, String name, List<String> parentsIds, InputStream fileData, String contentType, boolean keepRevisionForever, String fields);
	
	public File copyFile(String executionGoogleUser, String fileId, String copyName, String mimeType, Set<String> copyParentIds, Map<String, String> properties, boolean writersCanShare, boolean keepRevisionForever, String fields);

	public OutputStream getFileContent(String executionGoogleUser, String fileId);
	
	//TODO upload as spreadsheet/doc/slide...
	//TODO export file content as  
	
	public File createFolder(String executionGoogleUser, String name, List<String> parentsIds);
		
	//TODO: too many parameters
	public File updateFileMetadata(String executionGoogleUser, String fileId, String name, Set<String> addParentIds, Set<String> removeParentIds, Map<String, String> properties, Date lastModifyDatetime);

	public void trashFile(String executionGoogleUser, String fileId);
	
	public void deleteFile(String executionGoogleUser, String fileId);
	
	/*
	 * File permission
	 */		
	public PermissionList getPermissionList(String executionGoogleUser, String fileId);
		
	public Permission getPermission(String executionGoogleUser, String permissionId);
	
	public Permission addPermission(String executionGoogleUser, String fileId, DriveFilePermissionTypeEnum type, DriveFilePermissionRoleEnum role, String value);
	
	public Permission updatePermission(String executionGoogleUser, String fileId, DriveFilePermissionTypeEnum type, DriveFilePermissionRoleEnum role, String value);
		
	public void deletePermission(String executionGoogleUser, String fileId, String permissionId);

		
}
