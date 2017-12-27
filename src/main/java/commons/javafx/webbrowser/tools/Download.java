package commons.javafx.webbrowser.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class Download implements Runnable{
	private final Logger logger = Logger.getLogger(getClass().getName());

	private URL url;
	
	private Label label;
	
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

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			int r = conn.getResponseCode();
			if (r==HttpURLConnection.HTTP_OK) {
				String userHome = System.getProperty("user.home", "");
				if (userHome.length()>0)
					userHome += "/";
				userHome += "Downloads/";
				String filename = getFilename(conn);
				File file = getUniqueFile(userHome, filename);
				labelText = file.getName();
				updateLabelText(labelText + " ...");
				
				InputStream in = conn.getInputStream();
				FileOutputStream out = new FileOutputStream(file);
				FileUtil.copy(in, out);
				out.close();
				in.close();
				conn.disconnect();
				
				updateLabelText(labelText + " - ok");
				logger.info("download completed: "+file.getName());
			}
			else {
				// download failed
				logger.severe("download failed: rc="+r);
				updateLabelText(labelText + " - error");
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "download()", e);
			updateLabelText(labelText + " - error");
		}
	}

	private File getUniqueFile(String userHome, String filename) {
		File file = new File(userHome, filename);
		int counter = 0;
		while (file.exists()) {
			counter++;
			file = new File(userHome, "("+counter+")"+filename);
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
