module org.simpleflatmapper.csv {
        requires transitive org.simpleflatmapper.map;
        requires transitive org.simpleflatmapper.tuple;
        requires transitive org.simpleflatmapper.lightningcsv;
        exports org.simpleflatmapper.csv;
        exports org.simpleflatmapper.csv.property;
        exports org.simpleflatmapper.csv.getter;
}