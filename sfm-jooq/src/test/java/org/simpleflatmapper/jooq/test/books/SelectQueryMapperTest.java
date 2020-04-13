package org.simpleflatmapper.jooq.test.books;

import static org.junit.Assert.assertEquals;
import static org.simpleflatmapper.jooq.test.books.Author.AUTHOR;
import static org.simpleflatmapper.jooq.test.books.Book.BOOK;

import java.sql.Connection;
import java.util.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.sql.DataSource;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Test;
import org.simpleflatmapper.jooq.SelectQueryMapper;
import org.simpleflatmapper.jooq.SelectQueryMapperFactory;
import org.simpleflatmapper.test.jdbc.DbHelper;

public class SelectQueryMapperTest {

	private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Test
	public void testSelectQuery() throws SQLException {

		Connection conn = DbHelper.objectDb();

		Configuration cfg = new DefaultConfiguration()
				.set(conn)
				.set(SQLDialect.HSQLDB);


		DSLContext dsl = DSL.using(cfg);

		SelectQueryMapper<Label> mapper = SelectQueryMapperFactory.newInstance().newMapper(Label.class);

		UUID uuid1 = UUID.randomUUID();
		Label label = new Label(1, uuid1, "l1", false);

		dsl.insertInto(Labels.LABELS).columns(Labels.LABELS.ID, Labels.LABELS.NAME, Labels.LABELS.OBSOLETE, Labels.LABELS.UUID)
				.values(1, "l1", false, uuid1).execute();

		List<Label> labels = mapper.asList(dsl.select(Labels.LABELS.ID, Labels.LABELS.NAME, Labels.LABELS.OBSOLETE, Labels.LABELS.UUID).from(Labels.LABELS));

		assertEquals(1, labels.size());
		assertEquals(label, labels.get(0));


	}

	@Test
	public void testBooks() throws Exception {

		DataSource dataSource = DbHelper.getHsqlDataSource();

		try (Connection connection = dataSource.getConnection()) {

			initBookDb(connection);

			SelectQueryMapper<Author> authorMapper = SelectQueryMapperFactory.newInstance().newMapper(Author.class);

			List<Author> authors = authorMapper.asList(DSL.using(connection)
					.select(AUTHOR.ID, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME, AUTHOR.DATE_OF_BIRTH, AUTHOR.DATE_OF_DEATH,
							BOOK.ID, BOOK.TITLE)
					.from(AUTHOR).leftJoin(BOOK).on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
					.orderBy(AUTHOR.ID));


			assertEquals(2, authors.size());

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyy-MM-dd");
			assertEquals(
					new Author(1, "George", "Orwell", sdf.parse("1903-06-25"), LocalDate.parse("1950-01-21", DATE_FORMAT),
						Arrays.asList(
								new Book(1, "1984"),
								new Book(2, "Animal Farm")
						)
					), authors.get(0));
			assertEquals(
					new Author(2, "Paulo", "Coelho", sdf.parse("1947-08-24"), null,
						Arrays.asList(
								new Book(3, "O Alquimista"),
								new Book(4, "Brida")
						)
					), authors.get(1));
		}


	}

	private void initBookDb(Connection connection) throws SQLException {
		Statement st = connection.createStatement();

		st.execute("CREATE TABLE author (\n" +
				"  id INT NOT NULL,\n" +
				"  first_name VARCHAR(50),\n" +
				"  last_name VARCHAR(50) NOT NULL,\n" +
				"  date_of_birth DATE,\n" +
				"  date_of_death DATE,\n" +
				"\n" +
				"  CONSTRAINT pk_t_author PRIMARY KEY (ID)\n" +
				")");

		st.execute("CREATE TABLE book (\n" +
				"  id INT NOT NULL,\n" +
				"  author_id INT NOT NULL,\n" +
				"  title VARCHAR(400) NOT NULL,\n" +
				"  published_in INT,\n" +
				"\n" +
				"  rec_timestamp TIMESTAMP,\n" +
				"\n" +
				"  CONSTRAINT pk_t_book PRIMARY KEY (id),\n" +
				"  CONSTRAINT fk_t_book_author_id FOREIGN KEY (author_id) REFERENCES author(id),\n" +
				")\n" +
				";");
		st.execute("INSERT INTO author VALUES (1, 'George', 'Orwell', '1903-06-25', '1950-01-21')\n");
		st.execute("INSERT INTO author VALUES (2, 'Paulo', 'Coelho', '1947-08-24', null)\n");
		st.execute("INSERT INTO book VALUES (1, 1, '1984'        , 1948, null)\n");
		st.execute("INSERT INTO book VALUES (2, 1, 'Animal Farm' , 1945, null)\n");
		st.execute("INSERT INTO book VALUES (3, 2, 'O Alquimista', 1988, null)\n");
		st.execute("INSERT INTO book VALUES (4, 2, 'Brida'       , 1990, null)\n");
	}


	public static class Author {
		public final int id;
		public final String firstName;
		public final String lastName;
		public final Date dateOfBirth;
		public final LocalDate dateOfDeath;

		public final List<Book> books;

		public Author(int id, String firstName, String lastName, Date dateOfBirth, LocalDate dateOfDeath, List<Book> books) {
			this.id = id;
			this.firstName = firstName;
			this.lastName = lastName;
			this.dateOfBirth = dateOfBirth;
			this.dateOfDeath = dateOfDeath;
			this.books = books;
		}


		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Author author = (Author) o;

			if (id != author.id) return false;
			if (firstName != null ? !firstName.equals(author.firstName) : author.firstName != null) return false;
			if (lastName != null ? !lastName.equals(author.lastName) : author.lastName != null) return false;
			if (dateOfBirth != null ? !dateOfBirth.equals(author.dateOfBirth) : author.dateOfBirth != null)
				return false;
			if (dateOfDeath != null ? !dateOfDeath.equals(author.dateOfDeath) : author.dateOfDeath != null)
				return false;
			return books != null ? books.equals(author.books) : author.books == null;
		}

		@Override
		public int hashCode() {
			int result = id;
			result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
			result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
			result = 31 * result + (dateOfBirth != null ? dateOfBirth.hashCode() : 0);
			result = 31 * result + (dateOfDeath != null ? dateOfDeath.hashCode() : 0);
			result = 31 * result + (books != null ? books.hashCode() : 0);
			return result;
		}

		@Override
		public String toString() {
			return "Author{" +
					"id=" + id +
					", firstName='" + firstName + '\'' +
					", dateOfBirth=" + dateOfBirth +
					", dateOfDeath=" + dateOfDeath +
					", books=" + books +
					'}';
		}
	}

	public static class Book {
		public final int id;
		public final String title;

		public Book(int id, String title) {
			this.id = id;
			this.title = title;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Book book = (Book) o;

			if (id != book.id) return false;
			return title != null ? title.equals(book.title) : book.title == null;
		}

		@Override
		public int hashCode() {
			int result = id;
			result = 31 * result + (title != null ? title.hashCode() : 0);
			return result;
		}

		@Override
		public String toString() {
			return "Book{" +
					"id=" + id +
					", title='" + title + '\'' +
					'}';
		}
	}
}
