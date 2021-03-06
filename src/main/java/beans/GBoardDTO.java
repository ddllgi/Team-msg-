package beans;

import java.sql.Timestamp;

public class GBoardDTO {

	private int g_number;
	private String mem_id;
	private String g_nickname;
	private String g_title;
	private String g_body;
	private Timestamp g_date;
	private int g_count;
	private int g_scrap;
	private int g_category; //글카테고리->11:해리포터1, 12:해리포터2, 13:해리포터3,,,
	private int g_ref; //게시판분류->1:자유, 2:정보, 3:별점, 4:hot
	
	private int g_comments; //댓글 수 담을

	public int getG_number() {
		return g_number;
	}

	public void setG_number(int g_number) {
		this.g_number = g_number;
	}

	public String getMem_id() {
		return mem_id;
	}

	public void setMem_id(String mem_id) {
		this.mem_id = mem_id;
	}

	public String getG_nickname() {
		return g_nickname;
	}

	public void setG_nickname(String g_nickname) {
		this.g_nickname = g_nickname;
	}

	public String getG_title() {
		return g_title;
	}

	public void setG_title(String g_title) {
		this.g_title = g_title;
	}

	public String getG_body() {
		return g_body;
	}

	public void setG_body(String g_body) {
		this.g_body = g_body;
	}

	public Timestamp getG_date() {
		return g_date;
	}

	public void setG_date(Timestamp g_date) {
		this.g_date = g_date;
	}

	public int getG_count() {
		return g_count;
	}

	public void setG_count(int g_count) {
		this.g_count = g_count;
	}

	public int getG_scrap() {
		return g_scrap;
	}

	public void setG_scrap(int g_scrap) {
		this.g_scrap = g_scrap;
	}

	public int getG_category() {
		return g_category;
	}

	public void setG_category(int g_category) {
		this.g_category = g_category;
	}

	public int getG_ref() {
		return g_ref;
	}

	public void setG_ref(int g_ref) {
		this.g_ref = g_ref;
	}

	public int getG_comments() {
		return g_comments;
	}

	public void setG_comments(int g_comments) {
		this.g_comments = g_comments;
	}

	
	
	
}
