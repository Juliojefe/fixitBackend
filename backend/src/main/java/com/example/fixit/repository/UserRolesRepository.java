package com.example.fixit.repository;
import com.example.fixit.model.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface UserRolesRepository extends JpaRepository<UserRoles, Integer> {

}