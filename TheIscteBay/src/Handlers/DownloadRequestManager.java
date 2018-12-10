package Handlers;

import java.io.IOException;

import InfoCarriers.RequestInfo;
import PeerConnections.PeerConnected;

public class DownloadRequestManager {

	// Constants
	public static final int MAX_REQUESTS = 5;

	// Current requests in line
	private int currentRequests;

	/* If possible allows client to deal with the request */
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

	/* Notifies that a request as been concluded */
	public synchronized void closeRequest() {
		currentRequests--;
		notify();
	}

}
