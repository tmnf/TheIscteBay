package Handlers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Client.GUI;
import Client.User;
import InfoCarriers.UploadedPart;
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

	// GUI
	private GUI gui;

	public DownloadManager(int fileSize, GUI gui, String path) {
		this.gui = gui;
		this.path = path;

		gui.startProgressBar(fileSize);

		fileDowloading = new byte[fileSize];
		startTime = System.currentTimeMillis();

		uploaders = new HashMap<>();
	}

	@Override
	public synchronized void run() {
		while (currentSize != fileDowloading.length) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		saveFile();
	}

	/* Receives file part and mounts it, updating GUI */
	public synchronized void receiveFilePart(UploadedPart filePartReceived, User user) {

		if (uploaders.containsKey(user))
			uploaders.put(user, uploaders.get(user) + 1);
		else
			uploaders.put(user, 1);

		byte[] filePart = filePartReceived.getFilePart();
		FileBlockRequestMessage info = filePartReceived.getPartInfo();

		currentSize += filePart.length;

		int start = info.getStartingIndex();
		int finish = start + info.getNumberOfBytes();

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

}
