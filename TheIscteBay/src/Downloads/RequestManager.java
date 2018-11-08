package Downloads;

import java.util.HashMap;

import SearchClasses.FileDetails;
import User.Client;
import User.User;

public class RequestManager { // A implementar..

	private HashMap<User, FileDetails[]> filesAvaible;

	private Client client;

	private int numberOfPeersSendingInfo;

	public RequestManager(Client client, int peersSendingInfo) {
		this.client = client;
		numberOfPeersSendingInfo = peersSendingInfo;

		filesAvaible = new HashMap<>();
	}

	public synchronized void manageRequestedFiles(User user, FileDetails[] files) {
		numberOfPeersSendingInfo--;

		if (files.length != 0) {
			filesAvaible.put(user, files);
		}

		while (numberOfPeersSendingInfo != 0)
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

	}

}
