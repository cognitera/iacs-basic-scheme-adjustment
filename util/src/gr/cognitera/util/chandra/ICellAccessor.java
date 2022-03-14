package gr.cognitera.util.chandra;

import java.util.List;

public interface ICellAccessor {

    int numberOfFields();
    int numberOfRows();
    List<String> row(int row);

}
