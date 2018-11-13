package Connections;

import java.io.IOException;
import java.net.Socket;

import SearchClasses.FileBlockRequestMessage;
import SearchClasses.UploadedPart;
import SearchClasses.WordSearchMessage;
import User.Client;

public class PeerConnected extends PeerConnection {

	public PeerConnected(Socket so, Client client) throws IOException {
		super(so, client);
	}

	@Override
	public void dealWith(Object aux) throws IOException {
		if (aux instanceof WordSearchMessage) // Enviar lista de ficheiros com o nome desejado
			sendFilesInFolder(aux.toString());
		else if (aux instanceof FileBlockRequestMessage) // Enviar parte do ficheiro desejado
			sendFilePartRequested((FileBlockRequestMessage) aux);
	}

	private void sendFilesInFolder(String fileName) throws IOException {
		send(mainClient.getFilesWithName(fileName));
	}

	private void sendFilePartRequested(FileBlockRequestMessage partToDowload) throws IOException {
		send(new UploadedPart(mainClient.getFilePart(partToDowload), partToDowload));
	}

}
