package org.simpleflatmapper.test.beans;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class TestPlanBase {

	private Long id;

	private String testPlanUuid;

	private String groupId;

	private String artifactId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTestPlanUuid() {
		return testPlanUuid;
	}

	public void setTestPlanUuid(String testPlanUuid) {
		this.testPlanUuid = testPlanUuid;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

}
