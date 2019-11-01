package org.simpleflatmapper.reflect.test;

import org.junit.Test;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;

import java.util.List;

public class Issue667Test {
    @Test
    public void failing() {
        ClassMeta<Root> classMeta = ReflectionService.newInstance().getClassMeta(Root.class);

        System.out.println("classMeta = " + classMeta);
    }

    public class Root {
        private String id;
        private List<Foo> foos;

        public Root() {}
        public Root(final String id, final List<Foo> foos) {
            this.id = id;
            this.foos = foos;
        }
    }

    class Foo {
        private String id;

        public Foo() {}
        public Foo(final String id) {
            this.id = id;
        }
    }
}
