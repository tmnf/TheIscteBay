package Connections;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

import User.Client;
import User.User;

public class ServerConnection extends GeneralConnection {

	// Server Channels
	private BufferedReader inServer;
	private PrintWriter outServer;

	private LinkedList<User> tempList;

	public ServerConnection(Socket so, Client client) throws IOException {
		super(so, client);
		tempList = new LinkedList<>();
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

		if (temp[0].equals("CLT")) {
			tempList.clear();
			while (!temp[0].equals("END")) {
				System.out.println(temp[0] + " " + temp[1] + " " + temp[2] + " " + temp[3]); // So para teste em aula
				tempList.add(new User(temp[1], Integer.parseInt(temp[2]), Integer.parseInt(temp[3])));
				try {
					temp = inServer.readLine().split(" ");
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

	@Override
	public void send(Object ob) {
		outServer.println((String) ob);
	}

}
