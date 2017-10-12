package com.medline.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.medline.model.Abstract;
import com.medline.repository.AbstractRepository;

@Service
public class FileWriterService {

	private AbstractRepository repo;

	@Value("${app.limit}")
	private Integer limit;

	@Value("${app.filePath}")
	private String filePath;
	
	@Value("${app.startPage:1}")
	private Integer startPage;
	
	@Autowired
	public FileWriterService(AbstractRepository repo) {
		this.repo = repo;
	}

	public void write() {
		System.out.println(String.format("=> READ data from database and WRITE to file. [Path: %s, StartPage: %s, Limit: %s]", filePath, startPage, limit));
		Integer totalPages = (int) Math.ceil(27_575_896 / limit);
		
		for (int page = startPage; page <= 5; page++) {
			System.out.println(String.format("-> Start reading %s records[page %s of %s]", limit, page, totalPages));
			long start = System.currentTimeMillis();
			
			List<Abstract> abstracts = repo.findAll(limit, (page - 1) * limit);
			
			System.out.println(String.format("-> Finish reading from database in %s seconds", (System.currentTimeMillis() - start) * Math.pow(10, -3)));
			
			writeToFile(abstracts, page);
			abstracts = null;
		}
	}
	
	private void writeToFile(List<Abstract> abstracts, Integer page){
		System.out.println(String.format("-> Writing %s -> %s/page%s.out...", abstracts.size(), filePath, page));
		long start = System.currentTimeMillis();
		try (ObjectOutputStream ois = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filePath + String.format("/page%s.out", page))))){
			try {
				ois.writeObject(abstracts);
			} catch (IOException e) {
				e.printStackTrace();
			}		
		} catch (FileNotFoundException ex) {
			System.out.println(ex.getMessage());
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		} finally{
			System.out.println(String.format("-> Finish writing file in %s seconds\n", (System.currentTimeMillis() - start) * Math.pow(10, -3)));
		}
	}
	
	@PostConstruct
	public void test(){
		File f = new File(filePath);
		if(!f.exists())
			f.mkdirs();
	}
}
