package com.medline.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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

	@Value("${app.limit:100000}")
	private Integer limit;

	@Value("${app.filePath:E:/Test}")
	private String filePath;

	@Value("${app.startPage:1}")
	private Integer startPage;
	
	@Autowired
	public FileWriterService(AbstractRepository repo) {
		this.repo = repo;
	}

	public void write() {
		System.out.println(String.format("%s, %s, %s", filePath, startPage, limit));
		Integer totalPages = (int) Math.ceil(27_575_896 / limit);
		
		for (int page = startPage; page <= totalPages; page++) {
			
			System.out.println(String.format("-> Start reading %s records[page %s of %s]", limit, page, totalPages));
			long start = System.currentTimeMillis();
			List<Abstract> abstracts = repo.findAll(limit, (page - 1) * limit);
			System.out.println(String.format("-> Finish reading from database in %s seconds", (System.currentTimeMillis() - start) * Math.pow(10, -3)));
			
			writeToFile(abstracts, page);
			abstracts = null;
		}
	}

	private void writeToFile(List<Abstract> abstracts, Integer page){
		System.out.println(String.format("=> Writing %s absatract of page %s to file...", abstracts.size(), page));
		long start = System.currentTimeMillis();
		try {
			PrintWriter pw = new PrintWriter(new File(filePath + String.format("/page%s.out", page)));
			pw.println(abstracts);
			pw.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (Exception ex){
			System.out.println(ex.getMessage());
		} finally{
			System.out.println(String.format("-> Finish writing file in %s seconds", (System.currentTimeMillis() - start) * Math.pow(10, -3)));
		}
	}
	
	@PostConstruct
	public void test(){
		File f = new File(filePath);
		if(!f.exists())
			f.mkdirs();
	}
	
}
