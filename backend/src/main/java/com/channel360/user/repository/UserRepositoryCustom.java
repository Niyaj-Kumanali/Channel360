package com.channel360.user.repository;

import com.channel360.user.entity.User;

import java.util.List;

public interface UserRepositoryCustom {

    List<User> spList(String search, String status, Long roleId,
                      Integer page, Integer size, String sortBy, String sortDir);

    Long spCount(String search, String status, Long roleId);
}
