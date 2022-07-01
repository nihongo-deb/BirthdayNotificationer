import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author KAWAIISHY
 * @project BirthdayNotificationer
 * @created 30.06.2022
 */
public class RunClass {
    public static void main(String[] args) {
        EmailSender emailSender = EmailSender.getEmailSender();
        emailSender.createMessage("dal34@tpu.ru",
                "just title", "just text");
//        дима, не разкоменчивай плз
//        emailSender.sendMessage();
        emailSender.validateEmail("dal34@tpu.ru");
        emailSender.validateEmail("dal34@tpuru");
        emailSender.validateEmail("@@.rtyrty");
    }
}
