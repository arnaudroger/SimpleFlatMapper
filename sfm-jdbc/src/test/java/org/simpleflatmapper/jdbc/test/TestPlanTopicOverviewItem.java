package org.simpleflatmapper.jdbc.test;

import org.simpleflatmapper.test.beans.TestPlanBase;

public class TestPlanTopicOverviewItem {

	private TestPlanBase testPlan;

    private String topicName;

    public TestPlanBase getTestPlan() {
		return testPlan;
	}

	public void setTestPlan(TestPlanBase testPlan) {
		this.testPlan = testPlan;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

}
