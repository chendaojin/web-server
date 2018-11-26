package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import GUI.ServerGUI;

/**
 * 主类，开启服务器
 * 
 */
public class Main {

	// web服务器端套接字
	public static ServerSocket server;

	// 关闭线程
	public static Thread pt;

	// 运行线程
	public static Thread rt;

	// 服务器运行状态
	public static boolean state = false;

	// 日志记录器
	public static Logger logger = Logger.getLogger(Main.class);

	// 线程池
	public static ExecutorService executor;

	/**
	 * 主方法
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		try {
			Class.forName("server.Constants");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if (args[0].equals("GUI")) {
			ServerGUI.openGUI();
		} else {
			start();
		}
	}

	/**
	 * 开启服务器方法
	 * 
	 * @return
	 */
	public static boolean start() {
		try {
			// 建立线程池
			executor = Executors.newCachedThreadPool();
			// 监听web服务端口
			server = new ServerSocket(Constants.port);
			// 开启关闭线程
			if (pt == null) {
				pt = new Thread(new Exit());
				pt.start();
			}
			// 开启运行线程
			if (rt == null) {
				rt = new Thread(new Accept());
				rt.start();
			}
			// 状态设为true
			state = true;
			// 记录信息日志
			logger.info("server start with port: " + Constants.port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return state;
	}

	/**
	 * 关闭服务器方法
	 * 
	 * @return
	 */
	public static boolean shutdown() {
		try {
			// 关闭web服务器套接字，释放web服务端口
			if (server != null) {
				server.close();
				server = null;
			}
			// 关闭线程池
			if (executor != null) {
				executor.shutdownNow();
			}
			// 状态设为false
			state = false;
			// 记录日志，info级别
			logger.info("shutdown server succeesfully");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return !state;
	}

}
