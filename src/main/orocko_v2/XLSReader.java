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

    /**
     * Public method to get access from other classes
     * @return message which will be sent
     * @throws IOException if we can not access to the Excel file
     */
   public String createOutput() throws IOException
   {
       Workbook workbook = XLSReader.getWorkbook("C:\\Users\\ДИМА\\Documents\\GitHub\\BirthdayNotificationer\\BirthdaysInfo.xls");
       ArrayList<String> birthdayList = XLSReader.getPersons(workbook);
       String message = XLSReader.createMessage(birthdayList);
       return message;
   }

   /**
    * Returns workbook depending on file format
    * @param path where the file is stored
    * @throws IOException if we can not access the file
    * @return workbook representation of Excel file
    * */
    private static Workbook getWorkbook(String path) throws IOException
    {
        // Получаем доступ к Excel-файлу
        File birthdayInfoFile = new File(path);
        FileInputStream fis = new FileInputStream(birthdayInfoFile);
        String type = new Tika().detect(birthdayInfoFile);
        switch(type)
        {
            case ("application/vnd.ms-excel"):
                return new HSSFWorkbook(fis);
            case ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"):
                return new XSSFWorkbook(fis);
        }
        return null;
    }

    /**
     * Get persons whose birthdays will be tomorrow
     * @param workbook representation of Excel file where birthdays data stored
     * @return ArrayList<String> with persons whose birthdays will be tomorrow
     */
    private static ArrayList<String> getPersons(Workbook workbook)
    {
        // Получаем лист именинников
        Sheet sheet = workbook.getSheetAt(0);
        // Получаем дату завтрашнего дня
        Date date = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Calendar currentDateCalendar = new GregorianCalendar();
        Calendar additionalCalendar = new GregorianCalendar();
        currentDateCalendar.setTime(date);
        boolean is29February = false;
        if (check29February()!=null)
        {
            // Если сегодня 28.02 и завтра не 29.02, то создаем дополнительную дату
            // 01.03 поздравляем как родившихся 01.03, так и родившихся 29.02
            additionalCalendar = check29February();
            is29February = true;
        }
        if(is29February)
        {
            return fillList(sheet, currentDateCalendar, additionalCalendar);
        }
        else
        {
            return fillList(sheet, currentDateCalendar);
        }
    }

    /**
     * Creates a message about tomorrow's birthdays
     * @param birthdayList array of persons whose birthdays will be tomorrow
     * @return message which will be sent
     */
    private static String createMessage(ArrayList<String> birthdayList)
    {
        // Составляем уведомление, содержащее имена именинников
        MessageFormat form = messageFormat(birthdayList.size());
        if(form == null)
        {
            return null;
        }
        String names = "";
        for (int i = 0; i< birthdayList.size(); i++)
        {
            names += birthdayList.get(i) + ", ";
        }
        names = names.substring(0, names.length() - 2);
        Object[] args = {names};
        return form.format(args);
    }

    /**
     * Checks if today is 28th February and non-leap year
     * @return calendar which store a date of 29th February
     */
    private static Calendar check29February()
    {
        // Если сегодня 28.02, а завтра не 29.02 то возвращаем дату 29.02
        Calendar calendar = new GregorianCalendar();
        Calendar tomorrow = new GregorianCalendar();
        calendar.setTime(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        tomorrow.setTime(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        if(calendar.get(Calendar.DAY_OF_MONTH) == 28 && calendar.get(Calendar.MONTH) == Calendar.FEBRUARY && tomorrow.get(Calendar.DAY_OF_MONTH) != 29 && tomorrow.get(Calendar.MONTH) != Calendar.FEBRUARY)
        {
            calendar.setTime(new Date(2020, Calendar.FEBRUARY, 29));
            return calendar;
        }
        else return null;
    }

    /**
     * Change message format depending on amount of tomorrow's birthdays
     * @param size amount of birthdays tomorrow
     * @return format of the message
     */
    private static MessageFormat messageFormat(int size)
    {
        // Изменение содержания сообщения в зависимости от количества именинников
        MessageFormat form = new MessageFormat("");
        switch (size)
        {
            case (0):
                return null;
            case (1):
                form = new MessageFormat("Уважаемые коллеги! Завтра свой день рождения празднует {0}!");
                break;
            default:
                form = new MessageFormat("Уважаемые коллеги! Завтра свои дни рождений празднуют {0}!");
                break;
        }
        return form;
    }

    /**
     * Fills birthday list if we don't have to take into account 29th February
     * @param sheet page in Excel with birthdays data
     * @param currentDateCalendar store information about tomorrow's date
     * @return list of person whose birthdays will be tomorrow
     */
    private static ArrayList<String> fillList(Sheet sheet, Calendar currentDateCalendar)
    {
        ArrayList<String> birthdayList = new ArrayList<>();
        for (Row row: sheet)
        {
            Calendar birthdayDateCalendar = new GregorianCalendar();
            birthdayDateCalendar.setTime(row.getCell(1).getDateCellValue());
            // Получаем дату рождения сотруднника и сравниваем с нашими датами
            if(birthdayDateCalendar.get(Calendar.DAY_OF_MONTH) == currentDateCalendar.get(Calendar.DAY_OF_MONTH) && birthdayDateCalendar.get(Calendar.MONTH) == currentDateCalendar.get(Calendar.MONTH) )
            {
                // Если ДР сотрудника совпадает с завтрашней датой, записываем его имя
                birthdayList.add(row.getCell(0).getStringCellValue());
            }
        }
        return birthdayList;
    }

    /**
     * Fills birthday list if we have to take into account 29th February
     * @param sheet page in Excel with birthdays data
     * @param currentDateCalendar store information about tomorrow's date
     * @param additionalCalendar store 29th February date
     * @return list of person whose birthdays will be tomorrow
     */
    private static ArrayList<String> fillList(Sheet sheet, Calendar currentDateCalendar, Calendar additionalCalendar)
    {
        ArrayList<String> birthdayList = new ArrayList<>();
        for (Row row: sheet)
        {
            Calendar birthdayDateCalendar = new GregorianCalendar();
            birthdayDateCalendar.setTime(row.getCell(1).getDateCellValue());
            // Получаем дату рождения сотруднника и сравниваем с нашими датами
            if(birthdayDateCalendar.get(Calendar.DAY_OF_MONTH) == currentDateCalendar.get(Calendar.DAY_OF_MONTH) && birthdayDateCalendar.get(Calendar.MONTH) == currentDateCalendar.get(Calendar.MONTH) )
            {
                // Если ДР сотрудника совпадает с завтрашней датой, записываем его имя
                birthdayList.add(row.getCell(0).getStringCellValue());
            }
            if(birthdayDateCalendar.get(Calendar.DAY_OF_MONTH) == additionalCalendar.get(Calendar.DAY_OF_MONTH) && birthdayDateCalendar.get(Calendar.MONTH) == additionalCalendar.get(Calendar.MONTH) )
            {
                // Если ДР сотрудника 29 февраля, год не високосный, и завтра 01.03, то поздравим его тоже
                birthdayList.add(row.getCell(0).getStringCellValue());
            }
        }
        return birthdayList;
    }
}
