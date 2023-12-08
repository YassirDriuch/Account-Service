package account.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role;
    private String name;
    private String description;

    @ManyToMany(mappedBy = "userGroups")
    private Set<User> users;

    public Group() {
    }

    public Group(String role, String name, String description) {
        this.role = role;
        this.name = name;
        this.description = description;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String name) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }
}
