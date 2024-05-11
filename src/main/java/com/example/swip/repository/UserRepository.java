package com.example.swip.repository;

import com.example.swip.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>{
    User findByEmail(String email);
    User findByEmailAndValidate(String email, String validate);
    User findByNickname(String nickname);
}
