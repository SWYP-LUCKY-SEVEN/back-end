package com.example.swip.repository;

import com.example.swip.entity.UserSearch;
import com.example.swip.entity.compositeKey.UserSearchId;
import com.example.swip.repository.custom.UserSearchRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSearchRepository extends JpaRepository<UserSearch, UserSearchId>, UserSearchRepositoryCustom {

}
