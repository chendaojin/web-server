package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

/**
 * 服务器关闭线程类
 * 
 */
public class Exit implements Runnable {

	// 关闭线程服务器端套接字
	public static ServerSocket shutdown;

	// 日志记录器
	public static Logger logger = Logger.getLogger(Exit.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		int listenPort = Constants.shutdownPort;
		/*
		 * 监听shutdown端口,如果被占用，由系统自动分配端口 若绑定端口出现其他异常，可能导致程序无法正常关闭，故此阶段捕获异常均退出程序
		 */
		try {
			shutdown = new ServerSocket(Constants.shutdownPort);
		} catch (BindException e1) {
			try {
				// 参数为0，系统自动分配端口
				shutdown = new ServerSocket(0);
				listenPort = shutdown.getLocalPort();
				Constants.setShutdwonPort(listenPort);
				Constants.writeSysDocument();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		} finally {
			// 记录日志，info级别
			logger.info("listen shutdown port: " + listenPort);
		}
		try {
			while (true && shutdown != null) {
				Socket socket = shutdown.accept();
				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(socket.getInputStream(),
								Constants.charset));
				OutputStreamWriter osw = new OutputStreamWriter(
						socket.getOutputStream());
				String str;
				// 收到shutdown信息，关闭服务器，否则返回"fail"

				do {
					str = buffer.readLine();
				} while (str == null || str.isEmpty());
				if (str.equals(Constants.shutdownWord)) {
					osw.append("success");
					osw.flush();
					osw.close();
					socket.close();
					exitAll();
				} else {
					// 记录日志，info级别
					logger.info("received bad message");
					// 返回"fail"信息
					osw.append("fail");
					osw.flush();
					osw.close();
					socket.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void exitAll() {
		Main.shutdown();
		// 写用户配置文件
		Constants.writeUserDocument();
		System.exit(0);
	}
}
