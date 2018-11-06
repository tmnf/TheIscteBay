package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Users.User;

public class ClientHandler extends Thread {

	private Server server;

	private ObjectInputStream in;
	private ObjectOutputStream out;

	private int ID;

	public ClientHandler(Socket so, Server server, int ID) {
		this.server = server;
		this.ID = ID;

		try {
			out = new ObjectOutputStream(so.getOutputStream());
			in = new ObjectInputStream(so.getInputStream());
		} catch (IOException e) {
			System.err.println("Erro na criação da Thread do cliente");
		}
	}

	public void run() {
		while (!interrupted()) {
			try {
				String text = (String) in.readObject();
				String[] aux = text.split(" ");

				if (aux[0].equals("INSC"))
					server.registerUser(aux[1], Integer.parseInt(aux[2]), ID);
				else {
					if (aux[0].equals("CLT")) {
						for (User x : server.getUsersOnline())
							out.writeObject("CLT " + x.toString());
						out.writeObject("END");
					}
				}

			} catch (Exception e) {
				server.disconectUser(ID);
				return;
			}
		}
	}

}
