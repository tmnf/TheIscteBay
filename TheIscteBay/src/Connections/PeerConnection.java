package Connections;

import java.io.IOException;
import java.net.Socket;

import Users.Client;

public class PeerConnection extends GeneralConnection {

	public PeerConnection(Socket so, Client client) throws IOException {
		super(so, client);
	}

	@Override
	public void run() {

	}

}
