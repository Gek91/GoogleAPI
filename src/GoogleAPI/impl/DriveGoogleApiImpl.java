package GoogleAPI.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.Update;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;
import com.google.api.services.drive.model.Revision;

import GoogleAPI.DriveGoogleApi;
import GoogleAPI.util.AbstractBaseGoogleApi;
import GoogleAPI.util.AbstractBaseGoogleAuthentication;


public class DriveGoogleApiImpl extends AbstractBaseGoogleApi implements DriveGoogleApi{

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
	protected AbstractGoogleJsonClient buildGoogleService(HttpTransport httpTransport, JacksonFactory jacksonFactory,
			Credential credential) {
		return new Drive(httpTransport, jacksonFactory, credential);
	}
	
	private Drive getDriveGoogleService(String executionGoogleUser) {
		return (Drive) getGoogleService(executionGoogleUser);
	}

	//constructor
	public DriveGoogleApiImpl(AbstractBaseGoogleAuthentication authenticationService) {
		super(authenticationService);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public File uploadNewFile(String executionGoogleUser, InputStream fileData, String contentType, String fileName, List<String> folderIds, boolean keepRevisionForever, String fields) {
		
		File uploadedFile = null;
		
		try {
			
			File driveFileInfo = new File();
			
			if (StringUtils.isNotBlank(fileName)) {
				driveFileInfo.setName(fileName);
			}
			
			if (folderIds != null && !folderIds.isEmpty()) {
				driveFileInfo.setParents(folderIds);
			}
			
			getLogger().info("GDrive APIs - Uploading file ...");
			
			uploadedFile = getDriveGoogleService(executionGoogleUser).files().create(driveFileInfo, new InputStreamContent(contentType, fileData))
					.setKeepRevisionForever(keepRevisionForever)
					.setUseContentAsIndexableText(true)
					.setFields(fields)
					.setSupportsTeamDrives(true)
					.execute();
			
			getLogger().info("GDrive APIs - File uploaded");
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return uploadedFile;
		
	}
	
	@Override
	public void updateFileMetadata(String executionGoogleUser, String fileId, String name, Set<String> addParentIds, Set<String> removeParentIds, Map<String, String> properties, Date lastModifyDatetime) {
		
		try {
			
			File driveFileInfo = new File();
			
			if (name != null) { 
				driveFileInfo.setName(name);
			}
			
			if (properties != null && !properties.isEmpty()) {
				driveFileInfo.setProperties(properties);
			}
			
			if (lastModifyDatetime != null) {
				driveFileInfo.setModifiedTime(new DateTime(lastModifyDatetime.getTime()));
			}
			
			
			Update updateCommand = getDriveGoogleService(executionGoogleUser).files().update(fileId, driveFileInfo);
			
			if (addParentIds != null && !addParentIds.isEmpty()) {
				updateCommand.setAddParents(StringUtils.join(addParentIds, ','));
			}
			
			if (removeParentIds != null && !removeParentIds.isEmpty()) {
				updateCommand.setRemoveParents(StringUtils.join(removeParentIds, ','));
			}
			
			getLogger().info("GDrive APIs - Updating file metadata ...");
			
			driveFileInfo = updateCommand.setSupportsTeamDrives(true).execute();
			
			getLogger().info("GDrive APIs - File metadata updated.");
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	@Override
	public File copyFile(String executionGoogleUser, String fileId, String copyName, String mimeType, Set<String> copyParentIds, Map<String, String> properties, boolean writersCanShare, boolean keepRevisionForever, String fields) {
		
		File result = new File();
		
		try {
			
			File driveFileInfo = new File();
			
			driveFileInfo.setName(copyName);
			
			if (mimeType != null) {
				driveFileInfo.setMimeType(mimeType);
			}
			
			driveFileInfo.setParents(new ArrayList<String>(copyParentIds));
			driveFileInfo.setWritersCanShare(writersCanShare);
			
			if (properties != null && !properties.isEmpty()) {
				driveFileInfo.setProperties(properties);
			}
			
			getLogger().info("GDrive APIs - Copying file ...");
			
			result = getDriveGoogleService(executionGoogleUser).files().copy(fileId, driveFileInfo)
					.setKeepRevisionForever(keepRevisionForever)
					.setFields(fields)
					.execute();
			
			getLogger().info("GDrive APIs - File copied.");
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return result;
		
	}
		
	@Override
	public File getFileMetadata(String executionGoogleUser, String fileId, String fields) {
		
		File result = null;
		
		try {
			
			getLogger().info("GDrive APIs - Retrieve file metadata ...");
			
			result = getDriveGoogleService(executionGoogleUser).files().get(fileId)
					.setFields(fields)
					.execute();
			
			getLogger().info("GDrive APIs - File metadata retrieved.");
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return result;
		
	}
	
	@Override
	public FileList searchFiles(String executionGoogleUser, String fields, String orderBy, String query, Integer pageSize, String nextPageToken) {
		
		FileList result = null;
		
		try {
			
			getLogger().info("GDrive APIs - Searching file ...");
			
			com.google.api.services.drive.Drive.Files.List listCommand = getDriveGoogleService(executionGoogleUser).files().list()
					.setFields(fields)
					.setOrderBy(orderBy)
					.setQ(query);
			
			getLogger().info("GDrive APIs - File list retrieved.");
			
			if (pageSize != null) {
				listCommand.setPageSize(pageSize);
			}
			
			if (nextPageToken != null) {
				listCommand.setPageToken(nextPageToken);
			}
			
			result = listCommand.execute();
		
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return result;
		
	}
	
	@Override
	public String getFolderIdByNameAndParent(String executionGoogleUser, String parentId, String folderName) {
		
		String folderId = null;
		
		try {
			
			getLogger().info("GDrive APIs - Searching folders ...");
			
			FileList result = getDriveGoogleService(executionGoogleUser).files().list()
					.setQ("mimeType = 'application/vnd.google-apps.folder' and '" + parentId + "' in parents and name = '" + folderName + "' and trashed = false")
					.setFields("files(id)")
					.execute();
			
			getLogger().info("GDrive APIs - Folders list retrieved.");
			
			if (!result.getFiles().isEmpty()) {
				folderId = result.getFiles().get(0).getId();
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return folderId;
		
	}
	
	@Override
	public Set<String> getSubFoldersIds(String executionGoogleUser, String parentId) {
		
		Set<String> folderIds = new HashSet<String>();
		
		try {
			
			getLogger().info("GDrive APIs - Searching sub folders ...");
			
			FileList result = getDriveGoogleService(executionGoogleUser).files().list()
					.setQ("mimeType = 'application/vnd.google-apps.folder' and '" + parentId + "' in parents and trashed = false")
					.setFields("files(id)")
					.execute();
			
			getLogger().info("GDrive APIs - Sub folders list retrieved.");
			
			
			for (File file : result.getFiles()) {
				folderIds.add(file.getId());
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return folderIds;
		
	}
	
	@Override
	public Set<String> getParentFoldersIds(String executionGoogleUser, String fileId) {
		
		Set<String> folderIds = new HashSet<String>();
		
		try {
			
			getLogger().info("GDrive APIs - Retrieve parent folders ...");
			
			File file = getDriveGoogleService(executionGoogleUser).files().get(fileId)
					.setFields("parents")
					.execute();
			
			getLogger().info("GDrive APIs - Parent folders list retrieved.");
			
			for (String parentId : file.getParents()) {
				folderIds.add(parentId);
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return folderIds;
		
	}
	
	@Override
	public void deleteFileById(String executionGoogleUser, String fileId) {
		
		try {
			
			getLogger().info("GDrive APIs - Deleting file ...");
			
			getDriveGoogleService(executionGoogleUser).files().delete(fileId).execute();
			
			getLogger().info("GDrive APIs - File deleted.");
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
			
	}
	
	@Override
	public void trashFilesByIds(String executionGoogleUser, List<String> fileIds) {
		
		if(fileIds == null || fileIds.isEmpty())
			return;
		
		try {
			
			getLogger().info("GDrive APIs - Deleting files with batch call ...");
			
			 List<List<String>> partitions = ListUtils.partition(fileIds, 50);
			 
			 int i = 0;
			 for(List<String> partition : partitions) {
				 
				 BatchRequest batchRequest = getDriveGoogleService(executionGoogleUser).batch();

				 for(String elem : partition) {

					File driveFileInfo = new File();
					driveFileInfo.setTrashed(true);
					
					getDriveGoogleService(executionGoogleUser).files().update(elem, driveFileInfo).queue(batchRequest, new BatchFileGoogleCallback());

				 }
				 
					getLogger().info("Executing {}° batch request", (i+1)+"");
					batchRequest.execute();
					i++;
			 }
			
			getLogger().info("GDrive APIs - Files deleted.");
			
		} catch (IOException e) {
			getLogger().error("Error executing batch", e);
			throw new RuntimeException(e);
		}
		
	}
	
	@Override
	public String createFolder(String executionGoogleUser, String name, List<String> parentIds) {
		
		String folderId = null;
		
		try {
			
			File driveFileInfo = new File();
			
			driveFileInfo.setName(name);
			driveFileInfo.setMimeType("application/vnd.google-apps.folder");
			
			if (parentIds != null && !parentIds.isEmpty()) {
				driveFileInfo.setParents(parentIds);
			}
			
			getLogger().info("GDrive APIs - Creating folder ...");
			
			driveFileInfo = getDriveGoogleService(executionGoogleUser).files().create(driveFileInfo)
					.setSupportsTeamDrives(true)
					.setFields("id")
					.execute();
			
			getLogger().info("GDrive APIs - Folder created.");
			
			folderId = driveFileInfo.getId();
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return folderId;
		
	}
		
	@Override
	public PermissionList listFilePermissions(String executionGoogleUser, String fileId) {
		
		PermissionList drivePermissionsList = null;
		
		try {
			
			getLogger().info("GDrive APIs - Retrieve file permission ...");
			
			drivePermissionsList = getDriveGoogleService(executionGoogleUser).permissions().list(fileId).setFields("*").execute();
			
			getLogger().info("GDrive APIs - File permissions retrieved.");
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return drivePermissionsList;
		
	}
	
	@Override
	public void addFilePermissionToUser(String executionGoogleUser, String fileId, String userEMailAddress, String role) {
		
		try {
			
			Permission driveFilePermission = new Permission();
			
			driveFilePermission.setEmailAddress(userEMailAddress);
			driveFilePermission.setType("user");
			driveFilePermission.setRole(role);
			
			getLogger().info("GDrive APIs - Add file permission ...");
			
			getDriveGoogleService(executionGoogleUser).permissions().create(fileId, driveFilePermission).setSendNotificationEmail(false).execute();
			
			getLogger().info("GDrive APIs - Permission added to file.");
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	@Override
	public void addFilePermissionToGroup(String executionGoogleUser, String fileId, String groupEMailAddress, String role) {
		
		try {
			
			Permission driveFilePermission = new Permission();
			
			driveFilePermission.setEmailAddress(groupEMailAddress);
			driveFilePermission.setType("group");
			driveFilePermission.setRole(role);
			
			getLogger().info("GDrive APIs - Add file permission ...");
			
			getDriveGoogleService(executionGoogleUser).permissions().create(fileId, driveFilePermission).setSendNotificationEmail(false).execute();
			
			getLogger().info("GDrive APIs - Permission added to file.");
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	@Override
	public void updateFilePermission(String executionGoogleUser, String fileId, String permissionId, String role) {
		
		try {
			
			Permission driveFilePermission = new Permission();
			driveFilePermission.setRole(role);
			
			getLogger().info("GDrive APIs - Update file permission ...");
			
			getDriveGoogleService(executionGoogleUser).permissions().update(fileId, permissionId, driveFilePermission).execute();
			
			getLogger().info("GDrive APIs - File permission updated.");
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	@Override
	public void removeFilePermission(String executionGoogleUser, String fileId, String permissionId) {
		
		try {
			
			getLogger().info("GDrive APIs - Remove file permission ...");
			
			getDriveGoogleService(executionGoogleUser).permissions().delete(fileId, permissionId).execute();
			
			getLogger().info("GDrive APIs - File permission removed.");
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	@Override
	public void publishLastFileRevision(String executionGoogleUser, String fileId, Boolean publishAuto, Boolean publishedOutsideDomain) {
		
		Drive driveService = getDriveGoogleService(executionGoogleUser);
		
		try {
			
			Revision driveFileRevision = new Revision();
			driveFileRevision.setPublished(true);
			
			getLogger().info("GDrive APIs - Publishing last file revision ...");
			
			Revision publishResult = driveService.revisions().update(fileId, "head", driveFileRevision).execute();
			
			getLogger().info("GDrive APIs - Last file revision published.");
			
			if (publishAuto != null) {
				
				driveFileRevision = new Revision();
				driveFileRevision.setPublishAuto(publishAuto);
				
				getLogger().info("GDrive APIs - Update 'publishAuto' property on last file revision ...");
				
				driveService.revisions().update(fileId, publishResult.getId(), driveFileRevision).execute();
				
				getLogger().info("GDrive APIs - Last file revision updated property 'publishAuto'.");
				
			}
			
			if (publishedOutsideDomain != null) {
				
				driveFileRevision = new Revision();
				driveFileRevision.setPublishedOutsideDomain(publishedOutsideDomain);
				
				getLogger().info("GDrive APIs - Update 'publishedOutsideDomain' property on last file revision ...");
				
				/*
				 * Attenzione - Lasciare le due chiamate - workaround dovuto a un BUG delle Google Drive REST API !!!
				 */
				
				driveService.revisions().update(fileId, publishResult.getId(), driveFileRevision).execute();
				driveService.revisions().update(fileId, publishResult.getId(), driveFileRevision).execute();
				
				getLogger().info("GDrive APIs - Last file revision updated property 'publishedOutsideDomain'.");
				
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}


	@Override
	public String updateFileContentIntoANewFileRevision(String executionGoogleUser, String contentType,  InputStream fileData, String targetFileId,
			boolean keepRevisionForever) {
		String generatedRevisionId = null;
		
		try {
			
			getLogger().info("GDrive APIs - Copying content into a new file revision ...");
			
			File result = getDriveGoogleService(executionGoogleUser).files().update(targetFileId, null, new InputStreamContent(contentType, fileData))
					.setKeepRevisionForever(keepRevisionForever)
					.setUseContentAsIndexableText(true)
					.setSupportsTeamDrives(true)
					.setFields("headRevisionId")
					.execute();
			
			generatedRevisionId = result.getHeadRevisionId();
			
			getLogger().info("GDrive APIs - Content copied into into a new file revision.");
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return generatedRevisionId;
	}
		
	@Override
	public void addFilePermissionToUserBatch(String executionGoogleUser, String fileId, Map<String,String> userEMailAddressRolesMap) {
		if(userEMailAddressRolesMap == null || userEMailAddressRolesMap.isEmpty())
			return;
		
		try {
			getLogger().info("GDrive APIs - Add file permissions in batch mode...");
			
			BatchRequest batchRequest = getDriveGoogleService(executionGoogleUser).batch();
			
			for(String userEMailAddress : userEMailAddressRolesMap.keySet()) {
				Permission driveFilePermission = new Permission();
				
				driveFilePermission.setEmailAddress(userEMailAddress);
				driveFilePermission.setType("user");
				driveFilePermission.setRole(userEMailAddressRolesMap.get(userEMailAddress));
				
				getDriveGoogleService(executionGoogleUser).permissions().create(fileId, driveFilePermission).setSendNotificationEmail(false).queue(batchRequest, new BatchGoogleCallback());;
			}
			
			batchRequest.execute();
			
			getLogger().info("GDrive APIs - Permissions added to file in batch mode.");

	
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void updateFilePermissionBatch(String executionGoogleUser, String fileId, Map<String,String> permissionIdsRolesMap) {
		if(permissionIdsRolesMap == null || permissionIdsRolesMap.isEmpty())
			return;
		
		try {	
			getLogger().info("GDrive APIs - Update files permissions in batch mode...");
			
			BatchRequest batchRequest = getDriveGoogleService(executionGoogleUser).batch();
			
			for(String permissionId : permissionIdsRolesMap.keySet()) {
				Permission driveFilePermission = new Permission();
				driveFilePermission.setRole(permissionIdsRolesMap.get(permissionId));
				
				getDriveGoogleService(executionGoogleUser).permissions().update(fileId, permissionId, driveFilePermission).queue(batchRequest, new BatchGoogleCallback());
			}
			
			batchRequest.execute();
			
			getLogger().info("GDrive APIs - Permissions file updated in batch mode.");
									
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void removeFilePermissionBatch(String executionGoogleUser, String fileId, List<String> permissionIds) {
		if(permissionIds == null || permissionIds.isEmpty())
			return;
		
		try {
			getLogger().info("GDrive APIs - Remove file permissions in batch mode ...");
			
			BatchRequest batchRequest = getDriveGoogleService(executionGoogleUser).batch();
			
			for(String permissionId : permissionIds) {
				
				getDriveGoogleService(executionGoogleUser).permissions().delete(fileId, permissionId).queue(batchRequest, 
					new JsonBatchCallback<Void>() {

				      @Override
				      public void onSuccess(Void content, HttpHeaders responseHeaders) { }

				      @Override
				      public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
				    	  getLogger().error("GDrive APIs - Error Message in Batch delete on permission: " + e.getMessage());
				      }
				      
				 });
				
			}
			
			batchRequest.execute();
			
			getLogger().info("GDrive APIs - File permissions removed in batch mode.");
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String createFolderIfNotExistsByName(String executionGoogleUser, String parentId, String folderName) {

		String folderId = null;
		
		Drive driveService = getDriveGoogleService(executionGoogleUser);
		
		try {
			
			String query = "mimeType='application/vnd.google-apps.folder' and '" + parentId +  "' in parents and name = '" + folderName + "' and trashed = false";
			
			getLogger().info("GDrive APIs - Searching folder by name: {} ", query);
			
			FileList foundFolderList = driveService.files().list()
					.setQ(query)
					.setSpaces("drive")
					.setFields("files(id)")
					.execute();
			
			if (foundFolderList.getFiles().isEmpty()) {
				
				folderId = createFolderInternal(driveService, folderName, Arrays.asList(parentId));
				
			} else {
				getLogger().info("GDrive APIs - Folder {} found by name.", folderName);
				folderId = foundFolderList.getFiles().get(0).getId();
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return folderId;
	}
	
	@Override
	public String createFolderIfNotExistsByNameInTeamDrive(String executionGoogleUser, String folderName, String teamDriveId) {
		
		String folderId = null;
		
		Drive driveService = getDriveGoogleService(executionGoogleUser);
		
		try {
			
			String query = "mimeType='application/vnd.google-apps.folder' and name = '" + folderName + "' and trashed = false";
			
			getLogger().info("GDrive APIs - Searching folder by name: {} ", query);
			
			FileList foundFolderList = driveService.files().list()
					.setQ(query)
					.setIncludeTeamDriveItems(true)
					.setSupportsTeamDrives(true)
					.setTeamDriveId(teamDriveId)
					.setCorpora("teamDrive")
					.setSpaces("drive")
					.setFields("files(id)")
					.execute();
			
			if (foundFolderList.getFiles().isEmpty()) {
				
				folderId = createFolderInternal(driveService, folderName, Arrays.asList(teamDriveId));
				
			} else {
				getLogger().info("GDrive APIs - Folder {} found by name.", folderName);
				folderId = foundFolderList.getFiles().get(0).getId();
			}
		
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	
		return folderId;
		
	}
	
	private String createFolderInternal(Drive driveService, String name, List<String> parentIds) {
		String folderId = null;
		
		try {
			
			File driveFileInfo = new File();
			
			driveFileInfo.setName(name);
			driveFileInfo.setMimeType("application/vnd.google-apps.folder");
			
			if (parentIds != null && !parentIds.isEmpty()) {
				driveFileInfo.setParents(parentIds);
			}
			
			getLogger().info("GDrive APIs - Creating folder {}...", name);
			
			driveFileInfo = driveService.files().create(driveFileInfo)
					.setSupportsTeamDrives(true)
					.setFields("id")
					.execute();
			
			getLogger().info("GDrive APIs - Folder {} created.", name);
			
			folderId = driveFileInfo.getId();
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return folderId;
	}
		
	//Batch Callback object
	class BatchGoogleCallback extends JsonBatchCallback<Permission> {
		@Override
		  public void onSuccess(Permission permission, HttpHeaders responseHeaders) { }
		
		@Override
        public void onFailure(GoogleJsonError  e, HttpHeaders responseHeaders) {
			getLogger().error("GDrive APIs - Error Message in Batch operation on permission: " + e.getMessage());
        }
	}
	
	class BatchVoidGoogleCallback extends JsonBatchCallback<Void> {

		@Override
		public void onSuccess(Void t, HttpHeaders responseHeaders) throws IOException {
			
		}

		@Override
		public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) throws IOException {
			getLogger().error("GDrive APIs - Error Message in Batch operation");
		}
		
	}
	
	class BatchFileGoogleCallback extends JsonBatchCallback<File> {

		@Override
		public void onSuccess(File t, HttpHeaders responseHeaders) throws IOException {
						
		}

		@Override
		public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) throws IOException {
			getLogger().error("GDrive APIs - Error Message in Batch operation");
		}
		
	}
}
