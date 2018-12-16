package Handlers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Client.Client;
import Client.GUI;
import Client.User;
import InfoCarriers.FilePart;
import PeerObjects.FileBlockRequestMessage;

public class DownloadManager extends Thread {

	// Constants
	public static final int SIZEPART = 1024;

	// File Path
	public String path;

	// Current File Downloading Information
	private byte[] fileDowloading;
	private int currentSize;

	// Download Start Time
	private long startTime;

	// Peers Uploading File
	private HashMap<User, Integer> uploaders;

	// Users with file info
	private ArrayList<User> usersWithFile;
	private int usersUploading;

	// GUI
	private GUI gui;

	// Client
	private Client client;

	public DownloadManager(int fileSize, Client client, String path, ArrayList<User> usersWithFile) {
		this.gui = client.getGui();
		this.path = path;
		this.usersWithFile = usersWithFile;

		gui.startProgressBar(fileSize);

		usersUploading = usersWithFile.size();
		fileDowloading = new byte[fileSize];
		startTime = System.currentTimeMillis();

		uploaders = new HashMap<>();
	}

	@Override
	public synchronized void run() {
		while (currentSize != fileDowloading.length) {
			if (usersUploading == 0)
				requestLastParts();

			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			gui.updatePeers(usersUploading, usersWithFile.size(), true);
		}

		saveFile();
		gui.updatePeers(0, 0, false);
	}

	/* Requests the lost parts to good uploaders */
	private void requestLastParts() {
		usersUploading = usersWithFile.size();
		client.connectToPeersWithFile(usersWithFile, this);
	}

	/* Receives file part and mounts it, updating GUI */
	public synchronized void receiveFilePart(FilePart filePartReceived, User user) {

		if (uploaders.containsKey(user))
			uploaders.put(user, uploaders.get(user) + 1);
		else
			uploaders.put(user, 1);

		byte[] filePart = filePartReceived.getFilePart();
		FileBlockRequestMessage info = filePartReceived.getPartInfo();

		currentSize += filePart.length;

		int start = info.getOffset();
		int finish = start + info.getLenght();

		for (int i = start, aux = 0; i < finish; i++, aux++) {
			fileDowloading[i] = filePart[aux];
		}

		gui.progressOnBar(currentSize);

		notify();
	}

	/* After download complete saves byte array to file */
	public void saveFile() {
		try {
			Files.write(Paths.get(path), fileDowloading);
		} catch (IOException e) {
			e.printStackTrace();
		}

		generateLog();
	}

	/* Generates download complete report window */
	private void generateLog() {
		long timeSpent = (System.currentTimeMillis() - startTime);

		String log = "";
		for (Map.Entry<User, Integer> x : uploaders.entrySet()) {
			User aux = x.getKey();
			log += "Fornecedor [Endereço: " + aux.getEndereco() + ", Porto: " + aux.getPorto() + "]: " + x.getValue()
					+ "\n";
		}
		if (timeSpent > 1000)
			log += "Tempo decorrido: " + (timeSpent / 1000) + "s";
		else
			log += "Tempo decorrido: " + timeSpent + "ms";

		JOptionPane.showMessageDialog(new JFrame(), log, "Download Concluído", 1);
	}

	/* Bad uploaders handling */

	public synchronized void disconnectUser() {
		usersUploading--;
	}

	public synchronized void notifyBadUploader(User user) {
		disconnectUser();
		User aux = null;
		for (User x : usersWithFile)
			if (x.equals(user))
				aux = x;

		if (aux != null)
			usersWithFile.remove(aux);
	}

}
