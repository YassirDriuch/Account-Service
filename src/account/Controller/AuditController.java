package account.Controller;

import account.Repository.EventRepository;
import account.model.Event;
import account.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class AuditController {

    @Autowired
    EventRepository eventRepo;

    @GetMapping("/api/security/events/")
    public ResponseEntity<List<Event>> getSecurityEvents(){
        return new ResponseEntity<>(eventRepo.findAll(), HttpStatus.OK);
    }
}
