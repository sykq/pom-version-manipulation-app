package org.psc.pomversionmanipulation.app.model;

import java.nio.file.Path;

public class PomPath implements Comparable<PomPath> {
	private Path path;
	private String groupId;
	private String artifactId;
	private String version;
	private boolean selected = true;

	public PomPath(Path path, String groupId, String artifactId, String version) {
		this.path = path;
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}

	public PomPath(Path path, String groupId, String artifactId, String version, boolean selected) {
		this(path, groupId, artifactId, version);
		this.selected = selected;
	}

	/**
	 * @return the path
	 */
	public Path getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(Path path) {
		this.path = path;
	}

	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId
	 *            the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * @return the artifactId
	 */
	public String getArtifactId() {
		return artifactId;
	}

	/**
	 * @param artifactId
	 *            the artifactId to set
	 */
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected
	 *            the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String toString() {
		final String name;
		if (path.getNameCount() > 0) {
			name = path.getName(Math.max(0, path.getNameCount() - 1)).toString();
		} else {
			name = path.toString();
		}
		return name;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public int compareTo(PomPath o) {
		return path.compareTo(o.getPath());
	}

}
