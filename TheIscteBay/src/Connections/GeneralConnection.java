package Connections;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import User.Client;

public abstract class GeneralConnection extends Thread {

	protected Client mainClient;

	protected Socket so;

	// Peer Channels
	protected ObjectInputStream in;
	protected ObjectOutputStream out;

	// Server Channels
	protected BufferedReader inServer;
	protected PrintWriter outServer;

	public GeneralConnection(Socket so, Client client) throws IOException {
		this.mainClient = client;
		this.so = so;
		startChannels();
	}

	private void startChannels() throws IOException {
		if (this instanceof PeerConnection) {
			out = new ObjectOutputStream(so.getOutputStream());
			in = new ObjectInputStream(so.getInputStream());
		} else if (this instanceof ServerConnection) {
			outServer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(so.getOutputStream())), true);
			inServer = new BufferedReader(new InputStreamReader(so.getInputStream()));
		}

	}

	@Override
	public void run() {
		while (!interrupted()) {
			try {
				if (this instanceof PeerConnection) {
					Object aux = in.readObject();
					dealWith(aux);

					// Terminar a conexão com o par após operações desejadas
					mainClient.disconectPeer((PeerConnection) this);
					interrupt();
				} else {
					String aux = inServer.readLine();
					dealWith(aux);
				}
			} catch (Exception e) {
				return;
			}
		}
		System.out.println("Conexão terminada. Porta: " + so.getPort());
	}

	public void send(Object ob) {
		try {
			if (this instanceof PeerConnection)
				out.writeObject(ob);
			else
				outServer.println((String) ob);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public abstract void dealWith(Object aux) throws IOException;
}
