DuoduoAudioRobot
================

铃声多多抓取机器人。用来抓取铃声多多官网的铃声并下载。
用到了GSON包，已放在了libs目录下了。


 * 使用说明
 * 每一个DuoduoRingRobotClient实例都是一个独立的线程。
 * 构造DuoduoRingRobotClient实例需要2个参数 listId：菜单id，page：分页页码 。菜单id对应关系如下：
  
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

  
  使用示例：
  
  Thread thread1 = new Thread(new DuoduoRingRobotClient(1, 0));
  
  thread1.start(); //抓取最热下面的所有数据
  
  Thread thread2 = new Thread(new DuoduoRingRobotClient(2, 0));
  
  thread2.start(); //抓取流行金曲下的所有数据
  
  等等...
