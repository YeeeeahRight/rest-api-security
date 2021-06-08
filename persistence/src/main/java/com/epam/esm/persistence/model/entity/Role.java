package com.epam.esm.persistence.model.entity;

import com.epam.esm.persistence.audit.EntityAuditListener;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "roles")
public class Role extends AbstractEntity {

    @Column(name = "name", length = 60, unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private List<User> users;

    public Role(long id, String name) {
        setId(id);
        this.name = name;
    }

    public Role() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;

        return Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Role{" +
                "name='" + name + '\'' +
                "} " + super.toString();
    }
}
