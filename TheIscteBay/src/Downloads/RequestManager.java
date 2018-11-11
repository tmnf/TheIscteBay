package Downloads;

import java.util.ArrayList;

import Connections.PeerConnection;
import SearchClasses.FileDetails;
import User.Client;

public class RequestManager { // A implementar..

	private ArrayList<FileDetails> filesAvaible;
	private ArrayList<PeerConnection> usersProviding;

	private Client client;

	private int numberOfPeersSendingInfo;

	public RequestManager(Client client, int peersSendingInfo) {
		this.client = client;
		numberOfPeersSendingInfo = peersSendingInfo;

		filesAvaible = new ArrayList<>();
		usersProviding = new ArrayList<>();
	}

	public synchronized void manageRequestedFiles(FileDetails[] files, PeerConnection peer) {
		numberOfPeersSendingInfo--;

		if (files.length != 0) {
			for (int i = 0; i != files.length; i++)
				if (!filesAvaible.contains(files[i]))
					filesAvaible.add(files[i]);
			usersProviding.add(peer);
		} else
			peer.interrupt();

		while (numberOfPeersSendingInfo != 0)
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		client.showOnGuiList(filesAvaible.toArray(new FileDetails[filesAvaible.size()]));
	}

}
