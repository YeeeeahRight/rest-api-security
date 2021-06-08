package com.epam.esm.web.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.Set;

public class UserDto extends RepresentationModel<UserDto>  {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    @NotNull(message = "entity.data.missing")
    @Size(min = 1, max = 60, message = "user.username.invalid")
    private String username;

    @NotNull(message = "entity.data.missing")
    @Size(min = 1, max = 80, message = "user.first.name.invalid")
    private String firstName;

    @NotNull(message = "entity.data.missing")
    @Size(min = 1, max = 80, message = "user.last.name.invalid")
    private String lastName;

    @NotNull(message = "entity.data.missing")
    @Size(min = 1, max = 100, message = "user.email.invalid")
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 5, max = 200, message = "user.pass.invalid")
    private String password;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<RoleDto> roles;

    @JsonCreator
    public UserDto() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<RoleDto> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleDto> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        UserDto userDto = (UserDto) o;

        if (!Objects.equals(username, userDto.username)) {
            return false;
        }
        if (!Objects.equals(firstName, userDto.firstName)) {
            return false;
        }
        if (!Objects.equals(lastName, userDto.lastName)) {
            return false;
        }
        if (!Objects.equals(email, userDto.email)) {
            return false;
        }
        return Objects.equals(password, userDto.password);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
