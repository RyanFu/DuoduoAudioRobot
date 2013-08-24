package me.yongbo.DuoduoRingRobot;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;


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
	private final static String rootdir = "E:/rings/data/";
	private final static String CACHE_DIR = "E:/rings/cache/";
	private final static String CACHE_KEY = "listId%1$d-page%2$d";
	
	public static String GET_RINGINFO_URL = "http://www.shoujiduoduo.com/ringweb/ringweb.php?type=getlist&listid=%1$d&page=%2$d";
	public static String GET_DOWN_URL = "http://www.shoujiduoduo.com/ringweb/ringweb.php?type=geturl&act=down&rid=%1$s";
	public static String ERROR_MSG = "listId为 %1$d 的Robot发生错误，已自动停止。当前page为 %2$d";
	public static String STATUS_MSG = "开始抓取数据，当前listId： %1$d，当前page： %2$d";
	public static String FILE_DIR = "E:/RingData/";
	public static String FILE_NAME = "listId=%1$d.txt";
	
	private int listId;
	private int page;
	private int endPage = -1;
	private int hasMore = 1;
	private DbHelper dbHelper;
	private boolean stop = false;
	
	protected static SimpleDateFormat sdf;
	private static ExecutorService pool;// 线程池
	protected final static int MAX_FAILCOUNT = 5; // 最多失败次数，请求某个URL失败超过这个次数将自动停止发起请求

	static {
		pool = Executors.newFixedThreadPool(20); // 固定线程池
		sdf = new SimpleDateFormat("yyyyMMdd/HHmm/");
	}
	/**
	 * 构造函数
	 * @param listId 菜单ID
	 * @param page 开始页码
	 * @param endPage 结束页码
	 * */
	public DuoduoRingRobotClient(int listId, int beginPage, int endPage) {
		this.listId = listId;
		this.page = beginPage;
		this.endPage = endPage;
		this.dbHelper = new DbHelper();
	}
	/**
	 * 构造函数
	 * @param listId 菜单ID
	 * @param page 开始页码
	 * */
	public DuoduoRingRobotClient(int listId, int page) {
		this(listId, page, -1);
	}

	/**
	 * 获取铃声
	 * */
	public void getRings() {
		String url = String.format(GET_RINGINFO_URL, listId, page);
		int cnt = 0;
		do{
			try {
				String responseStr = httpGet(url);
				ringParse(responseStr.replaceAll("\\{\"hasmore\":[0-9]*,\"curpage\":[0-9]*\\},", "").replaceAll(",]", "]"));
				hasMore = getHasmore(responseStr);
				page = getNextPage(responseStr);
				break;
			} catch(Exception e){
				e.printStackTrace();
				cnt++;
				System.err.println("对于数据" + url + "第" + cnt
						+ "次抓取失败,正在尝试重新抓取...");
			}
		} while(cnt < MAX_FAILCOUNT);
	}

	/**
	 * 发起http请求
	 * @param webUrl 请求连接地址
	 * */
	public String httpGet(String webUrl) throws Exception {
		URL url;
		URLConnection conn;
		StringBuilder sb = new StringBuilder();
		url = new URL(webUrl);
		conn = url.openConnection();
		conn.connect();
		InputStream is = conn.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader bufReader = new BufferedReader(isr);
		String lineText;
		while ((lineText = bufReader.readLine()) != null) {
			sb.append(lineText);
		}
		return sb.toString();
	}
	/**
	 * 写入缓存
	 * @param rings list数据
	 * */
	public void writeToCache(List<Ring> rings){
		int cnt = 0;
		do {
			try {
				File destDir = new File(CACHE_DIR);
				if (!destDir.exists()) {
					destDir.mkdirs();
				}
	            ObjectOutputStream os = new ObjectOutputStream(  
	                    new FileOutputStream(CACHE_DIR + String.format(CACHE_KEY, listId, page)));  
	            os.writeObject(rings);  
	            os.close();
	            break;
	        } catch (Exception e) { 
	        	e.printStackTrace();
	        	cnt++;
	            System.err.println("写入缓存失败,正在尝试第" + cnt + "次重新写入");
	        } 
		} while(cnt < MAX_FAILCOUNT);
		
	}
	/**
	 * 读取缓存
	 * @param _listId 菜单Id
	 * @param _page 页码
	 * @return List数据
	 * */
	public List<Ring> readFromCache(final int _listId,final int _page){
		List<Ring> rings = null;
		int cnt = 0;
		do {
			try {
				ObjectInputStream is = new ObjectInputStream(new FileInputStream(CACHE_DIR + String.format(CACHE_KEY, _listId, _page)));  
	            rings = (List<Ring>) is.readObject();
	            is.close();
	            break;
	        } catch (Exception e) { 
	        	e.printStackTrace();
	        	cnt++;
	            System.err.println("读取缓存失败,正在尝试第" + cnt + "次重新读取");
	        } 
		} while(cnt < MAX_FAILCOUNT);
		return rings;
	}
	
	/**
	 * 将json字符串转化成Ring对象
	 * @param json Json字符串
	 * */
	public List<Ring> ringParse(String json) throws Exception {
		List<Ring> rings = new ArrayList<Ring>();
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
			if(isAvailableRing(ring)) {
				initSaveDir(rootdir); //获取存储目录
				
				ring.setDownUrl(getRingDownUrl(ring.getId()));
				
				String type = ring.getDownUrl().substring(ring.getDownUrl().lastIndexOf("."));
				String filename = ring.getArtist()+ "_" +ring.getName()+ "_" + ring.getId() + type;
				
				ring.setSavePath(curDir + filename);
				ring.setType(type);
				rings.add(ring);
				
				down(ring.getDownUrl(), folderPath, filename);
				//写入数据库
				//writeToDatabase(ring);
			}
		}
		//写入缓存
		writeToCache(rings);
		return rings;
	}
	
	/**
	 * 写入数据库
	 * @param ring 一个Ring的实例
	 * */
	public void writeToDatabase(Ring ring) {
		dbHelper.execute("addRing", ring);
	}

	@Override
	public void run() {
		while(hasMore == 1 && !stop){
			if(endPage != -1){
				if(page > endPage) { break; }
			}
			System.out.println(String.format(STATUS_MSG, listId, page));
			getRings();
			System.out.println(String.format("该页数据抓取完成"));
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
	/**
	 * 获取铃声的下载地址
	 * @param rid 铃声的id
	 * */
	public String getRingDownUrl(String rid) throws Exception{
		String url = String.format(GET_DOWN_URL, rid);
		String responseStr = httpGet(url);
		return responseStr.split("\\?")[0];
	}

	/**
	 * 下载网络图片到本地
	 * 
	 * @param imgUrl
	 *            网络图片路径
	 * @param folderPath
	 *            本地存放目录
	 * @param fileName
	 *            存放的文件名
	 * */
	public void down(final String imgUrl, final String folderPath,
			final String fileName) {
		System.out.println("开始下载:" + imgUrl);
		File destDir = new File(folderPath);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}

		pool.execute(new Runnable() {
			@Override
			public void run() {
				HttpClient client = HttpUtil.getHttpClient();
				HttpGet get = new HttpGet(imgUrl);
				int failCount = 0;
				do {
					try {
						HttpResponse response = client.execute(get);
						HttpEntity entity = response.getEntity();
						if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK && entity != null) {
							byte[] data = readFromResponse(entity);
							String savePath = folderPath + fileName;
							File imageFile = new File(savePath);
							FileOutputStream outStream = new FileOutputStream(
									imageFile);
							outStream.write(data);
							outStream.close();
						}
						break;
					} catch (Exception e) {
						failCount++;
						System.err.println("对于链接" + imgUrl + "第" + failCount
								+ "次下载失败,正在尝试重新下载...");
					} finally {
						
					}
				} while (failCount < MAX_FAILCOUNT);
			}
		});
	}
	
	public static byte[] readFromResponse(HttpEntity entity) throws Exception {
		InputStream inStream = entity.getContent();
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		long length = entity.getContentLength();
		// 显示文件大小格式：2个小数点显示
		DecimalFormat df = new DecimalFormat("0.00");
		// 总文件大小
		String fileSize = df.format((float) length / 1024 / 1024) + "MB";
		//缓存
		byte[] buffer = new byte[1024];
		
		int len = 0;
		// int count = 0;
		// String processText;
		long t = System.currentTimeMillis();

		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
			// 下载进度
			// count += len;
			// processText = df.format((float) count / 1024 / 1024) + "MB" + "/"
			// + fileSize;
			// System.out.println(processText);
		}
		System.out
				.println("下载完成，耗时：" + (System.currentTimeMillis() - t) + "毫秒");
		inStream.close();
		return outStream.toByteArray();
	}
	
	protected String curDir; //按照当前时间生成的目录
	protected String folderPath; //图片存放的目錄（绝对路径）

	public void initSaveDir(String rootDir) {
		Date date = new Date();
		curDir = sdf.format(date);
		folderPath = rootDir + curDir;
	}
	
	public static Map<String, String> getRequestHeaders() {
		Map<String, String> param = new HashMap<>();
		param.put("Host", "bcs.duapp.com");
		param.put("Pragma", "no-cache");
		return param;
	}
}
