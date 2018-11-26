package exit;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class RequestExit {
	public static void main(String args[]) {
		try {
			// 读xml配置
			SAXReader reader = new SAXReader();
			Document sysCfg = reader.read(new File("conf/sysCfg.xml"));
			Element sysRootElt = sysCfg.getRootElement();
			int port = Integer.parseInt(sysRootElt
					.elementTextTrim("shutdown_port"));
			String shutdownWord = sysRootElt.elementTextTrim("shutdown_word");
			// 建立socket连接
			Socket socket = new Socket("localhost", port);

			OutputStreamWriter osw = new OutputStreamWriter(
					socket.getOutputStream());
			InputStreamReader isr = new InputStreamReader(
					socket.getInputStream());
			// 发送停止服务器命令
			osw.write("\r\n" + shutdownWord + "\r\n");
			osw.flush();

			char[] cbuf = new char[1024];
			int len = isr.read(cbuf);
			String retbuf = new String(cbuf, 0, len);
			if (retbuf.equals("fail")) {
				System.out.println("关闭服务器失败");
			} else {
				System.out.println("关闭服务器成功");
			}
			// 关闭输出流和套接字
			isr.close();
			osw.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

}
