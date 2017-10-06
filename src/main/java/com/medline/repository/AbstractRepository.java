package com.medline.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.medline.model.Abstract;

@Repository
public interface AbstractRepository {

	@Select("SELECT "
			+ "A .pmid,"
		    + "	A .art_arttitle as title,"
		    + "	A .art_journal_title as journal_title,"
       		+ "	AT.value as abstract,"
       		+ "	to_char(P.date, 'YYYYMMDD') as publish_date"
	        + "	FROM"
		    + "	pmid_to_date P"
		    + "	INNER  JOIN medcit_art_abstract_abstracttext AT ON AT.pmid = P.pmid"
		    + "	INNER  JOIN medcit A ON A.pmid = P.pmid"
		    + "	ORDER BY P.pmid"
		    + "	LIMIT #{limit} OFFSET #{offset}")
	public List<Abstract> findAll(@Param("limit") Integer limit, @Param("offset") Integer offset);
	
}
