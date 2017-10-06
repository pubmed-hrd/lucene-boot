package com.medline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.medline.service.IndexingService;

@SpringBootApplication
public class MedlineLucenceBootApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(MedlineLucenceBootApplication.class, args);
		
		IndexingService idx = context.getBean(IndexingService.class);
		idx.createIndex();
		
	}
}
