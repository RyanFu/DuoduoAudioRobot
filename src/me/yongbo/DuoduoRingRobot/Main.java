package me.yongbo.DuoduoRingRobot;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * 使用说明
		 * 每一个DuoduoRingRobotClient实例都是一个独立的线程。
		 * 构造DuoduoRingRobotClient实例需要2个参数 listId：菜单id，page：开始页码 ，endPage：结束页码，当没有指定结束页码时将一直读取到最后一页。
	   	 * 菜单id对应关系如下：
		 * 最热：1
		 * 最新：0
		 * 最多下载：22
		 * 好评率高：23
		 * 流行金曲：2
		 * 影视广告：3
		 * 动漫：4
		 * 短信：5
		 * DJ：6
		 * 搞笑：7
		 * 网友上传：8
		 **/
		/*
		Thread thread0 = new Thread(new DuoduoRingRobotClient(0, 0));
		thread0.start();
		
		Thread thread1 = new Thread(new DuoduoRingRobotClient(1, 0));
		thread1.start();
		
		
		Thread thread2 = new Thread(new DuoduoRingRobotClient(2, 0));
		thread2.start();
		Thread thread3 = new Thread(new DuoduoRingRobotClient(3, 0));
		thread3.start();
		*/
		/*
		Thread thread4 = new Thread(new DuoduoRingRobotClient(4, 0));
		thread4.start();
		Thread thread5 = new Thread(new DuoduoRingRobotClient(5, 0));
		thread5.start();
		Thread thread6 = new Thread(new DuoduoRingRobotClient(6, 0));
		thread6.start();
		Thread thread7 = new Thread(new DuoduoRingRobotClient(7, 0));
		thread7.start();
		
		Thread thread8 = new Thread(new DuoduoRingRobotClient(8, 0));
		thread8.start();*/
		/*
		Thread thread22 = new Thread(new DuoduoRingRobotClient(22, 917));
		thread22.start();*/
		
		
		
		Thread thread23 = new Thread(new DuoduoRingRobotClient(2, 400));
		thread23.start();
		
		/*
		Thread thread23_2 = new Thread(new DuoduoRingRobotClient(23, 601, 700));
		thread23_2.start();
		
		Thread thread23_3 = new Thread(new DuoduoRingRobotClient(23, 701, 800));
		thread23_3.start();
		
		Thread thread23_4 = new Thread(new DuoduoRingRobotClient(23, 801, 900));
		thread23_4.start();
		
		Thread thread23_5 = new Thread(new DuoduoRingRobotClient(23, 901, 914));
		thread23_5.start();
		*/
		/*
		Ring ring = new Ring();
		ring.setId(1111);
		ring.setName("aaaaa");
		ring.setPlaycnt("132wang");
		ring.setArtist("dsd");
		ring.setDuration("1323");
		ring.setDownUrl("sdsd");
		new DbHelper().execute("addRing", ring);*/
	}
}
