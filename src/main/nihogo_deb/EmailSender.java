import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * @author KAWAIISHY
 * @project BirthdayNotificationer
 * @created 30.06.2022
 */
public class EmailSender {
    private static FileInputStream fileInputStreamSendProps;
    private static FileInputStream fileInputStreamUserProps;
    private static final Properties sendProperties = new Properties();
    private static final Properties userProperties = new Properties();
    private static Message message;
    private static Session session;

    private EmailSender(){
        try {
            fileInputStreamSendProps = new FileInputStream("src/main/resources/mail.properties");
            fileInputStreamUserProps = new FileInputStream("src/main/resources/user.properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            sendProperties.load(fileInputStreamSendProps);
            userProperties.load(fileInputStreamUserProps);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStreamSendProps.close();
                fileInputStreamUserProps.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        session = Session.getDefaultInstance(sendProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        userProperties.getProperty("mail.user"),
                        userProperties.getProperty("mail.password"));
            }
        });
    }

    public static void createMessage(String emailAddress, String subject, String innerText){
        message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(userProperties.getProperty("mail.user")));
            InternetAddress[] internetAddresses = {new InternetAddress(emailAddress)};
            message.setRecipients(Message.RecipientType.TO, internetAddresses);
            message.setSubject(subject);
            message.setText(innerText);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(){
        try {
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
