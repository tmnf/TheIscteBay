package Downloads;

import java.io.IOException;

import PeerConnections.PeerConnected;
import SearchClasses.FileBlockRequestMessage;

public class RequestManager {

	public static final int MAX_REQUESTS = 5;

	private int currentRequests;

	public synchronized void addRequest(FileBlockRequestMessage request, PeerConnected peer) throws IOException {
		while (currentRequests == MAX_REQUESTS)
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		currentRequests++;
		peer.sendFilePartRequested(request);
	}

	public synchronized void closeRequest() {
		currentRequests--;
		notify();
	}

}
