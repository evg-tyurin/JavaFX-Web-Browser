package commons.javafx.webbrowser.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;

import javafx.application.Platform;
import javafx.scene.control.Label;

/**
 * Скачивает файл из указанного адреса.
 * Если скачанный файл это JNLP, запускает приложение. 
 * 
 * @author evg.tyurin
 *
 */
public class Download implements Runnable{
	private final Logger logger = Logger.getLogger(getClass().getName());

	private URL url;
	
	private Label label;
	
	private String contentType;
	
	public Download(String url, Label label) throws MalformedURLException {
		this.url = new URL(url);
		this.label = label;
	}
	
	@Override
	public void run() {
		String labelText = "Downloading";
		try {
			labelText = getFilename(url);
			updateLabelText(labelText + " ...");
			
			File dir = new File(System.getProperty("user.home", ""), "Downloads/");

			File file = downloadFile(url, dir);

			updateLabelText(labelText + " - ok");
			logger.info("download completed: "+file.getName());

			if (contentType.startsWith("application/x-java-jnlp-file")) {
				launchJnlpFile(file);
			}
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, "download()", e);
			updateLabelText(labelText + " - error");
		}
	}

	private void launchJnlpFile(File file) {
		try {
			Document xml = XmlUtils.getDomDocument(file);
			String codebase = XmlUtils.getConfigParam(xml, "/jnlp/@codebase");
			String jar = XmlUtils.getConfigParam(xml, "/jnlp/resources/jar/@href");
			String mainClass = XmlUtils.getConfigParam(xml, "/jnlp/application-desc/@main-class");
			String argument = XmlUtils.getConfigParam(xml, "/jnlp/application-desc/argument/text()");
			
			String jarUrl = codebase+"/"+jar;
			logger.info("jnlp.jarUrl = "+jarUrl);
			logger.info("jnlp.main = "+mainClass);
			logger.info("jnlp.argument = "+argument);
			
			File dir = new File(System.getProperty("java.io.tmpdir", ".")+"/");
			File jarfile = downloadFile(new URL(jarUrl), dir);
			
			Runtime.getRuntime().exec(new String[] {
					"java", "-Xmx128M", "-cp", jarfile.getAbsolutePath(), mainClass, argument
			});

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Can't launch JNLP file", e);
			throw new RuntimeException(e);
		}
	}

	private File downloadFile(URL url, File dir)
			throws IOException, MalformedURLException, FileNotFoundException 
	{
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		int r = conn.getResponseCode();
		if (r!=HttpURLConnection.HTTP_OK)
			throw new IOException("Can't download file: HTTP="+r);
		
		this.contentType = conn.getContentType();

		String filename = getFilename(conn);
		File file = getUniqueFile(dir, filename);
		
		InputStream in = conn.getInputStream();
		FileOutputStream out = new FileOutputStream(file);
		FileUtil.copy(in, out);
		out.close();
		in.close();
		conn.disconnect();
		return file;
	}

	private File getUniqueFile(File dir, String filename) {
		File file = new File(dir, filename);
		int counter = 0;
		while (file.exists()) {
			counter++;
			file = new File(dir, "("+counter+")"+filename);
		}
		return file;
	}

	private String getFilename(HttpURLConnection conn) {
		String filename = null;
		String contentDisposition = conn.getHeaderField("Content-Disposition");
		if (contentDisposition!=null) {
			int start = contentDisposition.indexOf("filename=");
			if (start>=0) {
				filename = contentDisposition.substring(start+"filename=".length());
				filename = filename.replace('"', ' ').trim();
			}
		}
		if (filename==null) {
			filename = getFilename(conn.getURL());
		}
		if (filename==null) {
			filename = "file.dat";
		}
		return filename;
	}

	private String getFilename(URL url) {
		String path = url.getPath();
		int start = path.lastIndexOf("/");
		if (start>=0) {
			path = path.substring(start+1);
		}
		if (!path.isEmpty())
			return path;
		return url.toString().replaceAll("[\\:\\?/\\\\]+", "_");
	}
	
	private void updateLabelText(String text) {
		logger.info("request label update: "+text);
		Platform.runLater(() -> {
			logger.info("actually update label: "+text);
			label.setText(text);
		});

	}
}
