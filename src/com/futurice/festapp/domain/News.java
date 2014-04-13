package com.futurice.festapp.domain;

import java.util.Date;

public class News {
	private String id;
	private String title;
	private String image;
	private String teaserText;
	private String content;
	private Date time;
	private String status;

	public News(){}

	public News(String id, String title, String image, String teaserText, String content,
			Date time, String status) {
		this.id = id;
		this.title = title;
		this.image = image;
		this.teaserText = teaserText;
		this.content = content;
		this.time = time;
		this.status = status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getTeaserText() {
		return teaserText;
	}

	public void setTeaserText(String teaserText) {
		this.teaserText = teaserText;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	};
	
	
}
