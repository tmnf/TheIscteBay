package PeerConnections;

import java.io.IOException;
import java.net.Socket;

import Client.Client;
import Connections.PeerConnection;
import Handlers.DownloadRequestManager;
import InfoCarriers.RequestInfo;
import InfoCarriers.FilePart;
import PeerObjects.FileBlockRequestMessage;
import PeerObjects.FileDetails;
import PeerObjects.WordSearchMessage;
import Utils.Utils;

public class PeerConnected extends PeerConnection {

	// Request Manger
	private DownloadRequestManager requestManager;

	public PeerConnected(Socket so, Client client, DownloadRequestManager requestManager) throws IOException {
		super(so, client);

		this.requestManager = requestManager;
	}

	@Override
	public void dealWith(Object aux) throws IOException {
		if (aux instanceof WordSearchMessage) // Enviar lista de ficheiros com o nome desejado
			sendFilesInFolder(((WordSearchMessage) aux).toString());
		else if (aux instanceof FileBlockRequestMessage) // Enviar parte do ficheiro desejado
			requestManager.addRequest(new RequestInfo((FileBlockRequestMessage) aux, this));
	}

	/* Outcome Methods */

	public void sendFilesInFolder(String fileName) throws IOException {
		FileDetails[] file = Utils.getFilesWithName(mainClient.getPath(), fileName);
		send(file);
	}

	public void sendFilePartRequested(FileBlockRequestMessage partToDowload) throws IOException {
		byte[] filePart = Utils.getFilePart(partToDowload, mainClient.getPath());

		send(new FilePart(filePart, partToDowload));
		requestManager.closeRequest(); // Após enviar ficheiro desocupa a fila
	}

	@Override
	protected void handleInterruption() {
		interrupt();
	}

}
