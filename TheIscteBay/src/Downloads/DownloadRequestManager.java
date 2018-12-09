package Downloads;

import java.io.IOException;

import HandlerClasses.RequestInfo;
import PeerConnections.PeerConnected;

public class DownloadRequestManager {

	public static final int MAX_REQUESTS = 5;

	private int currentRequests;

	public synchronized void addRequest(RequestInfo requestInfo) throws IOException {
		while (currentRequests >= MAX_REQUESTS)
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		currentRequests++;

		PeerConnected peerRequesting = requestInfo.getPeer();
		peerRequesting.sendFilePartRequested(requestInfo.getFileInfo());
	}

	public synchronized void closeRequest() {
		currentRequests--;
		notify();
	}

}
