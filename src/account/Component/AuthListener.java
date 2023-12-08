package account.Component;

import account.ExceptionHandler.BadRequestException;
import account.ExceptionHandler.RoleNotFoundException;
import account.ExceptionHandler.UserNotFoundException;
import account.Repository.EventRepository;
import account.Repository.GroupRepository;
import account.Repository.UserRepository;
import account.model.Event;
import account.model.Group;
import account.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
public class AuthListener {

    @Autowired
    EventRepository eventRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    private GroupRepository groupRepository;

    private static Logger log = LoggerFactory.getLogger(AuthListener.class);

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        eventRepository.save(new Event(
                LocalDateTime.now(),
                "LOGIN_FAILED",
                event.getAuthentication().getName().toLowerCase(),
                httpServletRequest.getServletPath(),
                httpServletRequest.getServletPath()
        ));
        if (userRepository.findByEmailIgnoreCase(event.getAuthentication().getName()).isPresent()) {
            User user = userRepository.findByEmailIgnoreCase(event.getAuthentication().getName()).orElseThrow(() -> new UsernameNotFoundException("User not Found!"));
            user.setLoginAttempts(user.getLoginAttempts() + 1);
            if (user.getLoginAttempts() > 4) {
                 if (user.getUserGroups().stream().map(Group::getRole).noneMatch(s -> s.equalsIgnoreCase("ROLE_ADMINISTRATOR"))) {
                    eventRepository.save(new Event(
                            LocalDateTime.now(),
                            "BRUTE_FORCE",
                            user.getEmail().toLowerCase(),
                            httpServletRequest.getServletPath(),
                            httpServletRequest.getServletPath()
                    ));
                    eventRepository.save(new Event(
                            LocalDateTime.now(),
                            "LOCK_USER",
                            user.getEmail().toLowerCase(),
                            "Lock user " + user.getEmail().toLowerCase(),
                            httpServletRequest.getServletPath()
                    ));
                    user.setAccountNonLocked(false);
                }

            }
            userRepository.save(user);
        }
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        User user = userRepository.findByEmailIgnoreCase(event.getAuthentication().getName()).orElseThrow(() -> new UsernameNotFoundException("User not Found!"));
        user.setLoginAttempts(0);
        userRepository.save(user);
    }
}
