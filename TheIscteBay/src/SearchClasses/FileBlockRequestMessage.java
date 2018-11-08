package SearchClasses;

import java.io.Serializable;

public class FileBlockRequestMessage implements Serializable {

	private static final long serialVersionUID = 5051590291124625141L;

	private int startingIndex, numberOfBytes;

	public FileBlockRequestMessage(int startingIndex, int numberOfBytes) {
		this.startingIndex = startingIndex;
		this.numberOfBytes = numberOfBytes;
	}

	public int getStartingIndex() {
		return startingIndex;
	}

	public int getNumberOfBytes() {
		return numberOfBytes;
	}

}
