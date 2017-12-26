package commons.javafx.webbrowser.browser;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.scene.control.ComboBox;
/**
 * Search engine combo box and its utility methods.
 * 
 * @author evg.tyurin@gmail.com
 * @author GOXR3PLUS
 *
 */
public class SearchEngineComboBox extends ComboBox<Engine>{
	private List<Engine> engines = new ArrayList<>();
	{
		ResourceBundle b = ResourceBundle.getBundle("search-engines");
		int count = Integer.parseInt(b.getString("engines.count"));
		for (int i = 1; i <= count; i++) {
			String name = b.getString("engine."+i+".name");
			String url = b.getString("engine."+i+".url");
			engines.add(new Engine(name, url));
		}

	}
	/**
	 * Return the Search Url for the Search Provider For example for `Google` returns `https://www.google.com/search?q=`
	 * 
	 * @param searchProvider
	 * @return The Search Engine Url
	 */
	public String getSearchEngineHomeUrl(Engine searchProvider) {
		return searchProvider.url;
	}

	public String getSelectedEngineHomeUrl() {
		Engine searchProvider = getSelectionModel().getSelectedItem();
		return getSearchEngineHomeUrl(searchProvider);
	}

	public void init() {
		getItems().addAll(engines);
		getSelectionModel().select(0);		
	}

}
class Engine{
	String name;
	String url;
	public Engine(String name, String url) {
		this.name = name;
		this.url = url;
	}
	@Override
	public String toString() {
		return name;
	}
	
}