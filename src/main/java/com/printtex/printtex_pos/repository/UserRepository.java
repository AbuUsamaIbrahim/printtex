package com.printtex.printtex_pos.repository;

import com.printtex.printtex_pos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByEmail(String email);

    Optional<User> findByEmailAndActive(String email, int active);

    User findByIdAndActive(int userId, int active);

    List<User> findAllByIsNeedPermissionTrueAndActive(int active);

    @Query("SELECT u.name,u.email,u.active,b.name as branch from User u left join Branch b on u.branch.id=b.id")
    List<User> findAllUserWithBranch();

    int countByEmailAndActive(String email, int active);
}
