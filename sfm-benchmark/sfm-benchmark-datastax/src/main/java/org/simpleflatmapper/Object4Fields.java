package org.simpleflatmapper;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(keyspace = "test", name = "test_table")
public class Object4Fields {
        @PartitionKey
        private long id;

        @Column(name = "year_started")
        private int yearStarted;
        private String name;
        private String email;

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
        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }

        public int getYearStarted() {
            return yearStarted;
        }
        public void setYearStarted(int yearStarted) {
            this.yearStarted = yearStarted;
        }

    @Override
    public String toString() {
        return "Object4Fields{" +
                "id=" + id +
                ", yearStarted=" + yearStarted +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
