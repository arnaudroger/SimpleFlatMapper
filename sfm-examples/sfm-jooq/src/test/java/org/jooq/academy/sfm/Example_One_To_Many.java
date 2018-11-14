package org.jooq.academy.sfm;

import static org.jooq.academy.tools.Tools.connection;
import static org.jooq.example.db.h2.Tables.AUTHOR;
import static org.jooq.example.db.h2.Tables.BOOK;
import static org.jooq.example.db.h2.Tables.BOOK_TO_BOOK_STORE;

import org.jooq.academy.tools.Tools;
import org.jooq.example.db.h2.tables.records.AuthorRecord;
import org.jooq.example.db.h2.tables.records.BookRecord;
import org.jooq.example.db.h2.tables.records.BookToBookStoreRecord;
import org.jooq.impl.DSL;

import org.jooq.lambda.tuple.Tuple2;
import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.util.TypeReference;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Example_One_To_Many {

    @Test
    public void authorsAndBooksQuery() throws SQLException {

        // All we need to execute a query is provide it with a connection and then
        // call fetch() on it.
        Tools.title("Selecting authorsAndBooks");
        
         Tools.print(DSL.using(connection())
                 .select(AUTHOR.ID, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME, AUTHOR.DATE_OF_BIRTH,
                         BOOK.ID, BOOK.TITLE)
                 .from(AUTHOR).leftJoin(BOOK).on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
                 .orderBy(AUTHOR.ID).fetch());

    }

    @Test
    public void authorsAndBooks() throws SQLException {

        // All we need to execute a query is provide it with a connection and then
        // call fetch() on it.
        Tools.title("Selecting authorsAndBooks");

        JdbcMapper<Tuple2<AuthorRecord, List<BookRecord>>> mapper = JdbcMapperFactory.newInstance()
                .addKeys("id").newMapper(new TypeReference<Tuple2<AuthorRecord, List<BookRecord>>>() {
                });
        try (ResultSet rs =
                     DSL.using(connection())
                .select(AUTHOR.ID, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME, AUTHOR.DATE_OF_BIRTH,
                        BOOK.ID, BOOK.TITLE)
                .from(AUTHOR).leftJoin(BOOK).on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
                .orderBy(AUTHOR.ID).fetchResultSet()) {
            mapper.stream(rs).forEach(Tools::print);

        }
    }

    @Test
    public void authorsAndBooksAndBookStoreQuery() throws SQLException {

        // All we need to execute a query is provide it with a connection and then
        // call fetch() on it.
        Tools.title("Selecting authorsAndBooksAndBookStore");


        Tools.print(DSL.using(connection())
                .select(AUTHOR.ID, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME, AUTHOR.DATE_OF_BIRTH,
                        BOOK.ID, BOOK.TITLE,
                        BOOK_TO_BOOK_STORE.BOOK_STORE_NAME, BOOK_TO_BOOK_STORE.STOCK)
                .from(AUTHOR)
                .leftJoin(BOOK).on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
                .leftJoin(BOOK_TO_BOOK_STORE).on(BOOK_TO_BOOK_STORE.BOOK_ID.eq(BOOK.ID))
                .orderBy(AUTHOR.ID).fetch());

    }

    @Test
    public void authorsAndBooksAndBookStore() throws SQLException {

        // All we need to execute a query is provide it with a connection and then
        // call fetch() on it.
        Tools.title("Selecting authorsAndBooksAndBookStore");

        JdbcMapper<
                Tuple2< AuthorRecord,
                        List<Tuple2<BookRecord, List<BookToBookStoreRecord>>>
                >
            > mapper =
                JdbcMapperFactory.newInstance()
                .addKeys("ID", "BOOK_STORE_NAME").newMapper(new TypeReference<Tuple2<AuthorRecord, List<Tuple2<BookRecord, List<BookToBookStoreRecord>>>>>() {
                });

        try (ResultSet rs =
                     DSL.using(connection())
                             .select(AUTHOR.ID, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME, AUTHOR.DATE_OF_BIRTH,
                                     BOOK.ID, BOOK.TITLE,
                                     BOOK_TO_BOOK_STORE.BOOK_STORE_NAME, BOOK_TO_BOOK_STORE.STOCK)
                             .from(AUTHOR)
                                .leftJoin(BOOK).on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
                                .leftJoin(BOOK_TO_BOOK_STORE).on(BOOK_TO_BOOK_STORE.BOOK_ID.eq(BOOK.ID))
                             .orderBy(AUTHOR.ID).fetchResultSet()) {
            mapper.stream(rs).forEach(Tools::print);

        }
    }
}
