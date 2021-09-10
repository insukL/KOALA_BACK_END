package in.koala.service;

import in.koala.domain.test.SampleMail;
import org.springframework.http.ResponseEntity;

public interface SesService {

    ResponseEntity send(SampleMail sampleMail);
}
