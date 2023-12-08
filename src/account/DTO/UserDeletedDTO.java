package account.DTO;

public class UserDeletedDTO {
    private String user, status;

    public UserDeletedDTO(String user, String status) {
        this.user = user;
        this.status = status;
    }

    public UserDeletedDTO() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
