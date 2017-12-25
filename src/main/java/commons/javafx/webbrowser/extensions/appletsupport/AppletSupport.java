package commons.javafx.webbrowser.extensions.appletsupport;

import java.applet.Applet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import commons.javafx.webbrowser.extensions.StateListenerExtension;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.scene.control.Alert;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
/**
 * This browser extension supports Applet tag in HTML pages.
 * If the tag is presented on a page, applet is loaded and instantiated.
 * NB! The extension has limited support of applets.
 * Rendering Applet component on a page/screen is not supported.
 * Invoking Applet methods from Javascript is supported, thus OS integration in a browser independent way is supported.
 * 
 * <p>
 * Compatibility notice.
 * Current version of AppletSupport requires the following applet tag parameters <param/>
 * <li>jnlp_href - used for extracting root path of the applet jar,
 * <li>cache_archive - name of the jar file with applet locatd in a root path above,
 * <li>cache_version - version of the jar file, used for caching and reusing jar files,
 * <li>className - class name of the applet to be loaded.
 * </p>
 * 
 * @author evg.tyurin
 *
 */
public class AppletSupport implements StateListenerExtension {
	Logger logger = Logger.getLogger("AppletSupport");
	
	private WebEngine webEngine;

	@Override
	public void changed(ObservableValue<? extends State> observable, State oldState, State newState) {
        if (newState == State.SUCCEEDED) {
			try {
				// check presence an applet on page and load the applet, then share it as javascript object 'appletId'
				checkApplet();
			} catch (Exception e) {
				logger.log(Level.SEVERE, "checkApplet", e);
			}
        }//if SUCCEEDED
		
	}
	/** Check that applet tag exists at the current HTML page. If so, triggers applet creation. */
	private void checkApplet() {
		NodeList applets = webEngine.getDocument().getDocumentElement().getElementsByTagName("applet");
		if (applets.getLength()==0)
			return;
		JSObject win = (JSObject) webEngine.executeScript("window");
		Element appletElem = (Element) applets.item(0);
		
		try {
			Applet applet = AppletLoader.createApplet(appletElem);
			applet.init();
			applet.start();
			win.setMember(applet.getParameter("id"), applet);
			logger.info("Registered: ["+applet.getParameter("id")+"] for "+applet.getClass().getName());
		} catch (Exception e) {
			logger.log(Level.SEVERE, "loadApplet", e);
			msgbox("Ошибка при загрузке апплета");
		}
	}

	private void msgbox(String msg)
	{
		logger.info(msg);
		Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.showAndWait();
	}

	@Override
	public WebEngine getWebEngine() {
		return webEngine;
	}

	@Override
	public void setWebEngine(WebEngine webEngine) {
		this.webEngine = webEngine;
	}
}
