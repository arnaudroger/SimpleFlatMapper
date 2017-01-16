package org.simpleflatmapper.poi.test;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.poi.SheetMapper;
import org.simpleflatmapper.poi.SheetMapperFactory;
import org.simpleflatmapper.test.jdbc.JoinTest;
import org.simpleflatmapper.test.beans.ProfessorGS;
import org.simpleflatmapper.util.ListCollector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//IFJAVA8_START
import java.util.stream.Collectors;
//IFJAVA8_END

public class JoinSheetMapperTest {

    Sheet joinSheet;

    SheetMapper<ProfessorGS> joinSheetMapper;

    @Before
    public void setUp(){
        Workbook wb = new HSSFWorkbook();
        joinSheet = wb.createSheet();


        for(int i = 0; i < JoinTest.ROWS.length; i++) {
            Object[] orow = JoinTest.ROWS[i];
            Row row = joinSheet.createRow(i);
            for(int j = 0; j < orow.length; j++) {
                Cell cell = row.createCell(j);
                if(orow[j] != null) {
                    if (orow[j] instanceof String) {
                        cell.setCellValue((String)orow[j]);
                    } else {
                        cell.setCellValue((Integer)orow[j]);
                    }
                }
            }
        }

        joinSheetMapper =
            SheetMapperFactory
                .newInstance()
                .newBuilder(ProfessorGS.class)
                .addKey("id")
                .addMapping("name")
                .addKey("students_id")
                .addMapping("students_name")
                .addMapping("students_phones_value")
                .mapper();

    }

    @Test
    public void iteratorOnSheetFrom0WithStaticMapper() {
        Iterator<ProfessorGS> iterator = joinSheetMapper.iterator(joinSheet);

        List<ProfessorGS> list = new ArrayList<ProfessorGS>();

        while(iterator.hasNext()) {
            list.add(iterator.next());
        }

        JoinTest.validateProfessors(list);
    }

    @Test
    public void forEachOnSheetFrom0WithStaticMapper() {
        JoinTest.validateProfessors(
                joinSheetMapper
                        .forEach(
                                joinSheet,
                                new ListCollector<ProfessorGS>()
                        ).getList()
        );

    }


    //IFJAVA8_START
    @Test
    public void streamOnSheetFrom0WithStreamWithStaticMapper() {

        List<ProfessorGS> list = joinSheetMapper.stream(joinSheet).collect(Collectors.toList());
        JoinTest.validateProfessors(list);

    }
    //IFJAVA8_END

}
