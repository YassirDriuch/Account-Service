package account.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class UpdateRoleDTO {

    @NotEmpty
    @NotNull
    private String user;
    @NotEmpty
    @NotNull
    private String role;
    @NotEmpty
    @NotNull
    private String operation;

    public UpdateRoleDTO() {
    }

    public UpdateRoleDTO(String user, String role, String operation) {
        this.user = user;
        this.role = role;
        this.operation = operation;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "UpdateRoleDTO{" +
                "user='" + user + '\'' +
                ", role='" + role + '\'' +
                ", operation='" + operation + '\'' +
                '}';
    }
}
