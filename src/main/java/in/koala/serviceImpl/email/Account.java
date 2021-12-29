package in.koala.serviceImpl.email;

import in.koala.enums.EmailType;
import in.koala.service.email.EmailService;

public class Account implements EmailService {
    @Override
    public EmailType getEmailType() {
        return EmailType.ACCOUNT;
    }
}
