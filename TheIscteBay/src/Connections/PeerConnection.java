package Connections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import User.Client;

public abstract class PeerConnection extends GeneralConnection {

	// Peer Channels
	protected ObjectInputStream in;
	protected ObjectOutputStream out;

	public PeerConnection(Socket so, Client client) throws IOException {
		super(so, client);
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
	protected void send(Object ob) {
		try {
			out.writeObject(ob);
		} catch (Exception e) {
			System.out.println("Erro ao enviar objeto");
		}
	}
}
