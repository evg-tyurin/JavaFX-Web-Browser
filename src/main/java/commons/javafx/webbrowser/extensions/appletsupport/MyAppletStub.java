package commons.javafx.webbrowser.extensions.appletsupport;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.net.URL;
import java.util.HashMap;
/**
 * Simple applet stub required by the process of applet loading.
 * 
 * @author evg.tyurin
 *
 */
public class MyAppletStub implements AppletStub {
	
	private HashMap<String, String> params = new HashMap<>();

	@Override
	public void appletResize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public AppletContext getAppletContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getCodeBase() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getDocumentBase() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParameter(String name) {
		return params.get(name);
	}

	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void setParameter(String name, String value){
		params.put(name, value);
	}

}
