package InfoCarriers;

import java.util.ArrayList;

import Client.User;
import PeerObjects.FileDetails;

public class FileInfo {

	// File Details
	private FileDetails fileInfo;

	// List of peers with this file
	private ArrayList<User> peersWithFile;

	public FileInfo(FileDetails fileInfo) {
		this.fileInfo = fileInfo;
		peersWithFile = new ArrayList<>();
	}

	/* Adds a peer to peersWithFile list */
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
