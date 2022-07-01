import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @author KAWAIISHY
 * @project BirthdayNotificationer
 * @created 30.06.2022
 */
public class EmailSender {
    private FileInputStream fileInputStreamSendProps;
    private FileInputStream fileInputStreamUserProps;
    private Properties sendProperties = new Properties();
    private Properties userProperties = new Properties();
    private Message message;
    private Session session;
    private final String REGEX_EMAIL_VALIDATOR = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static EmailSender emailSender = new EmailSender();

    private EmailSender() {
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

    public void createMessage(String emailAddress, String subject, String innerText) {
        message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(userProperties.getProperty("mail.user")));
            InternetAddress[] internetAddresses;
            if (validateEmail(emailAddress))
                internetAddresses = new InternetAddress[] {new InternetAddress(emailAddress)};
            else return;
            message.setRecipients(Message.RecipientType.TO, internetAddresses);
            message.setSubject(subject);
            message.setText(innerText);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage() {
        try {
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static EmailSender getEmailSender() {
        return emailSender;
    }

    public boolean validateEmail (String email){
        if (email == null || email.isEmpty()){
            System.out.println("email is invalid");
            return false;
        }
        Pattern pattern = Pattern.compile(REGEX_EMAIL_VALIDATOR);
        if (pattern.matcher(email).matches()){
            System.out.println("email is valid");
            return true;
        } else {
            System.out.println("email is invalid");
            return false;
        }
    }
}