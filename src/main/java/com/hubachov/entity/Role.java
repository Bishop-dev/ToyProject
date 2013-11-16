package com.hubachov.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Role")
public class Role implements Serializable {
    private static final long serialVersionUID = 1328453834819427799L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "role_id", nullable = false, unique = true)
    private long id;
    @Column(name = "role_name")
    private String name;

    public Role() {

    }

    public Role(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Role(RoleBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Role other = (Role) obj;
        return this.id == other.getId() && this.name.equals(other.getName());
    }

    @Override
    public String toString() {
        return "Role [id=" + id + ", name=" + name + "]";
    }


    public Role name(String name) {
        this.name = name;
        return this;
    }

    public static class RoleBuilder {
        private long id;
        private String name;

        public RoleBuilder id(long id) {
            this.id = id;
            return this;
        }

        public RoleBuilder name(String name) {
            this.name = name;
            return this;
        }

        public Role build() {
            return new Role(this);
        }
    }
}
