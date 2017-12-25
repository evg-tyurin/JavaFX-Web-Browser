package commons.javafx.webbrowser.extensions;

import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebEngine;
/**
 * Subclass of browser extensions which are webEngine state listeners.
 * @see ExtensionPoint#STATE_LISTENER
 * 
 * @author evg.tyurin
 *
 */
public interface StateListenerExtension extends Extension, ChangeListener<State>{

	WebEngine getWebEngine();
	
	void setWebEngine(WebEngine webEngine);
	
}
