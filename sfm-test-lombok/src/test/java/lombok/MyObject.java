package lombok;

@Data(staticConstructor = "of")
public class MyObject {

    private final long id;
    private final String name;

}
