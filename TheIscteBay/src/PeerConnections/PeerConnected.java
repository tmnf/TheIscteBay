package PeerConnections;

import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import Client.Client;
import Connections.PeerConnection;
import Handlers.DownloadRequestManager;
import InfoCarriers.RequestInfo;
import InfoCarriers.FilePart;
import PeerObjects.FileBlockRequestMessage;
import PeerObjects.FileDetails;
import PeerObjects.WordSearchMessage;
import Utils.Utils;

public class PeerConnected extends PeerConnection {

	// Request Manger
	private DownloadRequestManager requestManager;

	public PeerConnected(Socket so, Client client, DownloadRequestManager requestManager) throws IOException {
		super(so, client);

		this.requestManager = requestManager;
	}

	@Override
	public void dealWith(Object aux) throws IOException {
		if (aux instanceof WordSearchMessage) // Enviar lista de ficheiros com o nome desejado
			sendFilesInFolder(((WordSearchMessage) aux).toString());
		else if (aux instanceof FileBlockRequestMessage) // Enviar parte do ficheiro desejado
			requestManager.addRequest(new RequestInfo((FileBlockRequestMessage) aux, this));
	}

	/* Outcome Methods */

	public void sendFilesInFolder(String fileName) throws IOException {
		FileDetails[] file = Utils.getFilesWithName(mainClient.getPath(), fileName);
		send(file);
	}

	public void sendFilePartRequested(FileBlockRequestMessage uploadPartInfo) throws IOException {
		byte[] filePart = Utils.getFilePart(uploadPartInfo, mainClient.getPath());

		Timer timeOut = startTimeOutTimer();
		send(new FilePart(filePart, uploadPartInfo));

		requestManager.closeRequest();
		timeOut.cancel();
	}

	/* Checks if the client takes more than 10s to send a part */
	private Timer startTimeOutTimer() {
		Timer timer = new Timer();

		int timeOutTime = 10000;

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				handleInterruption();
			}
		}, timeOutTime);

		return timer;
	}

	@Override
	protected void handleInterruption() {
		interrupt();
	}

}
