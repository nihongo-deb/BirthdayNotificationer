import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.util.ArrayList;

public class Tester {

    public static void main(String[] args) throws IOException
    {
        XLSReader reader = new XLSReader();
        System.out.println(reader.createOutput());
    }
}
