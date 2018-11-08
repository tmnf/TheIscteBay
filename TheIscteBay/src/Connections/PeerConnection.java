package Connections;

import java.io.IOException;
import java.net.Socket;

import SearchClasses.FileDetails;
import SearchClasses.WordSearchMessage;
import User.Client;

public class PeerConnection extends GeneralConnection {

	public PeerConnection(Socket so, Client client) throws IOException {
		super(so, client);
	}

	@Override
	public void dealWith(Object aux) throws IOException {
		if (aux instanceof WordSearchMessage)
			out.writeObject(mainClient.getFilesWithName(aux.toString())); // Pedido de Ficheiros Recebido
		else if (aux instanceof FileDetails[])
			mainClient.showOnGuiList((FileDetails[]) aux); // Lista de Ficheiros Recebidos
	}
}
