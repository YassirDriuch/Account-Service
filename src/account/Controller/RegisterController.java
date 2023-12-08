package account.Controller;


import account.DTO.UserDTO;
import account.Repository.EventRepository;
import account.Service.RegisterService;
import account.model.Event;
import account.model.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;


@RestController
public class RegisterController {

    @Autowired
    RegisterService registerService;

    @Autowired
    EventRepository eventRepository;

    private final String signUpPath = "/api/auth/signup";
    private final String changePassPath = "/api/auth/changepass";


    @PostMapping(signUpPath)
    public ResponseEntity<UserDTO> signUp(@Valid @RequestBody User user) {
        ResponseEntity<UserDTO> responseEntity = registerService.save(user);
        eventRepository.save(new Event(
                LocalDateTime.now(),
                "CREATE_USER",
                "Anonymous",
                user.getEmail(),
                signUpPath
        ));
        return responseEntity;
    }

    @PostMapping(changePassPath)
    public ResponseEntity<Map<String,String>> changePassword(@AuthenticationPrincipal UserDetails details, @RequestBody Map<String, String> body) {
        ResponseEntity<Map<String,String>> responseEntity = registerService.updatePassword(registerService.getUserByEmail(details.getUsername()), body.get("new_password"));
        eventRepository.save(new Event(
                LocalDateTime.now(),
                "CHANGE_PASSWORD",
                details.getUsername(),
                details.getUsername(),
                "/api/auth/changepass"
        ));
        return responseEntity;
    }

}
