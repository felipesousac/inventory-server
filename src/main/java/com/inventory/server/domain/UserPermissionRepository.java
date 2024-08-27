package com.inventory.server.domain;

import com.inventory.server.model.user_permission.UserPermission;
import com.inventory.server.model.user_permission.UserPermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPermissionRepository extends JpaRepository<UserPermission, UserPermissionId> {
}
