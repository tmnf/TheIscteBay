package PeerConnections;

import java.io.IOException;
import java.net.Socket;

import Connections.PeerConnection;
import Downloads.DownloadManager;
import Downloads.FileInfoHandler;
import HandlerClasses.UploadedPart;
import SearchClasses.FileBlockRequestMessage;
import SearchClasses.FileDetails;
import SearchClasses.WordSearchMessage;
import User.Client;
import User.User;

public class ConnectionToPeer extends PeerConnection {

	// Managers
	private DownloadManager downManager;
	private FileInfoHandler fileInfoHandler;

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

	// Income Handle Methods

	private void handleFileInfoReceived(FileDetails[] files) {
		fileInfoHandler.handleFileInfo(files, user);
	}

	private void handleFilePartReceived(UploadedPart filePartReceived) {
		downManager.receiveFilePart(filePartReceived, user);
	}

	// Outcome Methods

	public void sendFileInfoRequest(String keyWord, FileInfoHandler fileInfoHandler) {
		this.fileInfoHandler = fileInfoHandler;
		send(new WordSearchMessage(keyWord));
	}

	public void sendFilePartRequest(String name, int startIndex, int size, DownloadManager downManager) {
		this.downManager = downManager;
		send(new FileBlockRequestMessage(name, startIndex, size));
	}

	// Getters
	public User getUser() {
		return user;
	}

}
