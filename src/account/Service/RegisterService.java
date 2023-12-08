package account.Service;

import account.DTO.UserDTO;
import account.ExceptionHandler.*;
import account.Repository.GroupRepository;
import account.Repository.UserRepository;
import account.model.Group;
import account.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RegisterService {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepo;
    @Autowired
    GroupRepository groupRepo;

    public ResponseEntity<UserDTO> save(User user){
        if (userRepo.findByEmailIgnoreCase(user.getEmail()).isEmpty()) {
            if (!isBreachedPassword(user.getPassword())) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                Set<Group> defaultGroup = new HashSet<>();
                if (userRepo.count() > 0) {
                    defaultGroup.add(groupRepo.findGroupByRole("ROLE_USER").orElseThrow(() -> new RoleNotFoundException("Role not found!")));
                    user.setUserGroups(defaultGroup);
                } else {
                    defaultGroup.add(groupRepo.findGroupByRole("ROLE_ADMINISTRATOR").orElseThrow(() -> new RoleNotFoundException("Role not found!")));
                    user.setUserGroups(defaultGroup);
                }
                userRepo.save(user);

                return new ResponseEntity<>(findByEmail(user.getEmail()), HttpStatus.OK);
            } else
                throw new BreachedPasswordException("The password is in the hacker's database!");
        } else
            throw new UserAlreadyFoundException("User exist!");
    }

    public User getUserByEmail(String email) {
        return userRepo.findByEmailIgnoreCase(email).orElseThrow(() -> new UserNotFoundException("User not found!"));
    }

    public ResponseEntity<Map<String,String>> updatePassword(User user, String password) {
        if (!passwordEncoder.matches(password, user.getPassword())){
            if (password.length()+1 > 12) {
                if (isBreachedPassword(password)) throw new BreachedPasswordException("The password is in the hacker's database!");
                user.setPassword(passwordEncoder.encode(password));
                userRepo.save(user);
                return new ResponseEntity<>(Map.of("email", user.getEmail().toLowerCase(), "status", "The password has been updated successfully"), HttpStatus.OK);
            } else throw new PasswordIsntDifferentException("Password length must be 12 chars minimum!");
        } else throw new PasswordIsntDifferentException("The passwords must be different!");
    }

    public UserDTO findByEmail(String email) {
        User user = userRepo.findByEmailIgnoreCase(email).orElseThrow(() -> new UserNotFoundException("User not found!"));
        return new UserDTO(user.getId(), user.getName(), user.getLastname(), user.getEmail(), user.getUserGroups().stream()
                .map(Group::getRole)
                .collect(Collectors.toList()));
    }

    public boolean isBreachedPassword(String password){
        List<String> breachedPasses = List.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
                "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
                "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");
        return breachedPasses.contains(password);
    }
}
