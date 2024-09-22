package com.example.swip.repository;

import com.example.swip.entity.User;
import com.example.swip.entity.enumtype.ChatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long>{
    User findByEmail(String email);
    User findByEmailAndValidate(String email, String validate);
    User findByNickname(String nickname);

    @Query("SELECT u.chat_status FROM User u WHERE u.id = :id")
    ChatStatus findChat_statusById(@Param("id") Long id);
}
