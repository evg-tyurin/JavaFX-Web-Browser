package commons.javafx.webbrowser.extensions.appletsupport;

import java.applet.Applet;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.logging.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Loads applet jar file and instantiate the applet.
 * 
 * @author evg.tyurin
 *
 */
public class AppletLoader {

	static Logger logger = Logger.getLogger("Browser");
	
	/** 
	 * Jar files already loaded from the network.
	 * Every aplet is loaded in its own ClassLoader. 
	 */
	private static HashMap<String, ClassLoader> appletClassloaders = new HashMap<>();

//	private static HashMap<String, Applet> applets = new HashMap<>();
	
	/** Adds the file to the classpath */
	public static void addSoftwareLibrary(File file) throws Exception {
		addSoftwareLibrary(file.toURI().toURL());
	}

	/** 
	 * Adds the file to the classpath 
	 * @return new classLoader for the jar file  
	 */
	public static URLClassLoader addSoftwareLibrary(URL file) throws Exception {
		// for java 9
		URLClassLoader classLoader = new URLClassLoader(new URL[] {file}, ClassLoader.getSystemClassLoader());
		return classLoader;
		// for java 8 and earlier
//		Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
//		method.setAccessible(true);
//		method.invoke(ClassLoader.getSystemClassLoader(), new Object[] { file });
	}

	/** 
	 * Loads jar file from the URL.
	 *
	 * @param clsName class name of the applet
	 * @param jarUrl URL of the jar file
	 * @param jarVersion version of the jar file
	 * @return applet instance
	 * @throws Exception
	 */
	public static Applet loadApplet(String clsName, String jarUrl, String jarVersion) throws Exception {
		String key = jarUrl+"$"+jarVersion;
		ClassLoader classLoader = appletClassloaders.get(key);
		if (classLoader==null){
			classLoader = AppletLoader.addSoftwareLibrary(new URL(jarUrl));
			appletClassloaders.put(key, classLoader);
			logger.info("applet loaded: "+jarUrl);
		}
		else{
			logger.info("applet already loaded: "+jarUrl);
		}
		Applet app = (Applet) classLoader.loadClass(clsName).newInstance();
		return app;
	}

	/** 
	 * Instantiates the applet from the applet tag of the HTML page.
	 * @param applet tag from the HTML page with all params and attributes
	 */
	public static Applet createApplet(Element applet) throws Exception {
		String appletId = applet.getAttribute("id");
		logger.info("applet{"+appletId+"} detected");
		
		String jnlpHref = getParamValue(applet,"jnlp_href");
		String jar = getParamValue(applet, "cache_archive");
		String jarVersion = getParamValue(applet, "cache_version");
		String clsName = getParamValue(applet, "className");
		
		String jarUrl = jnlpHref.substring(0, jnlpHref.lastIndexOf("/"))+"/"+jar;

		Applet app = AppletLoader.loadApplet(clsName, jarUrl, jarVersion);
		
		MyAppletStub stub = new MyAppletStub();
		stub.setParameter("id", appletId);
		NodeList params = applet.getElementsByTagName("param");
		for (int i = 0; i < params.getLength(); i++) {
			Element item = (Element) params.item(i);
			String name = item.getAttribute("name");
			String value = item.getAttribute("value");
			stub.setParameter(name, value);
		}
		app.setStub(stub);
		return app;

	}
	private static String getParamValue(Element e, String paramName) {
		NodeList params = e.getElementsByTagName("param");
		for (int i = 0; i < params.getLength(); i++) {
			Element item = (Element) params.item(i);
			if (item.getAttribute("name").equals(paramName))
				return item.getAttribute("value");
		}
		return null;
	}

}
