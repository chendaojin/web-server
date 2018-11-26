package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * 响应处理线程类 获取请求，解析并返回响应
 */
public class Handle implements Runnable {

	// 客户端套接字
	Socket client;

	// 输出流
	OutputStream os;

	// 输入流
	InputStream is;

	// 请求封装对象
	HttpRequest request;

	// 响应封装对象
	HttpResponse response;

	// 日志记录器
	static Logger logger = Logger.getLogger(Handle.class);

	// 时间格式化对象
	SimpleDateFormat format = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);

	/**
	 * 构造函数，客户端Socket作为参数传入以备读写数据流
	 * 
	 * @param client
	 */
	Handle(Socket client) {
		this.client = client;
		try {
			os = client.getOutputStream();
			is = client.getInputStream();
			request = new HttpRequest();
			response = new HttpResponse();
			format.setTimeZone(TimeZone.getTimeZone("GMT"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		process();
		// 记录日志，debug级别
		logger.debug("send Response to "
				+ client.getInetAddress().getHostAddress() + ":"
				+ client.getPort() + "\r\n" + response.header2Str());
		String str = response.getState();
		char c = str.charAt(0);
		String writeStr = "";
		/*
		 * 状态码2XX属于成功类状态，写入响应头好文件流，这里处理了200,206状态
		 * 状态码3XX属于重定向类状态，直接写入处理结果，这里处理了306状态
		 * 状态码4XX、5XX分别属于客户端错误类、服务器端错误类状态，直接写入处理结果，这里处理了404、500状态
		 */
		try {
			if (c == '2') {
				writeStr = response.header2Str();
				os.write(writeStr.getBytes(Constants.charset));
				sendFile(response.getFile());
			} else if (c == '3') {
				writeStr = redirectionHandling(request.getPath());
				os.write(writeStr.getBytes(Constants.charset));
			} else if (c == '4' || c == '5') {
				if (request == null) {
					writeStr = errorHandling(str, null);
				} else {
					writeStr = errorHandling(str, request.getPath());
				}
				os.write(writeStr.getBytes("UTF-8"));
			}
			os.flush();
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.toString());
		} catch (FileNotFoundException e) {
			System.out.println(e.toString());
		} catch (SocketException e) {
			return;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭读写流和客户端套接字
			try {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
				if (client != null) {
					client.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 处理请求
	 */
	public void process() {
		// 获取请求，组装成HttpRequest对象，出现异常返回转500(服务器内部错误)处理
		try {
			request = readRequest();
		} catch (UnsupportedEncodingException e) {
			response.setState("500");
			return;
		} catch (IOException e) {
			response.setState("500");
			return;
		}
		if (request == null) {
			response.setState("400");
			return;
		}
		/*
		 * 判断路径，如果是根路径或项目根路径，则返回301(重定向)处理
		 * 此处将路径放置在request对象的属性中,到错误处理是再写入response的Location字段
		 */
		String path = request.getPath();
		if (!path.equals("/index.html") && !path.matches(Constants.fullMatch)) {
			response.setState("307");
			if (path.equals("/")) {
				if (Constants.defaultProject != null
						&& !Constants.defaultProject.isEmpty()
						&& new File(Constants.webRoot + "/"
								+ Constants.defaultProject).exists()) {
					request.setPath(path + Constants.defaultProject
							+ "/index.html");
				} else {
					request.setPath(Constants.indexPage);
				}
			} else if (path.matches(Constants.pathMatch)) {
				request.setPath(path + "/index.html");
			} else if (path.matches(Constants.rootMatch)) {
				request.setPath(path + "index.html");
			} else {
				response.setState("400");
			}
			return;
		}
		// 将路径解码，异常转500(服务器内部错误)处理
		try {
			request.setPath(URLDecoder.decode(request.getPath(),
					Constants.charset));
		} catch (UnsupportedEncodingException e1) {
			response.setState("500");
			return;
		}
		/*
		 * 获取File对象，若文件不存在返回转404(文件不存在)处理 若文件存在，设置HttpResponse对象的file属性
		 */
		File file = new File(Constants.webRoot + request.getPath());

		if (!file.exists()) {
			response.setState("404");
			return;
		} else {
			response.setFile(file);
		}
		/*
		 * 如果配置了启用浏览器缓存，则检查Modified 如果请求头有If-Modified-Since字段，与文件最后修改时间比较
		 * 如果文件在此日期后修改过，则转304(不满足条件)处理，此处即告知浏览器，文件未更新，可直接调用缓存
		 * 如果上述过程出现异常，转500(服务器内部错误)
		 */
		String modified;
		if (Constants.isCached
				&& (modified = request.getProperty("If-Modified-Since")) != null
				&& !modified.isEmpty()) {
			Date fileDate = new Date(file.lastModified());
			Date reqDate;
			try {
				reqDate = format.parse(modified);
			} catch (ParseException e) {
				response.setState("500");
				return;
			}
			if (!reqDate.before(fileDate)) {
				response.setState("304");
				return;
			}
		}
		// 若请求头有Range字段，设置状态码206(部分内容)，否则状态码为200(成功)
		if (request.getProperty("Range") != null) {
			response.setState("206");
		} else {
			response.setState("200");
		}
		// 设置响应头，出现异常返回转500(服务器内部错误)处理
		try {
			response = setResponse(file, request);
		} catch (ParseException e) {
			response.setState("500");
			return;
		}
	}

	/**
	 * 请求头处理 返回HttpRequest对象
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public HttpRequest readRequest() throws IOException {
		// 读取请求头，并转换成字符串
		byte[] b = new byte[1024 * 1024];
		int len = is.read(b);
		if (len == -1)
			return null;
		String reqStr = new String(b, 0, len, Constants.charset);
		// 记录日志，debug级别
		logger.debug("receive Request from "
				+ client.getInetAddress().getHostAddress() + ":"
				+ client.getPort() + "\r\n" + reqStr);
		// 正则表达式匹配请求行，并设置HttpRequest对象
		Pattern reqPat = Pattern.compile(Constants.reqRegex);
		Matcher reqMatch = reqPat.matcher(reqStr);
		// 匹配成功，设置值。匹配失败，返回null
		if (reqMatch.find()) {
			request.setMethod(reqMatch.group("method"));
			request.setPath(reqMatch.group("path"));
			request.setVersion(reqMatch.group("version"));
			String[] str = request.getPath().split("\\.");
			if (str.length > 0) {
				request.setContentType(Constants.contentTypes
						.get(str[str.length - 1]));
			} else {
				request.setContentType(Constants.defaultType);
			}
		} else {
			return null;
		}
		// 正则表达式匹配首部字段，并设置HttpRequest对象
		Pattern headerPat = Pattern.compile(Constants.headerRegex);
		Matcher headerMatch = headerPat.matcher(reqStr);
		while (headerMatch.find()) {
			request.setProperty(headerMatch.group("key"),
					headerMatch.group("value"));
		}
		return request;
	}

	/**
	 * 响应头处理 返回HttpResponse对象
	 * 
	 * @param file
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	public HttpResponse setResponse(File file, HttpRequest request)
			throws ParseException {
		long flen = file.length();
		// 设置响应头
		response.setProperty("Last-Modified", format.format(new Date()));
		response.setProperty("Date", format.format(new Date()));
		response.setProperty("Content-type", request.getContentType());
		if (request.getProperty("Range") != null) {
			response.setProperty("Content-Range", "bytes 0-" + (flen - 1) + "/"
					+ flen);
		}
		response.setProperty("Content-Length", String.valueOf(flen));
		return response;
	}

	/**
	 * 发送文件流处理
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void sendFile(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[Constants.dataLenth];
		/*
		 * 文件小于data容量，一次读完，否则反复分段读取 输出流满会自动刷新发送
		 */
		for (int read; (read = fis.read(data)) > -1;) {
			os.write(data, 0, read);
		}
		fis.close();
	}

	/**
	 * 错误处理 显示错误页面，包含状态码状态描述等信息，返回要发送的字符串
	 * 
	 * @param state
	 * @return
	 */
	public String errorHandling(String state, String path) {
		response.clrAllProperty();
		response.setProperty("Content-type", Constants.defaultType);
		String header = response.header2Str();
		// 错误页面状态码、状态描述等替换成该错误类型的
		String entity = Constants.errorEntity.toString().replace("%errorCode%",
				state);
		if (path != null) {
			entity = entity.replace("%errorPath%", path);
		} else {
			entity = entity.replace("%errorPath%", "Bad Request");
		}
		entity = entity.replace("%errorDesc%",
				Constants.statusCodes.get(state)[0]);
		entity = entity.replace("%errorCnDesc%",
				Constants.statusCodes.get(state)[1]);
		return header + entity;
	}

	/**
	 * 重定向处理 包含Location字段，返回要发送的字符串
	 * 
	 * @return
	 */
	public String redirectionHandling(String path) {
		response.clrAllProperty();
		response.setProperty("Location", path);
		response.setProperty("Content-type", Constants.defaultType);
		String header = response.header2Str();
		return header;
	}
}
