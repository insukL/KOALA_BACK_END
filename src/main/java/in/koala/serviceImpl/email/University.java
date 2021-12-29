package in.koala.serviceImpl.email;

import in.koala.enums.EmailType;
import in.koala.service.email.EmailService;

public class University implements EmailService {
    @Override
    public EmailType getEmailType() {
        return EmailType.UNIVERSITY;
    }
}
