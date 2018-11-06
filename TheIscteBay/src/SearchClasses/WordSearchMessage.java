package SearchClasses;

import java.io.Serializable;

public class WordSearchMessage implements Serializable {

	private static final long serialVersionUID = 1L;

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
