package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.util.ListCollector;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Issue710Test {


    @Test
    public void testAbstractDiscriminatorIssue710() throws Exception {
        JdbcMapper<TestPlanTopicOverviewItem> mapper = JdbcMapperFactory.newInstance()
                .discriminator(TestPlanBase.class).with(TestPlanDwh.class)
                .newMapper(TestPlanTopicOverviewItem.class);

        List<TestPlanTopicOverviewItem> topicOverviewItems =
                mapper.forEach(setUpAbstractResultSetMock(), new ListCollector<TestPlanTopicOverviewItem>()).getList();

        assertEquals(3, topicOverviewItems.size());
        assertEquals("topic1", topicOverviewItems.get(0).topicName);
        assertEquals("topic2", topicOverviewItems.get(1).topicName);
        assertEquals("topic1", topicOverviewItems.get(2).topicName);

        assertEquals("testPlanUUID1", topicOverviewItems.get(0).testPlan.testPlanUuid);
        assertEquals("testPlanUUID1", topicOverviewItems.get(1).testPlan.testPlanUuid);
        assertEquals("testPlanUUID2", topicOverviewItems.get(2).testPlan.testPlanUuid);

        assertEquals("groupId1", topicOverviewItems.get(0).testPlan.groupId);
        assertEquals("groupId1", topicOverviewItems.get(1).testPlan.groupId);
        assertEquals("groupId1", topicOverviewItems.get(2).testPlan.groupId);

        assertEquals("artifactId1", topicOverviewItems.get(0).testPlan.artifactId);
        assertEquals("artifactId1", topicOverviewItems.get(1).testPlan.artifactId);
        assertEquals("artifactId1", topicOverviewItems.get(2).testPlan.artifactId);
    }
    public static class TestPlanTopicOverviewItem {

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

    public static abstract class TestPlanBase {

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

    public static class TestPlanDwh extends TestPlanBase
    {
    }


    private ResultSet setUpAbstractResultSetMock() throws SQLException {
        ResultSet rs = mock(ResultSet.class);

        ResultSetMetaData metaData = mock(ResultSetMetaData.class);

        final String[] columns = new String[] { "topicName", "test_plan_id", "test_plan_test_plan_uuid", "test_plan_group_id", "test_plan_artifact_id"};

        when(metaData.getColumnCount()).thenReturn(columns.length);
        when(metaData.getColumnLabel(anyInt())).then(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                return columns[-1 + (Integer)invocationOnMock.getArguments()[0]];
            }
        });

        when(rs.getMetaData()).thenReturn(metaData);

        final AtomicInteger ai = new AtomicInteger();

        final Object[][] rows = new Object[][]{
                {"topic1", 1, "testPlanUUID1", "groupId1", "artifactId1"},
                {"topic2", 1, "testPlanUUID1", "groupId1", "artifactId1"},
                {"topic1", 1, "testPlanUUID2", "groupId1", "artifactId1"},
        };

        when(rs.next()).then(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
                return ai.getAndIncrement() < rows.length;
            }
        });
        final Answer<Object> getValue = new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                final Object[] row = rows[ai.get() - 1];
                final Integer col = -1 + (Integer) invocationOnMock.getArguments()[0];
                return (row[col]);
            }
        };

        final Answer<Object> getColumnValue = new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                final Object[] row = rows[ai.get() - 1];
                final String col = (String) invocationOnMock.getArguments()[0];
                return (row[Arrays.asList(columns).indexOf(col)]);
            }
        };

        when(rs.getInt(anyInt())).then(getValue);
        when(rs.getString(anyInt())).then(getValue);
        when(rs.getString(any(String.class))).then(getColumnValue);
        when(rs.getObject(anyInt())).then(getValue);
        when(rs.getObject(anyString(), any(Class.class))).then(getColumnValue);

        return rs;
    }
}
