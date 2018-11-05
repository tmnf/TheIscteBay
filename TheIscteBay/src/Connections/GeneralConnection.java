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
	public abstract void run();

}
