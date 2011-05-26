package com.ivl.cviewer;

public class CommentEntry {
	private String user;
	private String comment;
	private int imageId;
	
	public CommentEntry(String user, String comment) {
		this.user = user;
		this.comment = comment;
		imageId = -1;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}
}
