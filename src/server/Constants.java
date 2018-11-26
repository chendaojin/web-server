package server;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * 常量类 系统配置，从配置文件读入常量等
 * 
 */
public class Constants {

	// web服务端口号
	public static int port;

	// 编码字符集
	public static String charset;

	// 缺省项目
	public static String defaultProject;

	// 是否启用缓存
	public static boolean isCached;

	// 状态码信息查表转换
	public static Map<String, String[]> statusCodes;

	// content-type字段查表转换
	public static Map<String, String> contentTypes;

	// 缺省首页（服务器首页）
	public static String indexPage;

	// 错误页
	public static String errorPage;

	// 默认content-type字段
	public static String defaultType;

	// 服务器关闭线程端口
	public static int shutdownPort;

	// 服务器关机字
	public static String shutdownWord;

	// 协议版本
	public static String version;

	// 读取文件流的一次读取长度
	public static int dataLenth;

	// web服务根目录
	public static String webRoot;

	// 配置根目录
	public static String confRoot;

	// 日志根目录
	public static String logsRoot;

	// 请求行正则表达式
	public static String reqRegex;

	// 首部字段正则表达式
	public static String headerRegex;

	// 项目路径匹配正则表达式
	public static String pathMatch;

	// 项目根匹配正则表达式
	public static String rootMatch;

	// 完整路径匹配正则表达式
	public static String fullMatch;

	// 错误页内容
	public static StringBuffer errorEntity;

	// 用户配置根元素
	private static Element userRootElt;

	// 系统配置根元素
	private static Element sysRootElt;

	// 协议配置根元素
	private static Element protRootElt;

	// 用户配置文档
	private static Document userCfg;

	// 系统配置文档
	private static Document sysCfg;

	// 协议配置文档
	private static Document protCfg;

	// 日志记录器
	public static Logger logger = Logger.getLogger(Constants.class);

	static {
		// 将系统输出绑定到日志文件
		File log4j = new File("logs/stdout.log");
		PrintStream ps;
		try {
			ps = new PrintStream(new DataOutputStream(new FileOutputStream(
					log4j)));
		} catch (FileNotFoundException e1) {
			ps = null;
			e1.printStackTrace();
		}
		System.setOut(ps);
		System.setErr(ps);

		// log4日志配置
		DOMConfigurator.configure("conf/log4j.xml");
		// 记录日志，info级别
		logger.info("load log-configuration successfully");

		/*
		 * 确保web目录下文件可写，设置所有文件及文件夹最新修改时间为当前时间
		 * 因为程序停止运行期间，用户对文件/文件夹的操作不可预料，此举可避免文件发生变化后被篡改最后修改时间而错误地返回301
		 */
		try {
			FileUtil.getAllFiles(new File("web"),
					File.class.getMethod("setWritable", boolean.class),
					new Object[] { true });
			FileUtil.getAllFiles(new File("web"),
					File.class.getMethod("setLastModified", long.class),
					new Object[] { new Date().getTime() });
			// 记录日志，info级别
			logger.info("file update successfully");
		} catch (NoSuchMethodException e2) {
			e2.printStackTrace();
		} catch (SecurityException e2) {
			e2.printStackTrace();
		}

		// 读入xml配置文件，生成根元素
		SAXReader reader = new SAXReader();
		try {
			userCfg = reader.read(new File("conf/userCfg.xml"));
			sysCfg = reader.read(new File("conf/sysCfg.xml"));
			protCfg = reader.read(new File("conf/protCfg.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		userRootElt = userCfg.getRootElement();
		sysRootElt = sysCfg.getRootElement();
		protRootElt = protCfg.getRootElement();

		// 读入系统配置
		indexPage = sysRootElt.elementTextTrim("index_page");
		errorPage = sysRootElt.elementTextTrim("error_page");
		defaultType = sysRootElt.elementTextTrim("default_type");
		shutdownPort = Integer.parseInt(sysRootElt
				.elementTextTrim("shutdown_port"));
		shutdownWord = sysRootElt.elementTextTrim("shutdown_word");
		version = sysRootElt.elementTextTrim("version");
		dataLenth = Integer.parseInt(sysRootElt.elementTextTrim("data_lenth"));
		webRoot = sysRootElt.elementTextTrim("web_root");
		confRoot = sysRootElt.elementTextTrim("conf_root");
		logsRoot = sysRootElt.elementTextTrim("logs_root");
		reqRegex = sysRootElt.elementTextTrim("req_regex");
		headerRegex = sysRootElt.elementTextTrim("header_regex");
		pathMatch = sysRootElt.elementTextTrim("path_match");
		rootMatch = sysRootElt.elementTextTrim("root_match");
		fullMatch = sysRootElt.elementTextTrim("full_match");
		// 记录日志，info级别
		logger.info("load system-configuration successfully");

		// 加载用户配置
		port = Integer.parseInt(userRootElt.elementTextTrim("port"));
		charset = userRootElt.elementTextTrim("charset");
		defaultProject = userRootElt.elementTextTrim("default_project");
		isCached = Boolean.valueOf(userRootElt.elementTextTrim("is_cached"));
		// 记录日志，info级别
		logger.info("load user-configuration successfully");

		// 读入协议配置——状态码表
		statusCodes = new HashMap<String, String[]>();
		List<?> statusNodes = protRootElt.element("status_codes").elements(
				"status");
		String key;
		String desc;
		String cnDesc;
		for (Object ele : statusNodes) {
			key = ((Element) ele).attribute("key").getText();
			desc = ((Element) ele).attribute("desc").getText();
			cnDesc = ((Element) ele).attribute("cnDesc").getText();
			statusCodes.put(key, new String[] { desc, cnDesc });
		}
		// 读入协议配置——内容类型表
		contentTypes = new HashMap<String, String>();
		List<?> typeNodes = protRootElt.element("content_types").elements(
				"content_type");
		String suffix;
		String type;
		for (Object ele : typeNodes) {
			suffix = ((Element) ele).attribute("suffix").getText();
			type = ((Element) ele).attribute("type").getText();
			contentTypes.put(suffix, type);
		}
		// 记录日志，info级别
		logger.info("load protocol-configuration successfully");

		// 读取错误页
		try {
			errorEntity = new StringBuffer();
			FileReader fr = new FileReader(webRoot + errorPage);
			char[] data = new char[dataLenth];
			for (int read; (read = fr.read(data)) > -1;) {
				errorEntity.append(data, 0, read);
			}
			fr.close();
			// 记录日志，info级别
			logger.info("load error-page successfully");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置关闭守护端口 程序自动调用，用于指定端口号不存在，系统自动分配
	 * 
	 * @param port
	 */
	public static void setShutdwonPort(int port) {
		sysRootElt.element("shutdown_port").setText(String.valueOf(port));
		writeSysDocument();
	}

	/**
	 * 设置web服务端口
	 * 
	 * @param port
	 */
	public static void setPort(int port) {
		userRootElt.element("port").setText(String.valueOf(port));
	}

	/**
	 * 设置字符集
	 * 
	 * @param charset
	 */
	public static void setCharset(String charset) {
		userRootElt.element("charset").setText(String.valueOf(charset));
	}

	/**
	 * 设置默认项目
	 * 
	 * @param deaultProject
	 */
	public static void setDeaultProject(String deaultProject) {
		userRootElt.element("default_project").setText(
				String.valueOf(deaultProject));
	}

	/**
	 * 设置是否启用浏览器缓存
	 * 
	 * @param isCached
	 */
	public static void setIsCached(boolean isCached) {
		userRootElt.element("is_cached").setText(String.valueOf(isCached));
	}

	/**
	 * 保存系统配置文件
	 * 
	 * @return
	 */
	public static boolean writeSysDocument() {
		return writeDocument(Constants.sysCfg, "sysCfg.xml");
	}

	/**
	 * 保存用户配置文件
	 * 
	 * @return
	 */
	public static boolean writeUserDocument() {
		return writeDocument(Constants.userCfg, "userCfg.xml");
	}

	/**
	 * 保存配置文件实现
	 * 
	 * @return
	 */
	public static boolean writeDocument(Document document, String fileName) {
		boolean flag = false;
		try {
			// 创建文件输出的时候，自动缩进的格式
			OutputFormat format = OutputFormat.createPrettyPrint();
			// 设置编码
			format.setEncoding("UTF-8");
			XMLWriter writer = new XMLWriter(
					new FileWriter("conf/" + fileName), format);
			writer.write(document);
			writer.close();
			flag = true;
			// 记录日志，info级别
			logger.info("write configuration " + fileName + " successfully");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 获取用户配置实现
	 * 
	 * @return
	 */
	public static Map<String, String> getUserCfg() {
		Map<String, String> cfg = new HashMap<String, String>();
		List<?> cfgNodes = userRootElt.elements();
		String key;
		String value;
		for (Object node : cfgNodes) {
			Element ele = (Element) node;
			key = ele.getName();
			value = ele.getTextTrim();
			cfg.put(key, value);
		}
		return cfg;
	}

}
