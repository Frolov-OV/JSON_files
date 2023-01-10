package classesJSON;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Test {
    private int id;
    private String title;
    private String value;
    private List<Test> values;
}
