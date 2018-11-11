package Connections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Downloads.DownloadManager;
import Downloads.RequestManager;
import SearchClasses.FileBlockRequestMessage;
import SearchClasses.FileDetails;
import SearchClasses.UploadedPart;
import SearchClasses.WordSearchMessage;
import User.Client;

public class PeerConnection extends GeneralConnection {

	// Peer Channels
	private ObjectInputStream in;
	private ObjectOutputStream out;

	// RequestManager
	private RequestManager requestManager;
	private DownloadManager downloadManager;

	public PeerConnection(Socket so, Client client, RequestManager man) throws IOException {
		super(so, client);
		requestManager = man;
	}

	@Override
	public void startChannels() throws IOException {
		out = new ObjectOutputStream(so.getOutputStream());
		in = new ObjectInputStream(so.getInputStream());
	}

	@Override
	public void run() {
		while (!interrupted()) {
			try {
				Object aux = in.readObject();
				dealWith(aux);
			} catch (Exception e) {
				return;
			}
		}
		mainClient.disconectPeer((PeerConnection) this);
		System.out.println("Conexão terminada. Porta: " + so.getPort());
	}

	@Override
	public void dealWith(Object aux) throws IOException {
		if (aux instanceof WordSearchMessage)
			out.writeObject(mainClient.getFilesWithName(aux.toString())); // Pedido de Ficheiros Recebido
		else if (aux instanceof FileDetails[])
			requestManager.manageRequestedFiles((FileDetails[]) aux, this); // Lista de Ficheiros Recebidos
		else if (aux instanceof FileBlockRequestMessage) {
			FileBlockRequestMessage temp = (FileBlockRequestMessage) aux;
			out.writeObject(new UploadedPart(mainClient.getFilePart(temp), temp)); // Envia o pedido do ficheiro
		} else if (aux instanceof UploadedPart) {
			System.out.println("Tentou");
			try {
				UploadedPart temp = (UploadedPart) aux;
				downloadManager = mainClient.down;
				downloadManager.downloadWait(temp.getFilePart(), temp.getPartInfo(), this);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Conseguiu");
		} // Recebe parte do ficheiro - RESOLVER
	}

	@Override
	public void send(Object ob) {
		try {
			out.writeObject(ob);
		} catch (Exception e) {
			System.out.println("Erro ao enviar objeto");
		}
	}

}
