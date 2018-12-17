package PeerConnections;

import java.io.IOException;
import java.net.Socket;

import Client.Client;
import Client.User;
import Connections.PeerConnection;
import Handlers.DownloadManager;
import Handlers.FileInfoHandler;
import InfoCarriers.FilePart;
import PeerObjects.FileBlockRequestMessage;
import PeerObjects.FileDetails;
import PeerObjects.WordSearchMessage;

public class ConnectionToPeer extends PeerConnection {

	// Managers
	private DownloadManager downManager;
	private FileInfoHandler fileInfoHandler;

	// User Info
	private User user;

	// Download
	private FileBlockRequestMessage currentDownload;

	public ConnectionToPeer(Socket so, Client client, User user) throws IOException {
		super(so, client);
		this.user = user;
	}

	@Override
	public void dealWith(Object aux) throws IOException {
		if (aux instanceof FileDetails[])
			handleFileInfoReceived((FileDetails[]) aux);
		else if (aux instanceof FilePart)
			handleFilePartReceived((FilePart) aux);
	}

	/* Income Handle Methods */

	/* Sends file information received to be processed */
	private void handleFileInfoReceived(FileDetails[] files) {
		fileInfoHandler.handleFileInfo(files, user);
		interrupt();
	}

	/*
	 * Sends file part received to be mounted and checks if there's more parts to
	 * request
	 */
	private void handleFilePartReceived(FilePart filePartReceived) {
		downManager.receiveFilePart(filePartReceived, user);
		currentDownload = null;
		sendRequestIfAvaible();
	}

	/* Outcome Methods */

	/* Sends a request about the existence of a file */
	public void sendFileInfoRequest(String keyWord, FileInfoHandler fileInfoHandler) {
		this.fileInfoHandler = fileInfoHandler;
		send(new WordSearchMessage(keyWord));
	}

	/* Sends a file part download request */
	public void sendFileRequest(DownloadManager downManager) {
		this.downManager = downManager;
		sendRequestIfAvaible();
	}

	/* Requests a file part download, if there's a part left */
	private void sendRequestIfAvaible() {
		currentDownload = mainClient.getRequest();
		if (currentDownload != null)
			send(currentDownload);
		else {
			interrupt();
			downManager.disconnectUser();
		}
	}

	// Getters
	public User getUser() {
		return user;
	}

	@Override
	protected void handleInterruption() {
		if (currentDownload != null)
			notifyBadUploader();
		interrupt();
	}

	// Notifies the system that this uploader had a problem
	private void notifyBadUploader() {
		mainClient.addRequest(currentDownload);
		downManager.notifyBadUploader(user);
	}
}
