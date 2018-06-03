package org.psc.pomversionmanipulation.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VersionManipulatorApp extends Application {
	private static final Logger LOGGER = LoggerFactory.getLogger(VersionManipulatorApp.class);

	@FXML
	Stage primaryStage;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/VersionManipulator.fxml"));
		primaryStage = stage;
		stage.setTitle("Version Manipulator");
		stage.setScene(new Scene(root, 1280, 768));
		stage.show();

	}

}
