package SearchClasses;

import java.io.Serializable;

public class FileDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	private String fileName;
	private int size;

	public FileDetails(String fileName, int size) {
		this.fileName = fileName;
		this.size = size;
	}

	public String getFileName() {
		return fileName;
	}

	public int getSize() {
		return size;
	}

	@Override
	public boolean equals(Object obj) {
		FileDetails temp = (FileDetails) obj;
		if (fileName.equals(temp.getFileName()) && size == temp.getSize())
			return true;
		return false;
	}

}
