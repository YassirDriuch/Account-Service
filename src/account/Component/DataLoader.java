package account.Component;

import account.Repository.GroupRepository;
import account.model.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {

    @Autowired
    GroupRepository groupRepository;
    private final static Logger log = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    public DataLoader(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
        createRoles();
    }

    private void createRoles() {
        try {
            // Check if roles already exist to avoid duplicates
            if (groupRepository.findGroupByRole("ROLE_ADMINISTRATOR").isEmpty()) {
                groupRepository.save(new Group("ROLE_ADMINISTRATOR", "Admin", "ADMIN"));
            }
            if (groupRepository.findGroupByRole("ROLE_USER").isEmpty()) {
                groupRepository.save(new Group("ROLE_USER", "User", "BUSINESS"));
            }
            if (groupRepository.findGroupByRole("ROLE_ACCOUNTANT").isEmpty()) {
                groupRepository.save(new Group("ROLE_ACCOUNTANT", "Accountant", "BUSINESS"));
            }
            if (groupRepository.findGroupByRole("ROLE_AUDITOR").isEmpty()) {
                groupRepository.save(new Group("ROLE_AUDITOR", "Auditor", "BUSINESS"));
            }
        } catch (Exception e) {
            // It's generally a good practice to log exceptions
            // Log the exception or handle it as per your requirement
            log.error(e.getMessage());
        }
    }
}