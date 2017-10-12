package com.medline.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.medline.model.Abstract;

@Service
public class FileReaderService {

	@Value("${app.filePath}")
	private String filePath;

	@SuppressWarnings({ "unchecked" })
	public List<Abstract> read(String file) {
		System.out.println(String.format("-> Reading %s...", file));
		
		try(ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
			try {
				return (List<Abstract>) ois.readObject();
			} catch (ClassNotFoundException e) {
				System.out.println(e.getMessage());
			}
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	public List<String> findFiles(){
		File folder = new File(filePath);
		File[] listOfFiles = folder.listFiles();
		List<String> files = new ArrayList<>();
		for (int i = 0; i < listOfFiles.length; i++) {
			files.add(listOfFiles[i].getAbsolutePath());
		}
		return files;
	}
}
