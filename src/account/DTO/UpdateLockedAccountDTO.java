package account.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class UpdateLockedAccountDTO {

    @NotNull
    @NotEmpty
    private String user, operation;

    public UpdateLockedAccountDTO(String user, String operation) {
        this.user = user;
        this.operation = operation;
    }

    public UpdateLockedAccountDTO() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
