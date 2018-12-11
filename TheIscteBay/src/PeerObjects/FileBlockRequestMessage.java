package PeerObjects;

import java.io.Serializable;

public class FileBlockRequestMessage implements Serializable {

	private static final long serialVersionUID = 5051590291124625141L;

	// File name
	private String fileName;

	// File's byte array starting index and number of bytes
	private int offset, lenght;

	public FileBlockRequestMessage(String fileName, int startingIndex, int numberOfBytes) {
		this.fileName = fileName;
		this.offset = startingIndex;
		this.lenght = numberOfBytes;
	}

	public int getOffset() {
		return offset;
	}

	public int getLenght() {
		return lenght;
	}

	public String getFileName() {
		return fileName;
	}

}
