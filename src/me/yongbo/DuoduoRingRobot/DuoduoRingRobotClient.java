package me.yongbo.DuoduoRingRobot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/*
 * @author yongbo_
 * @created 2013/4/16
 * 
 * */
public class DuoduoRingRobotClient implements Runnable {
	public static String GET_RINGINFO_URL = "http://www.shoujiduoduo.com/ringweb/ringweb.php?type=getlist&listid=%1$d&page=%2$d";
	public static String GET_DOWN_URL = "http://www.shoujiduoduo.com/ringweb/ringweb.php?type=geturl&act=down&rid=%1$d";
	public static String FILE_DIR = "E:/RingData/";
	public static String FILE_NAME = "listId=%1$d.txt";
	private int listId;
	private int page;
	
	private int hasMore = 1;
	
	/**
	 * 构造函数
	 * @param listId 菜单ID
	 * @param page 所要请求的分页页码
	 * */
	public DuoduoRingRobotClient(int listId, int page) {
		this.listId = listId;
		this.page = page;
	}

	/**
	 * 获取铃声
	 * */
	public void getRings() {
		String url2 = String.format(GET_RINGINFO_URL, listId, page);
		URL url;
		URLConnection conn;
		StringBuilder sb = new StringBuilder();
		String resultStr = "";
		try {
			url = new URL(url2);
			conn = url.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader bufReader = new BufferedReader(isr);
			String lineText;
			while ((lineText = bufReader.readLine()) != null) {
				sb.append(lineText);
			}
			resultStr = sb.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		hasMore = getHasmore(resultStr);
		page = getNextPage(resultStr);
		ringParse(resultStr.replaceAll("\\{\"hasmore\":[0-9]*,\"curpage\":[0-9]*\\},", "").replaceAll(",]", "]"));
	}

	/**
	 * 将json字符串转化成Ring对象，并存入txt中
	 * @param json Json字符串
	 * */
	public void ringParse(String json) {
		Ring ring = null;
		JsonElement element = new JsonParser().parse(json);
		JsonArray array = element.getAsJsonArray();
		// 遍历数组
		Iterator<JsonElement> it = array.iterator();
		Gson gson = new Gson();
		while (it.hasNext()) {
			JsonElement e = it.next();
			// JsonElement转换为JavaBean对象
			ring = gson.fromJson(e, Ring.class);
			if(isAvailableRing(ring)){
				System.out.println(ring.toString());
				writeToFile(ring.toString());
			}
		}
	}
	/**
	 * 写入txt
	 * @param data 字符串
	 * */
	private void writeToFile(String data) {
		String path = FILE_DIR + String.format(FILE_NAME, listId);
		File dir = new File(FILE_DIR);
		File file = new File(path);
		FileWriter fw = null;
		if(!dir.exists()){
			dir.mkdirs();
		}
		try {
			if(!file.exists()){
				file.createNewFile();
			}
			fw = new FileWriter(file, true);
			fw.write(data);
			fw.write("\r\n");
			fw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				if(fw != null){
					fw.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		while(hasMore == 1){
			getRings();
		}
		System.out.println("ending...");
	}
	
	private int getHasmore(String resultStr){
		Pattern p = Pattern.compile("\"hasmore\":([0-9]*),\"curpage\":([0-9]*)");
        Matcher match = p.matcher(resultStr);
        if (match.find()) {
        	return Integer.parseInt(match.group(1));
        }
        return 0;
	}
	private int getNextPage(String resultStr){
		Pattern p = Pattern.compile("\"hasmore\":([0-9]*),\"curpage\":([0-9]*)");
		Matcher match = p.matcher(resultStr);
		if (match.find()) {
			return Integer.parseInt(match.group(2));
		}
		return 0;
	}
	/**
	 * 判断当前Ring是否满足条件。当Ring的name大于50个字符或是duration为小数则不符合条件，将被剔除。
	 * @param ring 当前Ring对象实例
	 * */
	private boolean isAvailableRing(Ring ring){
		Pattern p = Pattern.compile("^[1-9][0-9]*$");
		Matcher match = p.matcher(ring.getDuration());
		if(!match.find()){
			return false;
		}
		if(ring.getName().length() > 50 || ring.getArtist().length() > 50){
			return false;
		}
		return true;
	}
}
