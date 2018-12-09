package HandlerClasses;

import PeerConnections.PeerConnected;
import SearchClasses.FileBlockRequestMessage;

public class RequestInfo {

	private FileBlockRequestMessage fileInfo;
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
