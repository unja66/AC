package com.kukung.ac;

public class Mp3Item {
	private String title;
	private String url;
	private String fileName;
	
	public Mp3Item(){}

	public Mp3Item(String fileName,String url,  String title) {
		this.fileName = fileName;
		this.title = title;
		this.url = url;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
