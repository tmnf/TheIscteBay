package PeerObjects;

import java.io.Serializable;

public class WordSearchMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	// Name of the file requested
	private String keyWord;

	public WordSearchMessage(String keyWord) {
		this.keyWord = keyWord;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public String toString() {
		return keyWord;
	}

}
