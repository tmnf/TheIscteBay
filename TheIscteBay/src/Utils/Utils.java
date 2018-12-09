package Utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import SearchClasses.FileBlockRequestMessage;
import SearchClasses.FileDetails;

public class Utils {

	// Devolve ficheiros com o nome desejado presentes na pasta do utilizador
	public static FileDetails[] getFilesWithName(String filePath, String fileName) throws IOException {
		File[] filesInFolder = new File(filePath).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.contains(fileName);
			}
		});

		FileDetails[] filesWithKeyWord = new FileDetails[filesInFolder.length];

		for (int i = 0; i != filesInFolder.length; i++) {
			byte[] fileContent = Files.readAllBytes(filesInFolder[i].toPath());
			filesWithKeyWord[i] = new FileDetails(filesInFolder[i].getName(), fileContent.length);
		}

		return filesWithKeyWord;
	}

	// Devolve parte do ficheiro
	public static byte[] getFilePart(FileBlockRequestMessage temp, String filePath) throws IOException {
		byte[] file = Files.readAllBytes(Paths.get(filePath + "/" + temp.getFileName()));
		byte[] filePart = new byte[temp.getNumberOfBytes()];
		for (int i = 0, aux = temp.getStartingIndex(); i != temp.getNumberOfBytes(); i++, aux++)
			filePart[i] = file[aux];
		return filePart;
	}

}
