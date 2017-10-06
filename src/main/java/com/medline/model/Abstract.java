package com.medline.model;

public class Abstract {
	private String pmid;
	private String title;
	private String value;
	private String journalTitle;
	private String date;

	public Abstract() {
		super();
	}

	public Abstract(String pmid, String title, String value, String journalTitle, String date) {
		super();
		this.pmid = pmid;
		this.title = title;
		this.value = value;
		this.journalTitle = journalTitle;
		this.date = date;
	}

	public String getPmid() {
		return pmid;
	}

	public void setPmid(String pmid) {
		this.pmid = pmid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getJournalTitle() {
		return journalTitle;
	}

	public void setJournalTitle(String journalTitle) {
		this.journalTitle = journalTitle;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "Abstract [pmid=" + pmid + ", title=" + title + ", value=" + value + ", journalTitle=" + journalTitle
				+ ", date=" + date + "]";
	}
}
