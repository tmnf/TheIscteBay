package PeerObjects;

import java.io.Serializable;

public class FileBlockRequestMessage implements Serializable {

	private static final long serialVersionUID = 5051590291124625141L;

	// File name
	private String fileName;

	// File's byte array starting index and number of bytes
	private int startingIndex, numberOfBytes;

	public FileBlockRequestMessage(String fileName, int startingIndex, int numberOfBytes) {
		this.fileName = fileName;
		this.startingIndex = startingIndex;
		this.numberOfBytes = numberOfBytes;
	}

	public int getStartingIndex() {
		return startingIndex;
	}

	public int getNumberOfBytes() {
		return numberOfBytes;
	}

	public String getFileName() {
		return fileName;
	}

}
