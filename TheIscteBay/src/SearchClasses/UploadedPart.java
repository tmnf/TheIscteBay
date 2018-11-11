package SearchClasses;

import java.io.Serializable;

public class UploadedPart implements Serializable {

	private static final long serialVersionUID = 2147505129483981539L;

	private byte[] filePart;
	private FileBlockRequestMessage partInfo;

	public UploadedPart(byte[] filePart, FileBlockRequestMessage partInfo) {
		this.filePart = filePart;
		this.partInfo = partInfo;
	}

	public byte[] getFilePart() {
		return filePart;
	}

	public FileBlockRequestMessage getPartInfo() {
		return partInfo;
	}

}
