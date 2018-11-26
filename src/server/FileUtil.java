package server;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FileUtil {

	/**
	 * 遍历文件夹下所有文件及文件夹，统一执行method方法，参数为args
	 * 
	 * @param dir
	 * @param method
	 * @param args
	 */
	public static void getAllFiles(File dir, Method method, Object[] args) {
		try {
			method.invoke(dir, args);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		if (!dir.isDirectory())
			return;
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			getAllFiles(files[i], method, args);
		}
	}

}
