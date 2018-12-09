package PeerConnections;

import java.io.IOException;
import java.net.Socket;

import Connections.PeerConnection;
import Downloads.DownloadRequestManager;
import HandlerClasses.RequestInfo;
import HandlerClasses.UploadedPart;
import SearchClasses.FileBlockRequestMessage;
import SearchClasses.FileDetails;
import SearchClasses.WordSearchMessage;
import User.Client;
import Utils.Utils;

public class PeerConnected extends PeerConnection {

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

	public void sendFilesInFolder(String fileName) throws IOException {
		FileDetails[] file = Utils.getFilesWithName(mainClient.getPath(), fileName);
		send(file);
	}

	public void sendFilePartRequested(FileBlockRequestMessage partToDowload) throws IOException {
		byte[] filePart = Utils.getFilePart(partToDowload, mainClient.getPath());

		send(new UploadedPart(filePart, partToDowload));
		requestManager.closeRequest(); // Após enviar ficheiro desocupa a fila
	}

}
