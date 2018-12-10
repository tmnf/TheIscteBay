package InfoCarriers;

import PeerConnections.PeerConnected;
import PeerObjects.FileBlockRequestMessage;

public class RequestInfo {

	// File Info
	private FileBlockRequestMessage fileInfo;

	// Peer requesting file
	private PeerConnected peerRequesting;

	public RequestInfo(FileBlockRequestMessage fileInfo, PeerConnected peerRequesting) {
		this.fileInfo = fileInfo;
		this.peerRequesting = peerRequesting;
	}

	public FileBlockRequestMessage getFileInfo() {
		return fileInfo;
	}

	public PeerConnected getPeer() {
		return peerRequesting;
	}

}
