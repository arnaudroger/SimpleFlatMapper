/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.simpleflatmapper;

import com.datastax.driver.core.*;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.thrift.transport.TTransportException;
import org.openjdk.jmh.annotations.*;
import org.sfm.datastax.DatastaxMapper;
import org.sfm.datastax.DatastaxMapperFactory;
import org.sfm.utils.ListHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

/*
Benchmark                          Mode  Cnt   Score   Error  Units
DatastaxBenchmark.datastaxMapper  thrpt   20  22.120 ± 0.233  ops/s
DatastaxBenchmark.sfmMapper       thrpt   20  29.294 ± 0.438  ops/s
Benchmark                         Mode  Cnt  Score    Error  Units
DatastaxBenchmark.datastaxMapper  avgt   20  0.045 ±  0.001   s/op
DatastaxBenchmark.sfmMapper       avgt   20  0.034 ±  0.001   s/op

 */
@State(Scope.Benchmark)
public class DatastaxBenchmark {

    Cluster cluster;
    Session session;

    Mapper<Object4Fields> datastaxMapper;

    DatastaxMapper<Object4Fields> sfmMapper;
     PreparedStatement preparedStatement;

    @Setup
    public void setUp() throws InterruptedException, TTransportException, ConfigurationException, IOException {
        cluster =
                Cluster
                        .builder()
                        .addContactPointsWithPorts(
                                Arrays.asList(new InetSocketAddress("localhost", 9042)))
                        .build();

        if (cluster.getMetadata().getKeyspace("testsfm") == null) {
            cluster.connect().execute("create keyspace testsfm WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 }");
        }

        this.session = cluster.connect("testsfm");

        this.session.execute("create table if not exists test_table  (id bigint primary key, year_started int, name varchar, email varchar)");



        if (this.session.execute("select * from test_table").isExhausted()) {
            for( int i  = 0; i < 10000; i++) {
                this.session.execute("insert into test_table(id, year_started, name, email) values (" + i + ", 1978, 'Arnaud Roger', 'arnaud.roger@gmail.com')");
            }
        }

        datastaxMapper = new MappingManager(this.session).mapper(Object4Fields.class);
        sfmMapper = DatastaxMapperFactory.newInstance().mapTo(Object4Fields.class);

        preparedStatement = this.session.prepare("select id, year_started, name, email from test_table ");

    }

    @TearDown
    public void tearDown() {
        if (session != null) {
            session.close();
        }
        if (cluster != null) {
            cluster.close();
        }
    }

    @Benchmark
    public List<Object4Fields> datastaxMapper() {
        return datastaxMapper.map(getResultSet()).all();
    }

    public ResultSet getResultSet() {
        return session.execute(new BoundStatement(preparedStatement));
    }


    @Benchmark
    public List<Object4Fields> sfmMapper() {
        return sfmMapper.forEach(getResultSet(), new ListHandler<Object4Fields>()).getList();
    }

    public static void main(String[] args) throws InterruptedException, TTransportException, ConfigurationException, IOException {
        final DatastaxBenchmark datastaxBenchmark = new DatastaxBenchmark();

        datastaxBenchmark.setUp();
        try {
            System.out.println(datastaxBenchmark.datastaxMapper());
            System.out.println(datastaxBenchmark.sfmMapper());
        } finally {

            datastaxBenchmark.tearDown();
        }

    }

}
