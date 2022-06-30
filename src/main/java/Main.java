import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author KAWAIISHY
 * @project BirthdayNotificationer
 * @created 30.06.2022
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("bullSheet");

        FileOutputStream fileOutputStream = new FileOutputStream("bull.xls");
        workbook.write(fileOutputStream);
        fileOutputStream.close();
    }
}
