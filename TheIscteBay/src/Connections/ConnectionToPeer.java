package Connections;

import java.io.IOException;
import java.net.Socket;

import SearchClasses.FileBlockRequestMessage;
import SearchClasses.FileDetails;
import SearchClasses.UploadedPart;
import SearchClasses.WordSearchMessage;
import User.Client;

public class ConnectionToPeer extends PeerConnection {

	public ConnectionToPeer(Socket so, Client client) throws IOException {
		super(so, client);
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
//		requestManager.manageRequestedFiles(files, this); Arranjar isto
	}

	private void handleFilePartReceived(UploadedPart filePartReceived) {
		try {
//			downloadManager.downloadWait(filePartReceived.getFilePart(), filePartReceived.getPartInfo(), this);
		} catch (Exception e) {
			System.err.println("Erro a receber a parte do ficheiro");
		}
	}

	// Outcome Methods

	public void sendFileInfoRequest(String keyWord) {
		send(new WordSearchMessage(keyWord));
	}

	public void sendFilePartRequest(String name, int startIndex, int size) {
		send(new FileBlockRequestMessage(name, startIndex, size));
	}

}
