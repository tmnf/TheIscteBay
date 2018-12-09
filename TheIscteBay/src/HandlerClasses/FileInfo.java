package HandlerClasses;

import java.util.ArrayList;

import SearchClasses.FileDetails;
import User.User;

public class FileInfo {

	private FileDetails fileInfo;
	private ArrayList<User> peersWithFile;

	public FileInfo(FileDetails fileInfo) {
		this.fileInfo = fileInfo;
		peersWithFile = new ArrayList<>();
	}

	public void addToPeers(User peer) {
		synchronized (peersWithFile) {
			peersWithFile.add(peer);
		}
	}

	public FileDetails getFileDetails() {
		return fileInfo;
	}

	public ArrayList<User> getPeersWithFile() {
		return peersWithFile;
	}

	@Override
	public boolean equals(Object obj) {
		FileInfo aux = (FileInfo) obj;
		if (aux.getFileDetails().equals(fileInfo))
			return true;
		return false;
	}

	@Override
	public String toString() {
		return fileInfo.getFileName() + ", " + fileInfo.getSize() + " bytes.";
	}

}
