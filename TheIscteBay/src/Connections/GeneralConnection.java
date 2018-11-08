package Connections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import User.Client;

public abstract class GeneralConnection extends Thread {

	protected Client mainClient;

	protected Socket so;

	// Channels
	protected ObjectInputStream in;
	protected ObjectOutputStream out;

	public GeneralConnection(Socket so, Client client) throws IOException {
		this.mainClient = client;
		this.so = so;
		out = new ObjectOutputStream(so.getOutputStream());
		in = new ObjectInputStream(so.getInputStream());
	}

	@Override
	public void run() {
		while (!interrupted()) {
			try {
				Object aux = in.readObject();
				dealWith(aux);

				if (this instanceof PeerConnection) { // Terminar a conex�o com o par ap�s opera��es desejadas
					mainClient.disconectPeer((PeerConnection) this);
					interrupt();
				}
			} catch (Exception e) {
				return;
			}
		}
		System.out.println("Conex�o terminada. Porta: " + so.getPort());
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
