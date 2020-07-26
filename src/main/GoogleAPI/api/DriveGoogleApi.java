package main.GoogleAPI.api;

import java.io.InputStream;
import java.io.OutputStream;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;

public interface DriveGoogleApi {

	/*
	 * File
	 */
	FileList getFileList(String executionGoogleUser, String query, String orderBy, Integer pageSize, String fields, String nextPageToken);
	
	File getFileMetadata(String executionGoogleUser, String fileId, String fields);
	
	OutputStream getFileContent(String executionGoogleUser, String fileId);
	
	File createFile(String executionGoogleUser, InputStream fileData, String contentType, File fileMetadata, String fields);
	
	File updateFileMetadata(String executionGoogleUser, String fileId, File file, String fields);
	
	File copyFile(String executionGoogleUser, String fileId, File fileMetadata, String fields);
	
	OutputStream exportFileContent(String executionGoogleUser, String fileId, String conversionMimeType); 
	
	void deleteFile(String executionGoogleUser, String fileId);
	
	/*
	 * File permission
	 */		
	PermissionList getFilePermissions(String executionGoogleUser, String fileId, String fields, String nextPageToken);
		
	Permission getFilePermissionMetadata(String executionGoogleUser, String fileId, String permissionId, String fields);
	
	Permission addFilePermission(String executionGoogleUser, String fileId, Permission permission);
	
	Permission updateFilePermission(String executionGoogleUser, String fileId, String permissionId, Permission permission);
		
	void removeFilePermission(String executionGoogleUser, String fileId, String permissionId);

}
