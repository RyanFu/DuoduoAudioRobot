package me.yongbo.DuoduoRingRobot;

public class Ring {
	private int id;
	private String name;
	private String playcnt;
	private String artist;
	private String duration;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPlaycnt() {
		return playcnt;
	}
	public void setPlaycnt(String playcnt) {
		this.playcnt = playcnt;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(id).append("|").append(name).append("|").append(playcnt).append("|").append(artist).append("|").append(duration);
		return sb.toString();
	}
	
}
