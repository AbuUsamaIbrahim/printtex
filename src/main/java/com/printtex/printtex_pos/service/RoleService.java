package com.printtex.printtex_pos.service;

import com.printtex.printtex_pos.model.Role;

import java.util.List;

public interface RoleService {
    void addRole(Role role);

    Role getRoleByName(String roleName);
}
