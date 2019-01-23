package org.simpleflatmapper.poi.test;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.simpleflatmapper.poi.SheetMapper;
import org.simpleflatmapper.poi.SheetMapperFactory;
import org.simpleflatmapper.util.ListCollector;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class UnorderedJoinSheetMapper605Test {


    @Test
    public void test(){
        Workbook wb = new HSSFWorkbook();
        Sheet joinSheet = wb.createSheet();


        Object[][] ROWS =new Object[][]{
                {1l, "prof1", 1l, "S1"},
                {2l, "prof2", 3l, "S3"},
                {1l, "prof1", 2l, "S2"},
                {2l, "prof2", 4l, "S4"},
                {3l, "prof3", 4l, "S4"},
                {3l, "prof3", 4l, "S4"},
        };
        for(int i = 0; i < ROWS.length; i++) {
            Object[] orow = ROWS[i];
            Row row = joinSheet.createRow(i);
            for(int j = 0; j < orow.length; j++) {
                Cell cell = row.createCell(j);
                if(orow[j] != null) {
                    if (orow[j] instanceof String) {
                        cell.setCellValue((String)orow[j]);
                    } else {
                        cell.setCellValue((Long)orow[j]);
                    }
                }
            }
        }

        SheetMapper<Prof> joinSheetMapper =
            SheetMapperFactory
                .newInstance()
                .unorderedJoin()
                .newBuilder(Prof.class)
                .addKey("id")
                .addMapping("name")
                .addKey("students_id")
                .addMapping("students_name")
                .mapper();


        List<Prof> profs = joinSheetMapper.forEach(joinSheet, new ListCollector<Prof>()).getList();

        assertEquals(Arrays.asList(
                new Prof(1l, "prof1", Arrays.asList(new Student(1l, "S1"), new Student(2l, "S2"))),
                new Prof(2l, "prof2", Arrays.asList(new Student(3l, "S3"), new Student(4l, "S4"))),
                new Prof(3l, "prof3", Arrays.asList(new Student(4l, "S4")))
        ), profs);
    }


    public static class Prof {
        public final long id;
        public final String name;
        public final List<Student> students;

        public Prof(long id, String name, List<Student> students) {
            this.id = id;
            this.name = name;
            this.students = students;
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
        public final long id;
        public final String name;

        public Student(long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return "Student{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Student student = (Student) o;

            if (id != student.id) return false;
            return name != null ? name.equals(student.name) : student.name == null;
        }

        @Override
        public int hashCode() {
            int result = (int) (id ^ (id >>> 32));
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }
    }
}
