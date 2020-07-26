package main.GoogleAPI.api.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;

import main.GoogleAPI.api.DriveGoogleApi;
import main.GoogleAPI.base.AbstractBaseGoogleApi;
import main.GoogleAPI.base.AbstractBaseGoogleAuthentication;
import main.GoogleAPI.base.AbstractGoogleServiceBatch;
import main.GoogleAPI.base.BasicBatchCallBack;


public class DriveGoogleApiImpl extends AbstractBaseGoogleApi<Drive> implements DriveGoogleApi{

	private static final List<String> SCOPES = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add(DriveScopes.DRIVE);
			add(DriveScopes.DRIVE_FILE);
			add(DriveScopes.DRIVE_METADATA);
		}
	}; 
	
	@Override
	protected Collection<String> getScopes() {
		return SCOPES;
	}

	@Override
	protected Drive buildGoogleService(HttpTransport httpTransport, JsonFactory jacksonFactory, HttpRequestInitializer requestInitializer) {
		return new Drive(httpTransport, jacksonFactory, requestInitializer);
	}
	
	private Drive getDriveGoogleService(String executionGoogleUser) {
		return getGoogleService(executionGoogleUser);
	}

	//constructor
	public DriveGoogleApiImpl(AbstractBaseGoogleAuthentication authenticationService) {
		super(authenticationService);
	}
	
	@Override
	public DriveBatchBuilder getBatchBuilder(String executionGoogleUser) {
	
		return new DriveBatchBuilder(executionGoogleUser, getDriveGoogleService(executionGoogleUser));
	}
	
	@Override
	public DriveBatchBuilder getBatchBuilder(String executionGoogleUser, int operationsInBatch) {
		
		return new DriveBatchBuilder(executionGoogleUser, getDriveGoogleService(executionGoogleUser), operationsInBatch);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * Methods
	 */
	
	@Override
	public FileList getFileList(String executionGoogleUser, String query, String orderBy, Integer pageSize, String fields, String nextPageToken) {
		
		FileList fileList = null;
		
		try {
			getLogger().info("Drive APIs - START getFileList | pageToken:{}", nextPageToken);
			
			fileList = getFileListOperation(executionGoogleUser, query, orderBy, pageSize, fields, nextPageToken).execute();
				
			getLogger().info("Drive APIs - END getFileList | pageToken:{}", nextPageToken);
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Drive APIs - Google service error in getFileList.");
			handleServiceException(e);
			
		} catch (Exception e) {
			getLogger().error("Drive APIs - Critical error in getFileList.", e);
			throw new RuntimeException(e);
		}
		
		return fileList;
	}
	
	@Override
	public File getFileMetadata(String executionGoogleUser, String fileId, String fields) {
		
		File file = null;
		
		try {
			
			getLogger().info("Drive APIs - START getFileMetadata | id:{}", fileId);
			
			file = getFileMetadataOperation(executionGoogleUser, fileId, fields).execute();

			getLogger().info("Drive APIs - END getFileMetadata | id:{}", fileId);

		} catch(GoogleJsonResponseException e) {
			getLogger().error("Drive APIs - Google service error in getFileMetadata.");
			handleServiceException(e);
			
		} catch (Exception e) {
			getLogger().error("Drive APIs - Critical error in getFileMetadata.", e);
			throw new RuntimeException(e);
		}
		
		return file;
	}
	
	@Override
	public OutputStream getFileContent(String executionGoogleUser, String fileId) {
		
		OutputStream outputStream = new ByteArrayOutputStream();
		
		try {
			
			getLogger().info("Drive APIs - START getFileContent | id:{}", fileId);
			
			getFileMetadataOperation(executionGoogleUser, fileId, null).executeMediaAndDownloadTo(outputStream);
			
			getLogger().info("Drive APIs - END getFileContent | id:{}", fileId);
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Drive APIs - Google service error in getFileContent.");
			handleServiceException(e);
			
		} catch (Exception e) {
			getLogger().error("Drive APIs - Critical error in getFileContent.", e);
			throw new RuntimeException(e);
		}
		
		return outputStream;
	}
	
	@Override
	public File createFile(String executionGoogleUser, InputStream fileData, String contentType, File fileMetadata, String fields) {
		
		try {
			
			getLogger().info("Drive APIs - START getFileMetadata");

			fileMetadata = createFileOperation(executionGoogleUser, fileData, contentType, fileMetadata, fields).execute();
			
			getLogger().info("Drive APIs - END getFileMetadata");

		} catch(GoogleJsonResponseException e) {
			getLogger().error("Drive APIs - Google service error in createFile.");
			handleServiceException(e);
			
		} catch (Exception e) {
			getLogger().error("Drive APIs - Critical error in createFile.", e);
			throw new RuntimeException(e);
		}
		
		return fileMetadata;
	}
	
	@Override
	public File updateFileMetadata(String executionGoogleUser, String fileId, File file, String fields) {
		
		try {
			
			getLogger().info("Drive APIs - START updateFileMetadata | id:{}", fileId);
			
			file = updateFileMetadataOperation(executionGoogleUser, fileId, file, fields).execute();

			getLogger().info("Drive APIs - END updateFileMetadata | id:{}", fileId);
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Drive APIs - Google service error in updateFileMetadata.");
			handleServiceException(e);
			
		} catch (Exception e) {
			getLogger().error("Drive APIs - Critical error in updateFileMetadata.", e);
			throw new RuntimeException(e);
		}
		
		return file;
	}
	
	@Override
	public File copyFile(String executionGoogleUser, String fileId, File fileMetadata, String fields) {
		
		try {
			
			getLogger().info("Drive APIs - START copyFile | id:{}", fileId);

			fileMetadata = copyFileOperation(executionGoogleUser, fileId, fileMetadata, fields).execute();			
			
			getLogger().info("Drive APIs - END copyFile | id:{}", fileId);

		} catch(GoogleJsonResponseException e) {
			getLogger().error("Drive APIs - Google service error in copyFile.");
			handleServiceException(e);
			
		} catch (Exception e) {
			getLogger().error("Drive APIs - Critical error in copyFile.", e);
			throw new RuntimeException(e);
		}
		
		return fileMetadata;
	}

	@Override
	public OutputStream exportFileContent(String executionGoogleUser, String fileId, String conversionMimeType) {
		
		OutputStream outputStream = new ByteArrayOutputStream();
		
		try {
			
			getLogger().info("Drive APIs - START exportFileContent | id:{}", fileId);

			exportFileOperation(executionGoogleUser, fileId, conversionMimeType).executeMediaAndDownloadTo(outputStream);
			
			getLogger().info("Drive APIs - END exportFileContent | id:{}", fileId);
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Drive APIs - Google service error in exportFileContent.");
			handleServiceException(e);
			
		} catch (Exception e) {
			getLogger().error("Drive APIs - Critical error in exportFileContent.", e);
			throw new RuntimeException(e);
		}
		
		return outputStream;
	}
	
	@Override
	public void deleteFile(String executionGoogleUser, String fileId) {
		
		try {
			
			getLogger().info("Drive APIs - START deleteFile | id:{}", fileId);
			
			deleteFileOperation(executionGoogleUser, fileId).execute();

			getLogger().info("Drive APIs - START deleteFile | id:{}", fileId);
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Drive APIs - Google service error in deleteFile.");
			handleServiceException(e);
			
		} catch (Exception e) {
			getLogger().error("Drive APIs - Critical error in deleteFile.", e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public PermissionList getFilePermissions(String executionGoogleUser, String fileId, String fields, String nextPageToken) {
		
		PermissionList drivePermissionsList = null;
		
		try {
			
			getLogger().info("Drive APIs - START getFilePermissions | id:{} - pageToken:{}", fileId, nextPageToken);
			
			drivePermissionsList = getFilePermissionsOperation(executionGoogleUser, fileId, fields, nextPageToken).execute();
				
			getLogger().info("Drive APIs - END getFilePermissions | id:{} - pageToken:{}", fileId, nextPageToken);
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Drive APIs - Google service error in getFilePermissions.");
			handleServiceException(e);
			
		} catch (Exception e) {
			getLogger().error("Drive APIs - Critical error in getFilePermissions.", e);
			throw new RuntimeException(e);
		}
		
		return drivePermissionsList;
	}

	@Override
	public Permission getFilePermissionMetadata(String executionGoogleUser, String fileId, String permissionId, String fields) {
		
		Permission permission = null;
		
		try {

			getLogger().info("Drive APIs - START updateFilePermission | fileId:{} - permissionId:{}", fileId, permissionId);

			permission = getFilePermissionMetadataOperation(executionGoogleUser, fileId, permissionId, fields).execute();
			
			getLogger().info("Drive APIs - START updateFilePermission | fileId:{} - permissionId:{}", fileId, permissionId);
			
		}  catch(GoogleJsonResponseException e) {
			getLogger().error("Drive APIs - Google service error in getFilePermissionMetadata.");
			handleServiceException(e);
			
		} catch (Exception e) {
			getLogger().error("Drive APIs - Critical error in getFilePermissionMetadata.", e);
			throw new RuntimeException(e);
		}
		
		return permission;
	}
	
	@Override
	public Permission addFilePermission(String executionGoogleUser, String fileId, Permission permission) {
		
		try {
			
			getLogger().info("Drive APIs - START addFilePermission | id:{}", fileId);
			
			permission = addFilePermissionOperation(executionGoogleUser, fileId, permission).execute();
					
			getLogger().info("Drive APIs - END addFilePermission | id:{}", fileId);
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Drive APIs - Google service error in addFilePermission.");
			handleServiceException(e);
			
		} catch (Exception e) {
			getLogger().error("Drive APIs - Critical error in addFilePermission.", e);
			throw new RuntimeException(e);
		}
		
		return permission;
	}

	@Override
	public Permission updateFilePermission(String executionGoogleUser, String fileId, String permissionId, Permission permission) {

		try {
			
			getLogger().info("Drive APIs - START updateFilePermission | fileId:{} - permissionId:{}", fileId, permissionId);
		
			permission = updateFilePermissionOperation(executionGoogleUser, fileId, permissionId, permission).execute();
			
			getLogger().info("GDrive APIs - END updateFilePermission | fileId:{} - permissionId:{}", fileId, permissionId);
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Drive APIs - Google service error in updateFilePermission.");
			handleServiceException(e);
			
		} catch (Exception e) {
			getLogger().error("Drive APIs - Critical error in updateFilePermission.", e);
			throw new RuntimeException(e);
		}

		return permission;
	}
	
	@Override
	public void removeFilePermission(String executionGoogleUser, String fileId, String permissionId) {
		
		try {
			
			getLogger().info("Drive APIs - START removeFilePermission | fileId:{} - permissionId:{}", fileId, permissionId);
			
			removeFilePermissionOperation(executionGoogleUser, fileId, permissionId).execute();	
			
			getLogger().info("GDrive APIs - END removeFilePermission | fileId:{} - permissionId:{}", fileId, permissionId);
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Drive APIs - Google service error in removeFilePermission.");
			handleServiceException(e);
			
		} catch (IOException e) {
			getLogger().error("Drive APIs - Critical error in removeFilePermission.", e);
			throw new RuntimeException(e);
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * Batch builder
	 */
	
	public class DriveBatchBuilder extends AbstractGoogleServiceBatch<Drive> {
		
		public DriveBatchBuilder(String executionGoogleUser, Drive service) {
			super(executionGoogleUser, service);
		}
		
		public DriveBatchBuilder(String executionGoogleUser, Drive service, int operationInBatch) {
			super(executionGoogleUser, service, operationInBatch);
		}
		
		public DriveBatchBuilder queueGetFileMetadataOperation(String fileId, String fields, BatchFileGoogleCallback callback) {
			
			queueOperation(getFileMetadataOperation(this.executionGoogleUser, fileId, fields), callback);
			
			return this;
		}
		
		public DriveBatchBuilder queueCreateFileOperation(InputStream fileData, String contentType, File fileMetadata, String fields, BatchFileGoogleCallback callback) {
			
			queueOperation(createFileOperation(fields, fileData, contentType, fileMetadata, fields), callback);
			
			return this;
		}
		
		public DriveBatchBuilder queueUpdateFileMetadataOperation(String fileId, File file, String fields, BatchFileGoogleCallback callback) {
			
			queueOperation(updateFileMetadataOperation(this.executionGoogleUser, fileId, file, fields), callback);
			
			return this;
		}
		
		public DriveBatchBuilder queueCopyFileOperation(String fileId, File fileMetadata, String fields, BatchFileGoogleCallback callback) {
			
			queueOperation(copyFileOperation(this.executionGoogleUser, fileId, fileMetadata, fields), callback);
			
			return this;
		}
		
		public DriveBatchBuilder queueDeleteFileOperation(String fileId) {
			
			queueVoidOperation(deleteFileOperation(this.executionGoogleUser, fileId));
			
			return this;
		}
		
		public DriveBatchBuilder queueGetFilePermissionMetadataOperation(String fileId, String permissionId, String fields, BatchPermissionGoogleCallback callback) {
			
			queueOperation(getFilePermissionMetadataOperation(this.executionGoogleUser, fileId, permissionId, fields), callback);
			
			return this;
		}
		
		public DriveBatchBuilder queueAddFilePermissionOperation(String fileId, Permission permission, BatchPermissionGoogleCallback callback) {
			
			queueOperation(addFilePermissionOperation(this.executionGoogleUser, fileId, permission), callback);
			
			return this;
		}
		
		public DriveBatchBuilder queueUpdateFilePermissionsOperation(String fileId, String permissionId, Permission permission, BatchPermissionGoogleCallback callback) {
			
			queueOperation(updateFilePermissionOperation(this.executionGoogleUser, fileId, permissionId, permission), callback);
			
			return this;
		}
		
		public DriveBatchBuilder queueRemoveFilePermissionOperation(String fileId, String permissionId) {
		
			queueVoidOperation(removeFilePermissionOperation(this.executionGoogleUser, fileId, permissionId));
			
			return this;
		}		
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * Operations
	 */
	
	private Drive.Files.List getFileListOperation(String executionGoogleUser, String query, String orderBy, Integer pageSize, String fields, String nextPageToken) {
		
		try {
			
			return getDriveGoogleService(executionGoogleUser).files().list()
					.setFields(fields)
					.setOrderBy(orderBy)
					.setQ(query)
					.setPageSize(pageSize)
					.setPageToken(nextPageToken);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	private Drive.Files.Get getFileMetadataOperation(String executionGoogleUser, String fileId, String fields) {
		
		try {
			
			return getDriveGoogleService(executionGoogleUser).files().get(fileId).setFields(fields);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Drive.Files.Create createFileOperation(String executionGoogleUser, InputStream fileData, String contentType, File fileMetadata, String fields) {
		
		try {
			
			return getDriveGoogleService(executionGoogleUser).files().create(fileMetadata, new InputStreamContent(contentType, fileData)).setFields(fields);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Drive.Files.Update updateFileMetadataOperation(String executionGoogleUser, String fileId, File file, String fields) {
		
		try {
			
			return getDriveGoogleService(executionGoogleUser).files().update(fileId, file).setFields(fields);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Drive.Files.Copy copyFileOperation(String executionGoogleUser, String fileId, File fileMetadata, String fields) {
		
		try {
			
			return getDriveGoogleService(executionGoogleUser).files().copy(fileId, fileMetadata).setFields(fields);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Drive.Files.Export exportFileOperation(String executionGoogleUser, String fileId, String conversionMimeType) {
		
		try{
			
			return getDriveGoogleService(executionGoogleUser).files().export(fileId, conversionMimeType);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Drive.Files.Delete deleteFileOperation(String executionGoogleUser, String fileId) {
		
		try {
			
			return getDriveGoogleService(executionGoogleUser).files().delete(fileId);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Drive.Permissions.List getFilePermissionsOperation(String executionGoogleUser, String fileId, String fields, String nextPageToken) {
		
		try {

			return getDriveGoogleService(executionGoogleUser).permissions().list(fileId)
					.setFields(fields)
					.setPageToken(nextPageToken);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
	
	private Drive.Permissions.Get getFilePermissionMetadataOperation(String executionGoogleUser, String fileId, String permissionId, String fields) {
		
		try {

			return getDriveGoogleService(executionGoogleUser).permissions().get(fileId, permissionId).setFields(fields);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Drive.Permissions.Create addFilePermissionOperation(String executionGoogleUser, String fileId, Permission permission) {
		
		try {
				
			return getDriveGoogleService(executionGoogleUser).permissions().create(fileId, permission).setSendNotificationEmail(false);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Drive.Permissions.Update updateFilePermissionOperation(String executionGoogleUser, String fileId, String permissionId, Permission permission) {
		
		try {
			
			return getDriveGoogleService(executionGoogleUser).permissions().update(fileId, permissionId, permission);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Drive.Permissions.Delete removeFilePermissionOperation(String executionGoogleUser, String fileId, String permissionId) {
		
		try {
			
			return getDriveGoogleService(executionGoogleUser).permissions().delete(fileId, permissionId);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * Callback
	 */
	
	public static class BatchPermissionGoogleCallback extends BasicBatchCallBack<Permission> {
		
		private String fileId;
		private Permission permission;
		
		public BatchPermissionGoogleCallback() { };
		
		public BatchPermissionGoogleCallback(String fileId) {
			this.fileId = fileId;
		}
				
		public String getFileId() {
			return fileId;
		}

		public void setFileId(String fileId) {
			this.fileId = fileId;
		}

		public Permission getPermission() {
			return permission;
		}

		public void setPermission(Permission permission) {
			this.permission = permission;
		}

		@Override
		public Permission onSuccessLogic(Permission entity) {
			getLogger().info("Drive APIs - Success batch call permission: {}", entity.getEmailAddress());

			this.permission = entity;
			
			return entity;
		}

		@Override
		public void onFailureLogic(GoogleJsonError e) {
			getLogger().error("Drive APIs - Error Message in Batch operation on permission: {}", e.getMessage());
		}
	}
	
	public static class BatchFileGoogleCallback extends BasicBatchCallBack<File> {

		private String fileId;
		
		public String getFileId() {
			return this.fileId;
		}
		
		@Override
		public File onSuccessLogic(File entity) {
			getLogger().info("Drive APIs - Success batch call file: {}", entity.getId());

			fileId = entity.getId();
			
			return entity;
		}

		@Override
		public void onFailureLogic(GoogleJsonError e) {
			getLogger().error("Drive APIs - Error Message in Batch operation on file: {}", e.getMessage());
		}
	}
}
