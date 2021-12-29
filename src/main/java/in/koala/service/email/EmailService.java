package in.koala.service.email;

import in.koala.enums.EmailType;

public interface EmailService {
    EmailType getEmailType();
    void sendEmail();
}
