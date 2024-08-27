package com.inventory.server.model.user_permission;

import com.inventory.server.model.Permission;
import com.inventory.server.model.User;
import jakarta.persistence.*;

@Entity
@Table(name = "user_permission")
public class UserPermission {

    @EmbeddedId
    private UserPermissionId id;

    public UserPermissionId getId() {
        return id;
    }

    public void setId(UserPermissionId id) {
        this.id = id;
    }

    @ManyToOne
    @MapsId("idUser")
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne
    @MapsId("idPermission")
    @JoinColumn(name = "id_permission")
    private Permission permission;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }
}
