package com.inventory.server.service;

import com.inventory.server.domain.UserPermissionRepository;
import com.inventory.server.model.user_permission.UserPermission;
import org.springframework.stereotype.Service;

@Service
public class UserPermissionService {

    private final UserPermissionRepository userPermissionRepository;

    public UserPermissionService(UserPermissionRepository userPermissionRepository) {
        this.userPermissionRepository = userPermissionRepository;
    }

    public void save(UserPermission userPermission) {
        userPermissionRepository.save(userPermission);
    }
}
