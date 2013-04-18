package me.yongbo.DuoduoRingRobot;

public class Ring {
	//铃声id
	private String id;
	//铃声名
	private String name;
	//播放次数
	private String playcnt;
	//歌手
	private String artist;
	//时长
	private String duration;
	//下载地址
	private String downUrl;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
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
	public String getDownUrl() {
		return downUrl;
	}
	public void setDownUrl(String downUrl) {
		this.downUrl = downUrl;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(id).append("|").append(name).append("|").append(playcnt).append("|").append(artist).append("|").append(duration).append("|").append(downUrl);
		return sb.toString();
	}
	
}
