package server;

import java.util.HashMap;
import java.util.Map;

/**
 * 响应封装类 每个对象均有对应的get、set方法，Map类的成员特殊处理
 * 
 */
public class HttpRequest {

	// 请求方法
	private String method;

	// 请求路径
	private String path;

	// 请求http协议版本
	private String version;

	// 请求文件类型
	private String ContentType;

	// 首部字段
	private Map<String, String> header = new HashMap<String, String>();

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getContentType() {
		return ContentType;
	}

	public void setContentType(String contentType) {
		ContentType = contentType;
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

	/**
	 * header转字符串
	 * 
	 * @return
	 */
	public String header2Str() {
		StringBuffer str = new StringBuffer();
		str.append(method + " " + path + " " + version);
		str.append("\r\n");
		for (Map.Entry<String, String> entry : header.entrySet()) {
			str.append(entry.getKey() + ":");
			str.append(entry.getValue() + "\r\n");
		}
		str.append("\r\n");
		return str.toString();
	}
}
