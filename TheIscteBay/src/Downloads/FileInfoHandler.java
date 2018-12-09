package Downloads;

import java.util.ArrayList;

import HandlerClasses.FileInfo;
import SearchClasses.FileDetails;
import User.Client;
import User.User;

public class FileInfoHandler extends Thread {

	private Client client;

	private int senders;

	private ArrayList<FileInfo> filesAvaible;

	public FileInfoHandler(int senders, Client client) {
		this.senders = senders;
		this.client = client;

		filesAvaible = new ArrayList<>();
	}

	public synchronized void run() {
		while (senders > 0)
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		FileInfo[] filesToShow = filesAvaible.toArray(new FileInfo[filesAvaible.size()]);
		client.showOnGuiList(filesToShow);
	}

	public synchronized void handleFileInfo(FileDetails[] files, User user) {
		senders--;

		for (FileDetails x : files) {
			FileInfo aux = new FileInfo(x);
			if (filesAvaible.contains(aux))
				addPeerTo(aux, user);
			else {
				aux.addToPeers(user);
				filesAvaible.add(aux);
			}
		}

		notify();

	}

	private void addPeerTo(FileInfo aux, User peer) {
		for (FileInfo x : filesAvaible)
			if (x.equals(aux))
				x.addToPeers(peer);
	}

}
