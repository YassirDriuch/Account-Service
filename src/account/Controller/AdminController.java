package account.Controller;

import account.DTO.UpdateLockedAccountDTO;
import account.DTO.UpdateRoleDTO;
import account.DTO.UserDTO;
import account.DTO.UserDeletedDTO;
import account.ExceptionHandler.BadRequestException;
import account.ExceptionHandler.IllegalRemovalException;
import account.ExceptionHandler.RoleNotFoundException;
import account.ExceptionHandler.UserNotFoundException;
import account.Repository.EventRepository;
import account.Repository.GroupRepository;
import account.Repository.UserRepository;
import account.model.Event;
import account.model.Group;
import account.model.User;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class AdminController {

    @Autowired
    UserRepository userRepo;

    @Autowired
    GroupRepository groupRepo;

    @Autowired
    EventRepository eventRepository;

    private final String updateRolePath = "/api/admin/user/role";

    private final static Logger log = LoggerFactory.getLogger(AdminController.class);

    @GetMapping("/api/admin/user/")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return new ResponseEntity<>(userRepo.findAll().stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getName(),
                        user.getLastname(),
                        user.getEmail().toLowerCase(),
                        user.getUserGroups()
                                .stream()
                                .map(Group::getRole)
                                .sorted()
                                .collect(Collectors.toList())))
                .collect(Collectors.toList()),
                HttpStatus.OK);
    }
    @DeleteMapping("/api/admin/user/")
    public void dummyFunction(){

    }

    @DeleteMapping("/api/admin/user/{email}")
    public ResponseEntity<UserDeletedDTO> deleteUser(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String email) {
        User toDelete = userRepo.findByEmailIgnoreCase(email).orElseThrow(() -> new UserNotFoundException("User not found!"));
        log.info("User Found:\n" + toDelete + "\n\n\n\n\n" + toDelete.getUserGroups().stream().map(Group::getRole).toList().contains("ROLE_ADMINISTRATOR"));
        if (toDelete.getUserGroups().stream().map(Group::getRole).toList().contains("ROLE_ADMINISTRATOR")) throw new IllegalRemovalException("Can't remove ADMINISTRATOR role!");
        removeUser(toDelete);
        eventRepository.save(new Event(
                LocalDateTime.now(),
                "DELETE_USER",
                userDetails.getUsername(),
                email,
                "/api/admin/user"
        ));
        return new ResponseEntity<>(
                    new UserDeletedDTO(email, "Deleted successfully!"),
                    HttpStatus.OK
            );
    }

    @Transactional
    public void removeUser(User user) {
        userRepo.delete(user);
    }

    @PutMapping(updateRolePath)
    public ResponseEntity<UserDTO> saveRolesToUser(@AuthenticationPrincipal UserDetails userDetails, @RequestBody(required = false) UpdateRoleDTO updateRole){
        log.info(updateRole.toString());
        String prefix = "ROLE_";
        User user = userRepo.findByEmailIgnoreCase(updateRole.getUser()).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Group group = groupRepo.findGroupByRole(prefix + updateRole.getRole()).orElseThrow(() -> new RoleNotFoundException("Role not found!"));
        if (updateRole.getOperation().equalsIgnoreCase("grant")){
            if (
                    (group.getDescription().equals("ADMIN") && user.getUserGroups().contains(groupRepo.findGroupByDescription("BUSINESS").orElseThrow(() -> new RoleNotFoundException("administrative and business groups"))))
                            ||
                            (group.getDescription().equals("BUSINESS") && user.getUserGroups().contains(groupRepo.findGroupByDescription("ADMIN").orElseThrow(() -> new RoleNotFoundException("administrative and business groups"))))
            ) throw new BadRequestException("The user cannot combine administrative and business roles!");
            if (user.getUserGroups().contains(group)) throw new BadRequestException("User already has role!");
            user.getUserGroups().add(group);
            userRepo.save(user);
        } else if (updateRole.getOperation().equalsIgnoreCase("remove")) {
            if (group.getRole().equals("ROLE_ADMINISTRATOR")) throw new BadRequestException("Can't remove ADMINISTRATOR role!");
            if (!user.getUserGroups().contains(group)) throw new BadRequestException("The user does not have a role!");
            if (user.getUserGroups().size() <= 1) throw new BadRequestException("The user must have at least one role!");
            user.getUserGroups().remove(group);
            userRepo.save(user);
        } else throw new RuntimeException();
        String operation = updateRole.getOperation().equalsIgnoreCase("grant") ? "Grant" : "Remove";
        String fromTo = updateRole.getOperation().equalsIgnoreCase("grant") ? " to " : " from ";
        eventRepository.save(new Event(
                LocalDateTime.now(),
                operation.toUpperCase() + "_ROLE",
                userDetails.getUsername(),
                operation + " role " + updateRole.getRole().toUpperCase() + fromTo + updateRole.getUser().toLowerCase(),
                updateRolePath
        ));
        return new ResponseEntity<>(new UserDTO(user),HttpStatus.OK);
    }

    @PutMapping("/api/admin/user/access")
    public ResponseEntity<Map<String, String>> updateLock(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UpdateLockedAccountDTO data) {
        User user = userRepo.findByEmailIgnoreCase(data.getUser()).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if(data.getOperation().equalsIgnoreCase("lock")) {
            if (user.getUserGroups().contains(groupRepo.findGroupByRole("ROLE_ADMINISTRATOR").orElseThrow(() -> new RoleNotFoundException("Role not found!"))))
                throw new BadRequestException("Can't lock the ADMINISTRATOR!");
            user.setAccountNonLocked(false);
        } else if (data.getOperation().equalsIgnoreCase("unlock")){
            user.setAccountNonLocked(true);
            user.setLoginAttempts(0);
        }
        String operation = data.getOperation().toLowerCase();
        operation = operation.substring(0,1).toUpperCase() + operation.substring(1).toLowerCase();
        eventRepository.save(new Event(
                LocalDateTime.now(),
                data.getOperation().toUpperCase() + "_USER",
                userDetails.getUsername(),
                operation + " user " + user.getEmail().toLowerCase(),
                updateRolePath
        ));
        userRepo.save(user);
        return new ResponseEntity<>(Map.of("status", "User " + data.getUser().toLowerCase() + " " + operation.toLowerCase() + "ed!"), HttpStatus.OK);
    }
}
