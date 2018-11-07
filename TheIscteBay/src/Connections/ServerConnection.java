package Connections;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

import Users.Client;
import Users.User;

public class ServerConnection extends GeneralConnection {

	private LinkedList<User> tempList;

	public ServerConnection(Socket so, Client client) throws IOException {
		super(so, client);
		tempList = new LinkedList<>();
		registerOnServer();
	}

	public void dealWith(Object aux) throws IOException {
		String[] temp = ((String) aux).split(" ");

		if (temp[0].equals("CLT")) {
			while (!temp[0].equals("END")) {
				tempList.add(new User(temp[1], Integer.parseInt(temp[2]), Integer.parseInt(temp[3])));
				try {
					temp = ((String) in.readObject()).split(" ");
				} catch (Exception e) {
					System.err.println("Falha ao receber mensagem");
					System.exit(1);
				}
			}
			mainClient.refreshPeersOnline(tempList);
		}
	}

	public void registerOnServer() {
		try {
			String insc = "INSC " + InetAddress.getLocalHost().getHostAddress() + " " + mainClient.getClientPort();
			send(insc);
		} catch (UnknownHostException e) {
			System.err.println("Erro ao registar no servidor");
			System.exit(1);
		}
	}

	public void requestClients() {
		send("CLT");
	}

}
