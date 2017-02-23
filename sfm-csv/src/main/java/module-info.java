module simpleflatmapper.csv {
        requires transitive simpleflatmapper.map;
        requires transitive simpleflatmapper.tuple;
        exports org.simpleflatmapper.csv;
        exports org.simpleflatmapper.csv.property;
}