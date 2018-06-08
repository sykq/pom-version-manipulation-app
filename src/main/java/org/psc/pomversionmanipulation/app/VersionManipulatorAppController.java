package org.psc.pomversionmanipulation.app;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang3.StringUtils;
import org.psc.pomversionmanipulation.app.model.PomPath;
import org.psc.pomversionmanipulation.app.model.PomPathTreeItemBuilder;
import org.psc.pomversionmanipulation.app.model.PomPathTreeItemBuilderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class VersionManipulatorAppController implements Initializable {
	private static final Logger LOGGER = LoggerFactory.getLogger(VersionManipulatorAppController.class);

	@FXML
	private MenuItem menuItemNew;
	@FXML
	private MenuItem menuItemEdit;

	@FXML
	private Stage primaryStage;

	@FXML
	private TreeView<PomPath> directoryView;

	@FXML
	private Label rootDirectoryLabel;

	private Path rootPath;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		directoryView.setEditable(true);
		// directoryView.setCellFactory(PomPathCheckBoxTreeCell.forTreeView());
		directoryView.setCellFactory(e -> new PomPathCheckBoxTreeCell());
		directoryView.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				CheckBoxTreeItem<PomPath> item = (CheckBoxTreeItem<PomPath>) directoryView.getSelectionModel()
						.getSelectedItem();
				item.setSelected(!item.isSelected());
			}
		});
	}

	@FXML
	public void addItem() {
	}

	@FXML
	public void openDirectoryChooser(ActionEvent event) throws IOException, XPathExpressionException,
			PomPathTreeItemBuilderException, ParserConfigurationException, SAXException {
		LOGGER.info(event.getSource().toString());
		LOGGER.info("test");
		DirectoryChooser dirChooser = new DirectoryChooser();
		File root = dirChooser.showDialog(null);

		LOGGER.info(root.getPath());
		rootPath = Paths.get(root.getPath());
		rootDirectoryLabel.setText(rootPath.toString());
		listSubdirectoriesWithPoms();
	}

	@FXML
	public void listSubdirectoriesWithPoms() throws IOException, XPathExpressionException,
			PomPathTreeItemBuilderException, ParserConfigurationException, SAXException {
		CheckBoxTreeItem<PomPath> root = new PomPathTreeItemBuilder(rootPath).build();
		directoryView.setRoot(root);
	}

	@FXML
	public void exit() {
		LOGGER.info("app closed {}", LocalDateTime.now().toString());
		System.exit(0);
	}

	private class PomPathCheckBoxTreeCell extends CheckBoxTreeCell<PomPath> {

		private SplitPane content;
		private PomPath pomPath;

		@Override
		public void updateItem(PomPath item, boolean empty) {
			super.updateItem(item, empty);

			if (isEmpty()) {
				setText(null);
				setGraphic(null);
			} else {
				content = new SplitPane();
				content.setOrientation(Orientation.HORIZONTAL);

				pomPath = item;

				final NodeContentSpecification setNodeContentResult = setNodeContent();
				setText(setNodeContentResult.getText());
				/*
				 * if (setNodeContentResult.isPom) { this.setTextFill(new Color(0.5, 0.5, 0.5,
				 * 1)); } else { this.setTextFill(Color.BLACK); }
				 */
				// setGraphic(content);
			}

		}

		private NodeContentSpecification setNodeContent() {
			ObservableList<Label> items = FXCollections.observableArrayList();
			items.add(new Label(pomPath.toString()));

			StringBuilder pomInfoBuilder = new StringBuilder();

			final String groupId = pomPath.getGroupId();
			final String artifactId = pomPath.getArtifactId();
			final String version = pomPath.getVersion();
			final boolean isPom = pomPath.getPath().endsWith("pom.xml");

			if (StringUtils.isNotBlank(groupId) && StringUtils.isNotBlank(artifactId)) {
				pomInfoBuilder.append('[').append(groupId).append(", ").append(artifactId);

				if (StringUtils.isNotBlank(version)) {
					pomInfoBuilder.append(", ").append(version);
				}

				pomInfoBuilder.append(']');
			}

			final String pomInfo = pomInfoBuilder.toString();
			items.add(new Label(pomInfo));
			content.getItems().clear();
			content.getItems().addAll(items);

			StringBuilder textBuilder = new StringBuilder().append(pomPath.toString());
			if (StringUtils.isNotBlank(pomInfo)) {
				textBuilder.append(' ').append(pomInfo);
			}

			return new NodeContentSpecification(textBuilder.toString(), isPom);
		}
	}

	private class NodeContentSpecification {
		private String text;
		private boolean isPom;

		public NodeContentSpecification(String text, boolean isPom) {
			this.text = text;
			this.isPom = isPom;
		}

		/**
		 * @return the text
		 */
		public String getText() {
			return text;
		}

		/**
		 * @param text
		 *            the text to set
		 */
		public void setText(String text) {
			this.text = text;
		}

		/**
		 * @return the isPom
		 */
		public boolean isPom() {
			return isPom;
		}

		/**
		 * @param isPom
		 *            the isPom to set
		 */
		public void setPom(boolean isPom) {
			this.isPom = isPom;
		}

	}

}
