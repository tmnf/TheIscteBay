package Connections;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import SearchClasses.FileDetails;
import SearchClasses.WordSearchMessage;
import Users.Client;

public class PeerConnection extends GeneralConnection {

	public PeerConnection(Socket so, Client client) throws IOException {
		super(so, client);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void dealWith(Object aux) throws IOException {
		if (aux instanceof WordSearchMessage)
			out.writeObject(mainClient.getFilesWithName(aux.toString())); // Pedido de Ficheiros Recebido
		else if (aux instanceof ArrayList<?>)
			mainClient.showOnGuiList((ArrayList<FileDetails>) aux); // Lista de Ficheiros Recebidos
	}
}
