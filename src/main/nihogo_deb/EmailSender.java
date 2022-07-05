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
    /** stream for getting host parameters */
    private FileInputStream fileInputStreamHostProps;

    /** stream for getting authorization parameters */
    private FileInputStream fileInputStreamAuthProps;

    /** stores parameters about the host */
    private final Properties propertiesHostProps = new Properties();

    /** stores parameters about the authorization */
    private final Properties propertiesAuthProps = new Properties();

    /** contains the subject, text, recipient, and message receipt parameters */
    private Message message;

    /** session like connection */
    private final Session session;

    /** regular expressions for email validation */
    private final static String REGEX_EMAIL_VALIDATOR = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    /** constructor without params */
    public EmailSender() {
        try {
            fileInputStreamHostProps = new FileInputStream("src/main/resources/host-props.properties");
            fileInputStreamAuthProps = new FileInputStream("src/main/resources/sender-auth-props.properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            propertiesHostProps.load(fileInputStreamHostProps);
            propertiesAuthProps.load(fileInputStreamAuthProps);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStreamHostProps.close();
                fileInputStreamAuthProps.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        session = Session.getDefaultInstance(propertiesHostProps, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        propertiesAuthProps.getProperty("mail.user"),
                        propertiesAuthProps.getProperty("mail.password"));
            }
        });
    }


    /**
     * create new message in EmailSender object
     * @param emailAddress - recipient's email address
     * @param subject - subject of the message
     * @param innerText - message text
     */
    public Message createMessage(String emailAddress, String subject, String innerText) {
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(propertiesAuthProps.getProperty("mail.user")));
            InternetAddress[] internetAddresses;
            if (validateEmail(emailAddress))
                internetAddresses = new InternetAddress[] {new InternetAddress(emailAddress)};
            else return null;
            message.setRecipients(Message.RecipientType.TO, internetAddresses);
            message.setSubject(subject);
            message.setText(innerText);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return message;
    }

    /** sending a message to recipients or the recipient */
    public void sendMessage() {
        try {
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /** method for validation email addressees */
    public static boolean validateEmail (String email){
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