package com.medline.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

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
import com.medline.repository.AbstractRepository;

@Service
public class IndexingService {

	private Analyzer analyzer;
	private AbstractRepository repository;
	
	@Value("${app.limit:100000}")
	private Integer limit;
	
	/*@Value("${app.indexPath:/mnt/disk3/luceneIndex}")
	private String indexPath;
	
	@Value("${app.isAppend:false}")
	private boolean isAppend;*/
	
	@Value("${app.startPage:1}")
	private Integer startPage;
	
	@Autowired
	public IndexingService(AbstractRepository repository) {
		this.repository = repository;
	}

	public boolean createIndex(String indexPath, boolean isAppend) {
		
		System.out.println(String.format("%s, %s, %s", indexPath, startPage, limit));
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
			
			Integer totalPages = (int) Math.ceil(27_575_896/limit);
			
			for (int page = 1; page <= 10; page++) {
				System.out.println(String.format("-> Start reading %s records[page %s of %s]", limit, page, totalPages));
				long start = System.currentTimeMillis();
				
				List<Abstract> abstracts = repository.findAll(getLimit(), (page - 1) * getLimit());
				
				System.out.println(String.format("-> Finish reading from database in %s seconds", (System.currentTimeMillis() - start) * Math.pow(10, -3)));
				addIndex(writer, abstracts);
				abstracts = null;
			}
			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void addIndex(IndexWriter write, List<Abstract> abstracts) throws IOException {
		System.out.println(String.format("-> Start indexing %s pmid...", abstracts.size()));
		long start = System.currentTimeMillis();
		for (Abstract abs : abstracts) {
			try{
				Document doc = new Document();
				if(abs.getValue() != null){
					doc.add(new TextField("title", abs.getTitle(), Field.Store.YES));
					doc.add(new TextField("abstract", abs.getValue(), Field.Store.YES));
					doc.add(new TextField("journalTitle", abs.getJournalTitle(), Field.Store.YES));
					doc.add(new StringField("date", abs.getDate(), Field.Store.YES));
					doc.add(new StringField("pmid", abs.getPmid(), Field.Store.YES));
					write.addDocument(doc);
				}
			}catch(Exception ex){
				System.out.println("Exception: " + ex.getMessage() + ", " + abs);
			}
		}
		System.out.println(String.format("-> Finish indexing in %s seconds.\n", (System.currentTimeMillis() - start) * Math.pow(10, -3)));
	}

	
	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	
	
	
	
	
}
