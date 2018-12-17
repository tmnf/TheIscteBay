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

	// Request Manager
	private DownloadRequestManager requestManager;

	public PeerConnected(Socket so, Client client) throws IOException {
		super(so, client);

		requestManager = client.getRequestManager();
	}

	@Override
	public void dealWith(Object aux) throws IOException {
		if (aux instanceof WordSearchMessage)
			sendFilesInFolder((WordSearchMessage) aux);
		else if (aux instanceof FileBlockRequestMessage)
			requestManager.addRequest(new RequestInfo((FileBlockRequestMessage) aux, this));
	}

	/* Outcome Methods */

	/* Sends files with a certain name to a peer requesting */
	public void sendFilesInFolder(WordSearchMessage wordMessage) throws IOException {
		String fileName = wordMessage.getKeyWord();
		FileDetails[] file = Utils.getFilesWithName(mainClient.getPath(), fileName);
		send(file);
	}

	/* Answers a file part download request */
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
