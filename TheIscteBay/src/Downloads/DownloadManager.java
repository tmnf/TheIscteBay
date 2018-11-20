package Downloads;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import Connections.PeerConnection;
import SearchClasses.FileBlockRequestMessage;
import SearchClasses.UploadedPart;

public class DownloadManager extends Thread {

	public static final int SIZEPART = 1024;
	public static final String path = "files/final.png";

	private byte[] fileDowloading;

	private int numberOfPeersStillUploading;

	public DownloadManager(int fileSize) {
		fileDowloading = new byte[fileSize];
	}

	@Override
	public synchronized void run() {
		while (numberOfPeersStillUploading != 0) {
			System.out.println(numberOfPeersStillUploading);
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		saveFile(); // Salvar arquivo
	}

	public synchronized void receiveFilePart(UploadedPart filePartReceived, PeerConnection peer) {
		numberOfPeersStillUploading--;

		byte[] filePart = filePartReceived.getFilePart();
		FileBlockRequestMessage info = filePartReceived.getPartInfo();

		for (int i = info.getStartingIndex(), aux = 0; i != info.getNumberOfBytes(); i++, aux++) {
			fileDowloading[i] = filePart[aux];
		}

		System.out.println(peer.getId() + " entregou");

		peer.interrupt();
		notify();
	}

	public void saveFile() {
		try {
			Files.write(Paths.get(path), fileDowloading);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Ficheiro recebido");
	}

	public synchronized void addPeerUploading() {
		numberOfPeersStillUploading++;
	}

}
