package InfoCarriers;

import java.io.Serializable;

import PeerObjects.FileBlockRequestMessage;

public class UploadedPart implements Serializable {

	private static final long serialVersionUID = 2147505129483981539L;

	// File part to send
	private byte[] filePart;

	// Information about file part
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
