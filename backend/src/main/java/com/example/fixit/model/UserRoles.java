package com.example.fixit.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_roles")
public class UserRoles {
    @Id
    private Integer userId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "isadmin", nullable = false)
    private Boolean isAdmin = false;

    @Column(name = "ismechanic", nullable = false)
    private Boolean isMechanic = false;

    // Getters and setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Boolean getIsMechanic() {
        return isMechanic;
    }

    public void setIsMechanic(Boolean isMechanic) {
        this.isMechanic = isMechanic;
    }
}