package Utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import PeerObjects.FileBlockRequestMessage;
import PeerObjects.FileDetails;

public class Utils {

	/*
	 * Returns existing files that contains a certain keyword in user's file folder
	 */

	public static FileDetails[] getFilesWithName(String filePath, String fileName) throws IOException {
		File[] filesInFolder = new File(filePath).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.contains(fileName);
			}
		});

		FileDetails[] filesWithKeyWord = new FileDetails[filesInFolder.length];

		int i = 0;
		for (File x : filesInFolder) {
			byte[] fileContent = Files.readAllBytes(x.toPath());
			filesWithKeyWord[i] = new FileDetails(x.getName(), fileContent.length);
			i++;
		}

		return filesWithKeyWord;
	}

	/* Returns part of a file */

	public static byte[] getFilePart(FileBlockRequestMessage temp, String filePath) throws IOException {
		byte[] file = Files.readAllBytes(Paths.get(filePath + "/" + temp.getFileName()));
		byte[] filePart = new byte[temp.getLenght()];

		for (int i = 0, aux = temp.getOffset(); i != temp.getLenght(); i++, aux++)
			filePart[i] = file[aux];

		return filePart;
	}

}
