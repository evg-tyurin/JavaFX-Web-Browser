package commons.javafx.webbrowser.tools;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
/**
 * Support for BASIC HTTP authorization.
 * Custom Authenticator which uses a dialog window to request the user for login and password.
 * The dialog is shown for protected sites only which are read from properties file.
 * 
 * @author evg.tyurin
 *
 */
public final class MyAuthenticator extends Authenticator {
	private Logger logger = Logger.getLogger("MyAuthenticator");

	private List<URI> protectedURI;
	
	public MyAuthenticator() {
		protectedURI = new ArrayList<>();
		ResourceBundle b = ResourceBundle.getBundle("protected-sites");
		int count = Integer.parseInt(b.getString("sites.count"));
		for (int i = 1; i <= count; i++) {
			String url = b.getString("site."+i+".url");
			protectedURI.add(URI.create(url));
		}
	}

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		if (isProtected(getRequestingHost())) {
			logger.info("use password for "+getRequestingURL());
			PasswordAuthentication pa = new PassAuthDialog().getPasswordAuthentication();
			return pa;
		}
		return null;
	}
	
	private boolean isProtected(String host) {
		for (URI uri : protectedURI) {
			if (uri.getHost().equals(host))
				return true;
		}
		return false;
	}

}// class
/**
 * Dialog with login and password text fields.
 * 
 * @author evg.tyurin
 */
final class PassAuthDialog {

	private PasswordAuthentication passwordAuthentication;
	
	private boolean ready;

	public PasswordAuthentication getPasswordAuthentication() {
		Runnable r = new Runnable() {

			public void run() {
				try {
					Dialog<PasswordAuthentication> dialog = getDialog();

					Optional<PasswordAuthentication> result = dialog.showAndWait();

					if (result.isPresent()) {
						passwordAuthentication = result.get();
					}
					else {
						passwordAuthentication = null;
					}
				}
				finally {
					ready = true;	
				}
			}
		};
		Platform.runLater(r);
		while(!ready) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return passwordAuthentication;
	}
	
	private Dialog<PasswordAuthentication> getDialog() {
		// Create the custom dialog.
		Dialog<PasswordAuthentication> dialog = new Dialog<>();
		dialog.setTitle("Login Dialog");
		dialog.setHeaderText("Login Dialog");

		// Set the icon (must be included in the project).
//		dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

		// Set the button types.
		ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField username = new TextField();
		username.setPromptText("Username");
		PasswordField password = new PasswordField();
		password.setPromptText("Password");

		grid.add(new Label("Username:"), 0, 0);
		grid.add(username, 1, 0);
		grid.add(new Label("Password:"), 0, 1);
		grid.add(password, 1, 1);

		// Enable/Disable login button depending on whether a username was entered.
		Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
		loginButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		username.textProperty().addListener((observable, oldValue, newValue) -> {
		    loginButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(grid);

		// Request focus on the username field by default.
		Platform.runLater(() -> username.requestFocus());
		
		// Convert the result to a username-password-pair when the login button is clicked.
		dialog.setResultConverter(dialogButton -> {
		    if (dialogButton == loginButtonType) {
		        return new PasswordAuthentication(username.getText(), password.getText().toCharArray());
		    }
		    return null;
		});

		return dialog;

	}
}
