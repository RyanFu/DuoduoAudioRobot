package me.yongbo.DuoduoRingRobot;

import java.util.List;

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
		
		new Thread(new DuoduoRingRobotClient(22, 0, 100)).start();
		new Thread(new DuoduoRingRobotClient(22, 101, 200)).start();
		new Thread(new DuoduoRingRobotClient(22, 201, 300)).start();
		new Thread(new DuoduoRingRobotClient(22, 301, 400)).start();
		new Thread(new DuoduoRingRobotClient(22, 401, 500)).start();
		new Thread(new DuoduoRingRobotClient(22, 501, 600)).start();
		new Thread(new DuoduoRingRobotClient(22, 601, 700)).start();
		new Thread(new DuoduoRingRobotClient(22, 701, 800)).start();
		new Thread(new DuoduoRingRobotClient(22, 801, 900)).start();
		new Thread(new DuoduoRingRobotClient(22, 901)).start();
		//List<Ring> rings = new DuoduoRingRobotClient(1, 1).readFromCache(1,2);
		//System.err.println(rings.size());
	}
}
