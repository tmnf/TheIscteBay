package Connections;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;

import Client.Client;
import Client.User;

public class ServerConnection extends GeneralConnection {

	// Server Channels
	private BufferedReader inServer;
	private PrintWriter outServer;

	// Lists
	private LinkedList<User> currentOnline;

	public ServerConnection(Socket so, Client client) throws IOException {
		super(so, client);
		currentOnline = new LinkedList<>();
		registerOnServer();
	}

	@Override
	public void startChannels() throws IOException {
		outServer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(so.getOutputStream())), true);
		inServer = new BufferedReader(new InputStreamReader(so.getInputStream()));
	}

	@Override
	public void run() {
		while (!interrupted()) {
			try {
				String aux = inServer.readLine();
				dealWith(aux);
			} catch (Exception e) {
				return;
			}
		}
	}

	@Override
	public void dealWith(Object aux) throws IOException {
		String[] temp = ((String) aux).split(" ");

		if (temp[0].equals("CLT"))
			receiveActiveUsers(temp);

	}

	/* Receives users currently online */
	private void receiveActiveUsers(String[] temp) {
		currentOnline.clear();
		while (!temp[0].equals("END")) {
			User aux = new User(temp[1], Integer.parseInt(temp[2]), Integer.parseInt(temp[3]));
			System.out.println(aux); // So para teste em aula
			currentOnline.add(aux);
			try {
				temp = inServer.readLine().split(" ");
			} catch (Exception e) {
				System.err.println("Falha ao receber mensagem");
			}
		}
		mainClient.refreshPeersOnline(currentOnline);
	}

	/* Registers client on Directory */
	public void registerOnServer() {
		String insc = "INSC " + mainClient.getClientAddress() + " " + mainClient.getClientPort();
		send(insc);
	}

	/* Sends a request message to directory */
	public void requestClients() {
		send("CLT");
	}

	@Override
	public void send(Object ob) {
		outServer.println((String) ob);
	}

}
