package PeerConnections;

import java.io.IOException;
import java.net.Socket;

import Connections.PeerConnection;
import Downloads.RequestManager;
import SearchClasses.FileBlockRequestMessage;
import SearchClasses.UploadedPart;
import SearchClasses.WordSearchMessage;
import User.Client;

public class PeerConnected extends PeerConnection {

	private RequestManager requestManager;

	public PeerConnected(Socket so, Client client, RequestManager requestManager) throws IOException {
		super(so, client);

		this.requestManager = requestManager;
	}

	@Override
	public void dealWith(Object aux) throws IOException {
		if (aux instanceof WordSearchMessage) // Enviar lista de ficheiros com o nome desejado
			sendFilesInFolder(aux.toString());
		else if (aux instanceof FileBlockRequestMessage) // Enviar parte do ficheiro desejado
			requestManager.addRequest((FileBlockRequestMessage) aux, this);
	}

	public void sendFilesInFolder(String fileName) throws IOException {
		send(mainClient.getFilesWithName(fileName));
	}

	public void sendFilePartRequested(FileBlockRequestMessage partToDowload) throws IOException {
		send(new UploadedPart(mainClient.getFilePart(partToDowload), partToDowload));
		requestManager.closeRequest(); // Após enviar ficheiro desocupa a fila
	}

}
