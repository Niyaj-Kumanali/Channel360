package com.channel360.user.infrastructure;

import com.channel360.user.domain.User;

import java.util.List;

public interface UserRepositoryCustom {

    List<User> spList(String search, String status, Long roleId,
                      Integer page, Integer size, String sortBy, String sortDir);

    Long spCount(String search, String status, Long roleId);
}
