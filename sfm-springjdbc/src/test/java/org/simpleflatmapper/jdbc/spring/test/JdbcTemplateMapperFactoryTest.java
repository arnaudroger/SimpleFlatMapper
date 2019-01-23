package org.simpleflatmapper.jdbc.spring.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.simpleflatmapper.jdbc.spring.PreparedStatementCallbackImpl;
import org.simpleflatmapper.jdbc.spring.ResultSetExtractorImpl;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.util.ListCollector;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public class JdbcTemplateMapperFactoryTest {
	
	JdbcTemplate template;
	
	@Before
	public void setUp() throws SQLException {
		template = new JdbcTemplate(new SingleConnectionDataSource(DbHelper.objectDb(), true));
	}

	@Test
	public void testRowMapper() throws SQLException, ParseException  {
		RowMapper<DbObject> mapper = JdbcTemplateMapperFactory.newInstance().newRowMapper(DbObject.class);
		List<DbObject> results = template.query(DbHelper.TEST_DB_OBJECT_QUERY, mapper);
		DbHelper.assertDbObjectMapping(results.get(0));
	}
	
	@Test
	public void testPreparedStatementCallback() throws SQLException, ParseException  {
		PreparedStatementCallback<List<DbObject>> mapper = JdbcTemplateMapperFactory.newInstance().newPreparedStatementCallback(DbObject.class);
		List<DbObject> results = template.execute(DbHelper.TEST_DB_OBJECT_QUERY, mapper);
		DbHelper.assertDbObjectMapping(results.get(0));
	}

	@Test
	public void testResultSetExtractor() throws SQLException, ParseException  {
		ResultSetExtractor<List<DbObject>> mapper = JdbcTemplateMapperFactory.newInstance().newResultSetExtractor(DbObject.class);
		List<DbObject> results = template.query(DbHelper.TEST_DB_OBJECT_QUERY, mapper);
		DbHelper.assertDbObjectMapping(results.get(0));
	}

	@Test
	public void testPreparedStatementCallbackWithHandler() throws SQLException, ParseException  {
		PreparedStatementCallbackImpl<DbObject> mapper = JdbcTemplateMapperFactory.newInstance().newPreparedStatementCallback(DbObject.class);
		List<DbObject> results = 
			template
				.execute(DbHelper.TEST_DB_OBJECT_QUERY, 
						mapper.newPreparedStatementCallback(new ListCollector<DbObject>())).getList();
		DbHelper.assertDbObjectMapping(results.get(0));
	}
	@Test
	public void testResultSetExtractorWithHandler() throws SQLException, ParseException  {
		ResultSetExtractorImpl<DbObject> mapper = JdbcTemplateMapperFactory.newInstance().newResultSetExtractor(DbObject.class);
		List<DbObject> results = 
			template
				.query(DbHelper.TEST_DB_OBJECT_QUERY, 
						mapper.newResultSetExtractor(new ListCollector<DbObject>())).getList();
		DbHelper.assertDbObjectMapping(results.get(0));
	}



	@Test
	public void testIssue483IgnorePropertyNotFound()  {
		ResultSetExtractor<List<ThinDbObject>> mapper = 
				JdbcTemplateMapperFactory.newInstance().ignorePropertyNotFound().newResultSetExtractor(ThinDbObject.class);
		List<ThinDbObject> results = template.query(DbHelper.TEST_DB_OBJECT_QUERY, mapper);
		Assert.assertEquals(1, results.get(0).getId());
		Assert.assertEquals("name 1", results.get(0).getName());
	}
	
	
	@Test
	public void testIssue606() throws SQLException {
		Connection dbConnection = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
		
		if (dbConnection == null) return;
		JdbcTemplate 		template = new JdbcTemplate(new SingleConnectionDataSource(dbConnection, true));

		ResultSetExtractor<List<Prof>> mapper = JdbcTemplateMapperFactory.newInstance().addKeys("id", "students_id").unorderedJoin().newResultSetExtractor(Prof.class);
		
		
		List<Prof> results = template.query("SELECT 1 as id , 'p1' as name, 1 as students_id, 's1' as students_name, 1::bit as students_test ", mapper);

		System.out.println("results = " + results);


	}


	public static class Prof {
		Long id;
		 String name;
		  List<Student> students;

		public Prof(Long id, String name, List<Student> students) {
			this.id = id;
			this.name = name;
			this.students = students;
		}

		public Long getId() {
			return id;
		}


		public String getName() {
			return name;
		}


		public List<Student> getStudents() {
			return students;
		}


		@Override
		public String toString() {
			return "Prof{" +
					"id=" + id +
					", name='" + name + '\'' +
					", students=" + students +
					'}';
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Prof prof = (Prof) o;

			if (id != prof.id) return false;
			if (name != null ? !name.equals(prof.name) : prof.name != null) return false;
			return students != null ? students.equals(prof.students) : prof.students == null;
		}

		@Override
		public int hashCode() {
			int result = (int) (id ^ (id >>> 32));
			result = 31 * result + (name != null ? name.hashCode() : 0);
			result = 31 * result + (students != null ? students.hashCode() : 0);
			return result;
		}
	}

	public static class Student {
		Long id;
		String name;
		boolean test;


		public Student(Long id, String name, boolean test) {
			this.id = id;
			this.name = name;
			this.test = test;
		}

		public Long getId() {
			return id;
		}


		public String getName() {
			return name;
		}


		public boolean getTest() {
			return test;
		}


		@Override
		public String toString() {
			return "Student{" +
					"id=" + id +
					", name='" + name + '\'' +
					", test='" + test + '\'' +
					'}';
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Student student = (Student) o;

			if (id != student.id) return false;
			if (test != student.test) return false;
			return name != null ? name.equals(student.name) : student.name == null;
		}

		@Override
		public int hashCode() {
			int result = (int) (id ^ (id >>> 32));
			result = 31 * result + (name != null ? name.hashCode() : 0);
			result = 31 * result + (test ? 1 : 0);
			return result;
		}
	}
	
	public static class ThinDbObject {
		private long id;
		private String name;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
