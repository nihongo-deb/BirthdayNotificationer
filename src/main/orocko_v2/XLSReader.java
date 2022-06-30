import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.tika.Tika;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class XLSReader {

    public static void main(String[] args) throws IOException {
        Workbook workbook = XLSReader.getWorkbook("C:\\Users\\ДИМА\\Documents\\GitHub\\BirthdayNotificationer\\BirthdaysInfo.xls");
        ArrayList<String> birthdayList = XLSReader.getPersons(workbook);
        XLSReader.createMessage(birthdayList);

    }

    private static Workbook getWorkbook(String path) throws IOException
    {
        //path = "C:\\Users\\ДИМА\\Documents\\GitHub\\BirthdayNotificationer\\BirthdaysInfo.xls";


        File BirthdayInfoFile = new File(path);
        FileInputStream fis = new FileInputStream(BirthdayInfoFile);
        String type = new Tika().detect(BirthdayInfoFile);
        switch(type)
        {
            case ("application/vnd.ms-excel"):
                return new HSSFWorkbook(fis);
            case ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"):
                return new XSSFWorkbook(fis);
        }
        return null;
    }

    private static ArrayList<String> getPersons(Workbook workbook) throws IOException
    {
        Sheet sheet = workbook.getSheetAt(0);
        ArrayList<String> birthdayList = new ArrayList<>();

        Date date = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Calendar currentDateCalendar = new GregorianCalendar();
        currentDateCalendar.setTime(date);

        for (Row row: sheet)
        {
            Calendar birthdayDateCalendar = new GregorianCalendar();
            birthdayDateCalendar.setTime(row.getCell(1).getDateCellValue());
            if(birthdayDateCalendar.get(Calendar.DAY_OF_MONTH) == currentDateCalendar.get(Calendar.DAY_OF_MONTH) && birthdayDateCalendar.get(Calendar.MONTH) == currentDateCalendar.get(Calendar.MONTH) )
            {

                birthdayList.add(row.getCell(0).getStringCellValue());
            }
        }
        return birthdayList;
    }

    private static void createMessage(ArrayList<String> birthdayList)
    {
        MessageFormat form = new MessageFormat("Уважаемые коллеги! Завтра свой день рождения празднует {0}!");
        String name = "";

        for (int i = 0; i< birthdayList.size(); i++)
        {
            name = birthdayList.get(i);
            Object[] args = {name};
            String message = form.format(args);
            System.out.println(message);
        }
    }


}
