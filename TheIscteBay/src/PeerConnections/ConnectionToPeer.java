package PeerConnections;

import java.io.IOException;
import java.net.Socket;

import Client.Client;
import Client.User;
import Connections.PeerConnection;
import Handlers.DownloadManager;
import Handlers.FileInfoHandler;
import InfoCarriers.UploadedPart;
import PeerObjects.FileBlockRequestMessage;
import PeerObjects.FileDetails;
import PeerObjects.WordSearchMessage;

public class ConnectionToPeer extends PeerConnection {

	// Managers
	private DownloadManager downManager;
	private FileInfoHandler fileInfoHandler;

	// User Info
	private User user;

	public ConnectionToPeer(Socket so, Client client, User user) throws IOException {
		super(so, client);
		this.user = user;
	}

	@Override
	public void dealWith(Object aux) throws IOException {
		if (aux instanceof FileDetails[]) // Recebe a lista de ficheiros com a palavra-chave desejada
			handleFileInfoReceived((FileDetails[]) aux);
		else if (aux instanceof UploadedPart) // Recebe parte do ficheiro pedido
			handleFilePartReceived((UploadedPart) aux);
	}

	/* Income Handle Methods */

	private void handleFileInfoReceived(FileDetails[] files) {
		fileInfoHandler.handleFileInfo(files, user);
		interrupt();
	}

	private void handleFilePartReceived(UploadedPart filePartReceived) {
		downManager.receiveFilePart(filePartReceived, user);
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
		FileBlockRequestMessage aux = mainClient.getRequest();
		if (aux != null)
			send(aux);
		else
			interrupt();
	}

	// Getters
	public User getUser() {
		return user;
	}
}
