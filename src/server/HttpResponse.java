package server;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 响应封装类 每个对象均有对应的get、set方法，Map类的成员特殊处理
 * 
 */
public class HttpResponse {

	// 状态
	private String state;

	// 首部字段
	private Map<String, String> header = new HashMap<String, String>();

	// 请求文件
	private File file;

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getProperty(String key) {
		return header.get(key);
	}

	public void setProperty(String key, String value) {
		header.put(key, value);
	}

	public void delProperty(String key) {
		header.remove(key);
	}

	public void clrAllProperty() {
		header.clear();
	}

	public Map<String, String> getHeader() {
		return header;
	}

	public void setHeader(Map<String, String> header) {
		this.header = header;
	}

	/**
	 * header转字符串
	 * 
	 * @return
	 */
	public String header2Str() {
		StringBuffer str = new StringBuffer();
		str.append(Constants.version + " " + state + " "
				+ Constants.statusCodes.get(state)[0]);
		str.append("\r\n");
		for (Map.Entry<String, String> entry : header.entrySet()) {
			str.append(entry.getKey() + ":");
			str.append(entry.getValue() + "\r\n");
		}
		str.append("\r\n");
		return str.toString();
	}

}
