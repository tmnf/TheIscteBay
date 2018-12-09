package Downloads;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import HandlerClasses.UploadedPart;
import PeerConnections.ConnectionToPeer;
import SearchClasses.FileBlockRequestMessage;
import User.GUI;
import User.User;

public class DownloadManager extends Thread {

	public static final int SIZEPART = 1024;
	public static final String path = "files/final.zip";

	private byte[] fileDowloading;
	private int currentSize;

	private long startTime;

	private HashMap<User, Integer> uploaders;

	private GUI gui;

	private ArrayList<ConnectionToPeer> peersWithFile;

	public DownloadManager(int fileSize, GUI gui, ArrayList<ConnectionToPeer> peersWithFile) {
		this.gui = gui;
		this.peersWithFile = peersWithFile;

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

		closePeers();
		saveFile(); // Salvar arquivo
	}

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

	public void saveFile() {
		try {
			Files.write(Paths.get(path), fileDowloading);
		} catch (IOException e) {
			e.printStackTrace();
		}

		generateLog();
	}

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

	private void closePeers() {
		for (ConnectionToPeer x : peersWithFile)
			x.interrupt();
	}

}
