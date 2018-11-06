package Connections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Users.Client;

public abstract class GeneralConnection extends Thread {

	protected Client mainClient;

	// Channels
	protected ObjectInputStream in;
	protected ObjectOutputStream out;

	public GeneralConnection(Socket so, Client client) throws IOException {
		this.mainClient = client;

		out = new ObjectOutputStream(so.getOutputStream());
		in = new ObjectInputStream(so.getInputStream());
	}

	@Override
	public void run() {
		while (!interrupted()) {
			try {
				Object aux = in.readObject();
				dealWith(aux);

				if (this instanceof PeerConnection) {
					mainClient.disconectPeer((PeerConnection) this);
					interrupt();
				}
			} catch (Exception e) {
				return;
			}
		}
		System.out.println("Conexão terminada...");
	}

	public void send(Object ob) {
		try {
			out.writeObject(ob);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public abstract void dealWith(Object aux) throws IOException;
}
