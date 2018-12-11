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
		if (aux instanceof FileDetails[]) // Recebe a lista de ficheiros com a palavra-chave desejada
			handleFileInfoReceived((FileDetails[]) aux);
		else if (aux instanceof FilePart) // Recebe parte do ficheiro pedido
			handleFilePartReceived((FilePart) aux);
	}

	/* Income Handle Methods */

	private void handleFileInfoReceived(FileDetails[] files) {
		fileInfoHandler.handleFileInfo(files, user);
		interrupt();
	}

	private void handleFilePartReceived(FilePart filePartReceived) {
		downManager.receiveFilePart(filePartReceived, user);
		currentDownload = null;
		sendRequestIfAvaible();
	}

	/* Outcome Methods */

	public void sendFileInfoRequest(String keyWord, FileInfoHandler fileInfoHandler) {
		this.fileInfoHandler = fileInfoHandler;
		send(new WordSearchMessage(keyWord));
	}

	public void sendFileRequest(DownloadManager downManager) {
		this.downManager = downManager;
		sendRequestIfAvaible();
	}

	private void sendRequestIfAvaible() {
		currentDownload = mainClient.getRequest();
		if (currentDownload != null)
			send(currentDownload);
		else
			interrupt();
	}

	// Getters
	public User getUser() {
		return user;
	}

	@Override
	protected void handleInterruption() {
		if (currentDownload != null)
			System.out.println("Avisar o cliente");
		interrupt();
	}
}
