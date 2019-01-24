package GoogleAPI;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.PermissionList;

public interface DriveGoogleApi {

	public File uploadNewFile(String executionGoogleUser, InputStream fileData, String contentType, String fileName, List<String> folderIds, boolean keepRevisionForever, String fields);
	
	public void updateFileMetadata(String executionGoogleUser, String fileId, String name, Set<String> addParentIds, Set<String> removeParentIds, Map<String, String> properties, Date lastModifyDatetime);
	
	public File copyFile(String executionGoogleUser, String fileId, String copyName, String mimeType, Set<String> copyParentIds, Map<String, String> properties, boolean writersCanShare, boolean keepRevisionForever, String fields);
		
	public File getFileMetadata(String executionGoogleUser, String fileId, String fields);
	
	public FileList searchFiles(String executionGoogleUser, String fields, String orderBy, String query, Integer pageSize, String nextPageToken);
	
	public String getFolderIdByNameAndParent(String executionGoogleUser, String parentId, String folderName);
	
	public Set<String> getSubFoldersIds(String executionGoogleUser, String parentId);
	
	public Set<String> getParentFoldersIds(String executionGoogleUser, String fileId);
	
	public void deleteFileById(String executionGoogleUser, String fileId);
	
	public void trashFilesByIds(String executionGoogleUser, List<String> fileIds);
	
	public String createFolder(String executionGoogleUser, String name, List<String> parentIds);
		
	public PermissionList listFilePermissions(String executionGoogleUser, String fileId);
	
	public void addFilePermissionToUser(String executionGoogleUser, String fileId, String userEMailAddress, String role);
	
	public void addFilePermissionToGroup(String executionGoogleUser, String fileId, String groupEMailAddress, String role);
	
	public void updateFilePermission(String executionGoogleUser, String fileId, String permissionId, String role);
	
	public void removeFilePermission(String executionGoogleUser, String fileId, String permissionId);
	
	public void publishLastFileRevision(String executionGoogleUser, String fileId, Boolean publishAuto, Boolean publishedOutsideDomain);
	
	public String updateFileContentIntoANewFileRevision(String executionGoogleUser, String contentType, InputStream fileData, String targetFileId, boolean keepRevisionForever);
	
	public void addFilePermissionToUserBatch(String executionGoogleUser, String fileId, Map<String,String> userEMailAddressRolesMap);

	public void updateFilePermissionBatch(String executionGoogleUser, String fileId, Map<String,String> permissionIdsRolesMap);
	
	public void removeFilePermissionBatch(String executionGoogleUser, String fileId, List<String> permissionIds);
	
	public String createFolderIfNotExistsByName(String executionGoogleUser, String parentId, String folderName);
	
	public String createFolderIfNotExistsByNameInTeamDrive(String executionGoogleUser, String folderName, String teamDriveId);
	
}
