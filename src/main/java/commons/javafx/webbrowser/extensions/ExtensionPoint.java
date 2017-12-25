package commons.javafx.webbrowser.extensions;

/**
 * Extension point is a point of either the browser initialization or other life cycle where the extension can be integrated. 
 * 
 * @author etyurin
 *
 */
public enum ExtensionPoint {
	
	/** Extensions of this type are webEngine state listeners and MUST implement appropriate interface. */ 
	STATE_LISTENER

}
