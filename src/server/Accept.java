package server;

import java.io.IOException;

public class Accept implements Runnable {

	public void run() {
		// 每当有访问请求，执行处理线程
		while (true && Main.server != null) {
			try {
				Main.executor.execute(new Handle(Main.server.accept()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
