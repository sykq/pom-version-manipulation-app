package org.psc.pomversionmanipulation.app.model;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.function.Predicate;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.psc.pom.version.manipulation.engine.VersionManipulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBoxTreeItem;

public class PomPathTreeItemBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(PomPathTreeItemBuilder.class);

	private static final Predicate<Path> DEFAULT_INCLUSION_FILTER = p -> {
		// path is root OR path is a directory OR path ends with pom.xml <=> is a
		// pom.xml
		// return p.getNameCount() == 0 || Files.isDirectory(p) ||
		// p.endsWith("pom.xml");
		return p.endsWith("pom.xml");
	};
	private static final XPathFactory X_PATH_FACTORY = XPathFactory.newInstance();

	private Path root;
	private Predicate<Path> inclusionFilter;

	public PomPathTreeItemBuilder() {

	}

	public PomPathTreeItemBuilder(Path root) {
		setRoot(root);
	}

	public PomPathTreeItemBuilder(Path root, Predicate<Path> inclusionFilter) {
		setRoot(root).setInclusionPredicate(inclusionFilter);
	}

	public PomPathTreeItemBuilder setRoot(Path root) {
		this.root = root;
		return this;
	}

	public PomPathTreeItemBuilder setInclusionPredicate(Predicate<Path> inclusionPredicate) {
		this.inclusionFilter = inclusionPredicate;
		return this;
	}

	public CheckBoxTreeItem<PomPath> build() throws PomPathTreeItemBuilderException, XPathExpressionException,
			IOException, ParserConfigurationException, SAXException {
		if (root == null) {
			String message = "root (Path) not set";
			LOGGER.error(message);
			throw new PomPathTreeItemBuilderException(message);
		}
		if (inclusionFilter == null) {
			inclusionFilter = DEFAULT_INCLUSION_FILTER;
		}

		CheckBoxTreeItem<PomPath> rootItem = new CheckBoxTreeItem<>(new PomPath(root, "", "", ""));
		resolveChildrenPaths(root, rootItem);
		return rootItem;
	}

	private boolean resolveChildrenPaths(Path currentPath, CheckBoxTreeItem<PomPath> rootItem)
			throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
		DirectoryStream<Path> dirContent = Files.newDirectoryStream(currentPath);

		ObservableList<CheckBoxTreeItem<PomPath>> children = FXCollections.observableArrayList();

		boolean shouldBeIncluded = false;
		Iterator<Path> dirContentIterator = dirContent.iterator();

		while (dirContentIterator.hasNext()) {
			Path next = dirContentIterator.next();
			if (next.toString().contains("$")) {
				// skip special folders and files with $ in name -> otherwise some kind of
				// exception occurs
				continue;
			}

			if (inclusionFilter.test(next)) {
				children.add(new CheckBoxTreeItem<PomPath>(createPomPath(next)));
				shouldBeIncluded = true;
			} else if (Files.isDirectory(next)) {
				CheckBoxTreeItem<PomPath> child = new CheckBoxTreeItem<PomPath>(new PomPath(next, "", "", ""));
				if (resolveChildrenPaths(next, child)) {
					children.add(child);
				}
			}
		}

		if (children.size() > 0) {
			rootItem.getChildren().addAll(children);
			shouldBeIncluded = true;
		}

		return shouldBeIncluded;

	}

	private PomPath createPomPath(Path pomPath)
			throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
		Document document = VersionManipulator.buildDocumentFromPom(pomPath);

		String groupId = getTextWithXPath(document, "/project/groupId/text()");
		String artifactId = getTextWithXPath(document, "/project/artifactId/text()");
		String version = getTextWithXPath(document, "/project/version/text()");

		return new PomPath(pomPath, groupId, artifactId, version);
	}

	private String getTextWithXPath(Document document, String expression) throws XPathExpressionException {
		XPath xPath = X_PATH_FACTORY.newXPath();
		XPathExpression xPathExpression = xPath.compile(expression);
		return (String) xPathExpression.evaluate(document, XPathConstants.STRING);
	}

}
