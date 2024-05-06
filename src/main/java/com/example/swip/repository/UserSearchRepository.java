package com.example.swip.repository;

import com.example.swip.entity.UserSearch;
import com.example.swip.entity.compositeKey.UserSearchId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSearchRepository extends JpaRepository<UserSearch, UserSearchId> {
}
