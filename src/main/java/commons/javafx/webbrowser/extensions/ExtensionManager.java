package commons.javafx.webbrowser.extensions;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
/**
 * Web browser extensions manager.
 * @see ExtensionPoint
 * 
 * @author evg.tyurin
 * 
 */
public class ExtensionManager {

	public List<Extension> getExtensions(ExtensionPoint point) {
		try {
			List<Extension> list = new ArrayList<>();
			ResourceBundle b = ResourceBundle.getBundle("extensions/extensions");
			int count = Integer.parseInt(b.getString("extensions.count"));
			for (int i = 1; i <= count; i++) {
				String type = b.getString("extension."+i+".type");
				String className = b.getString("extension."+i+".className");
				if (!type.equals(point.toString()))
					continue;
				Extension extension = (Extension) Class.forName(className).newInstance();
				list.add(extension);
			}
			return list;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

}
