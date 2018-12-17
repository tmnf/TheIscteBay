package Handlers;

import java.util.ArrayList;

import Client.Client;
import Client.User;
import InfoCarriers.FileInfo;
import PeerObjects.FileDetails;

public class FileInfoHandler extends Thread {

	// Client
	private Client client;

	// Peers still responding to request
	private int senders;

	// Files with correspondent name available
	private ArrayList<FileInfo> filesAvailable;

	public FileInfoHandler(int senders, Client client) {
		this.senders = senders;
		this.client = client;

		filesAvailable = new ArrayList<>();
	}

	public synchronized void run() {
		while (senders > 0)
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		FileInfo[] filesToShow = filesAvailable.toArray(new FileInfo[filesAvailable.size()]);
		client.showOnGuiList(filesToShow);
	}

	/*
	 * Gets file information of one user and synchronizes it with other users'
	 * information
	 */
	public synchronized void handleFileInfo(FileDetails[] files, User user) {
		senders--;

		for (FileDetails x : files) {
			FileInfo aux = new FileInfo(x);
			if (filesAvailable.contains(aux))
				addPeerTo(aux, user);
			else {
				aux.addToPeers(user);
				filesAvailable.add(aux);
			}
		}

		notify();
	}

	/* Adds a peer to the peers with certain file list */
	private void addPeerTo(FileInfo aux, User peer) {
		for (FileInfo x : filesAvailable)
			if (x.equals(aux))
				x.addToPeers(peer);
	}

}
