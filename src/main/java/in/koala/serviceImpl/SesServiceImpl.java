package in.koala.serviceImpl;

import in.koala.domain.test.SampleMail;
import in.koala.service.SesService;
import in.koala.util.SesSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.annotation.Resource;

@Transactional
@Service
public class SesServiceImpl implements SesService {

    @Resource
    private SesSender sesSender;

    @Resource
    private SpringTemplateEngine templateEngine;

    @Value("${ses.domain}")
    private String domain;

    @Override
    public ResponseEntity send(SampleMail sampleMail) {
        try{
            Context context = new Context();
            context.setVariable("content",sampleMail.getContent());
            String html = templateEngine.process("mail-sample.html",context);
            sesSender.sendMail("no-reply@" + domain, sampleMail.getTo(),sampleMail.getSubject(),html);
            return new ResponseEntity("mail send Success", HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity("mail send Fail", HttpStatus.BAD_REQUEST);
        }

    }
}
