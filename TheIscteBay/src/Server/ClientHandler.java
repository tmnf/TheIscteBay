package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import Client.User;

public class ClientHandler extends Thread {

	// Directory
	private Server server;

	// Directory Channels
	private BufferedReader in;
	private PrintWriter out;

	// Connection ID
	private int ID;

	public ClientHandler(Socket so, Server server, int ID) {
		this.server = server;
		this.ID = ID;

		initChannels(so);
	}

	/* Starts communication channels */
	private void initChannels(Socket so) {
		try {
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(so.getOutputStream())), true);
			in = new BufferedReader(new InputStreamReader(so.getInputStream()));
		} catch (IOException e) {
			System.err.println("Erro na criação dos canais da Thread do cliente");
			System.exit(1);
		}
	}

	public void run() {
		while (!interrupted()) {
			try {
				String text = in.readLine();
				String[] aux = text.split(" ");

				if (aux[0].equals("INSC"))
					server.registerUser(aux[1], Integer.parseInt(aux[2]), ID);
				else if (aux[0].equals("CLT"))
					sendUsersOnline();

			} catch (Exception e) {
				server.disconectUser(ID);
				return;
			}
		}
	}

	/* Sends users currently registered on directory */
	private void sendUsersOnline() {
		for (User x : server.getUsersOnline())
			out.println("CLT " + x.toString());
		out.println("END");
	}
}
