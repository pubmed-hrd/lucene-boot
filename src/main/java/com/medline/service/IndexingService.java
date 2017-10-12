package com.medline.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.medline.model.Abstract;

@Service
public class IndexingService {

	private Analyzer analyzer;
	private FileReaderService fr;
	
	@Value("${app.limit}")
	private Integer limit;
	
	@Value("${app.indexPath}")
	private String indexPath;
	
	@Value("${app.isAppend:false}")
	private boolean isAppend;
	
	@Value("${app.startPage:1}")
	private Integer startPage;
	
	@Autowired
	public IndexingService(FileReaderService fr) {
		this.fr = fr;
	}

	public boolean createIndex() {
		
		System.out.println(String.format("=> READ data from file and INDEX data. [Path: %s, StartPage: %s, Limit: %s]", indexPath, startPage, limit));
		try {
			analyzer = new StandardAnalyzer();

			Directory dir = FSDirectory.open(Paths.get(indexPath));
			IndexWriterConfig config = new IndexWriterConfig(analyzer);

			if (!isAppend) {
				config.setOpenMode(OpenMode.CREATE);
			} else {
				config.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}			
			IndexWriter writer = new IndexWriter(dir, config);
			
			long start = System.currentTimeMillis();
			for (String file: fr.findFiles()) {
				List<Abstract> abstracts = fr.read(file);
				System.out.println(String.format("-> Start indexing file: %s...", file));
				
				for(Abstract abs: abstracts){
					try{
						Document doc = new Document();
						if(abs.getValue() != null){
							doc.add(new TextField("title", abs.getTitle(), Field.Store.YES));
							doc.add(new TextField("abstract", abs.getValue(), Field.Store.YES));
							doc.add(new TextField("journalTitle", abs.getJournalTitle(), Field.Store.YES));
							doc.add(new StringField("date", abs.getDate(), Field.Store.YES));
							doc.add(new StringField("pmid", abs.getPmid(), Field.Store.YES));
							writer.addDocument(doc);
						}
					}catch(Exception ex){
						System.out.println("Exception: " + ex.getMessage() + ", " + abs);
					}
				}
				System.out.println(String.format("-> Finish indexing file: %s in seconds.\n", (System.currentTimeMillis() - start) * Math.pow(10, -3)));
			}
			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@PostConstruct
	public void checkIndexPath(){
		File file = new File(indexPath);
		if(!file.exists()){
			file.mkdirs();
			System.out.println(String.format("Folder: %s created!", indexPath));
		}
	}

}
