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
    public static void main(String[] args) throws MessagingException, IOException {
        FileInputStream fileInputStreamSendProps = new FileInputStream("src/main/resources/mail.properties");
        FileInputStream fileInputStreamUserProps = new FileInputStream("src/main/resources/user.properties");

        Properties sendProperties = new Properties();
        Properties userProperties = new Properties();

        sendProperties.load(fileInputStreamSendProps);
        userProperties.load(fileInputStreamUserProps);

        System.out.println(sendProperties);
        System.out.println(userProperties);

        Session session = Session.getDefaultInstance(sendProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        userProperties.getProperty("mail.user"),
                        userProperties.getProperty("mail.password"));
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(userProperties.getProperty("mail.user")));
        InternetAddress[] internetAddresses = {new InternetAddress("dal34@tpu.ru")};
        message.setRecipients(Message.RecipientType.TO, internetAddresses);
        message.setSubject("2 mail");
        message.setText("test -1");
        Transport.send(message);
    }
}
