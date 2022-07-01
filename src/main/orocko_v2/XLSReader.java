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

    public static void main(String[] args) throws IOException
    {
        Workbook workbook = XLSReader.getWorkbook("C:\\Users\\ДИМА\\Documents\\GitHub\\BirthdayNotificationer\\BirthdaysInfo.xls");
        ArrayList<String> birthdayList = XLSReader.getPersons(workbook);
        XLSReader.createMessage(birthdayList);
    }

    private static Workbook getWorkbook(String path) throws IOException
    {
        // Получаем доступ к Excel-файлу
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
        // Получаем лист именинников
        Sheet sheet = workbook.getSheetAt(0);
        ArrayList<String> birthdayList = new ArrayList<>();

        // Получаем дату завтрашнего дня
        Date date = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Calendar currentDateCalendar = new GregorianCalendar();
        Calendar additionalCalendar = new GregorianCalendar();
        currentDateCalendar.setTime(date);
        boolean is29February = false;
        if (check29February()!=null && currentDateCalendar.get(Calendar.DAY_OF_MONTH) != 29 && currentDateCalendar.get(Calendar.MONTH) != Calendar.FEBRUARY)
        {
            // Если сегодня 28.02 и завтра не 29.02, то создаем дополнительную дату
            // 01.03 поздравляем как родившихся 01.03, так и родившихся 29.02
            Date additionalDate = check29February();
            additionalCalendar.setTime(additionalDate);
            is29February = true;
        }

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
            if(is29February)
            {
                if(birthdayDateCalendar.get(Calendar.DAY_OF_MONTH) == additionalCalendar.get(Calendar.DAY_OF_MONTH) && birthdayDateCalendar.get(Calendar.MONTH) == additionalCalendar.get(Calendar.MONTH) )
                {
                    // Если ДР сотрудника 29 февраля, год не високосный, и завтра 01.03, то поздравим его тоже
                    birthdayList.add(row.getCell(0).getStringCellValue());
                }
            }
        }
        return birthdayList;
    }

    private static void createMessage(ArrayList<String> birthdayList)
    {
        // Составляем уведомление, содержащее имена именинников
        MessageFormat form = messageFormat(birthdayList.size());
        if(form == null)
        {
            return;
        }

        String names = "";

        for (int i = 0; i< birthdayList.size(); i++)
        {
            names += birthdayList.get(i) + ", ";

        }
        names = names.substring(0, names.length() - 2);
        Object[] args = {names};
        String message = form.format(args);
        System.out.println(message);
    }

    private static Date check29February()
    {
        // Если сегодня 28.02, то возвращаем дату 29.02
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        if(calendar.get(Calendar.DAY_OF_MONTH) == 28 && calendar.get(Calendar.MONTH) == 1)
        {

            return new Date(2020, Calendar.FEBRUARY, 29);
        }
        else return null;
    }

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


}
