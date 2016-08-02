package org.simpleflatmapper.lombok;

import lombok.Data;

@Data(staticConstructor = "of")
public class MyObject {

    private final long id;
    private final String name;

}
