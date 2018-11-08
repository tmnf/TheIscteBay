package Downloads;

import java.io.File;

import User.Client;

public class DownloadManager { // A implementar...

	private File[] fileDowloading;

	private Client client;

	private int numberOfPeersStillUploading;

	public DownloadManager(Client client, int peersUploading) {
		this.client = client;
		numberOfPeersStillUploading = peersUploading;
	}

	public synchronized void downloadWait() {
		numberOfPeersStillUploading--;

		while (numberOfPeersStillUploading != 0)
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

}
