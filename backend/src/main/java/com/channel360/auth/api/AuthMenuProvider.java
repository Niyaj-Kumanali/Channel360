package com.channel360.auth.api;

import com.channel360.common.dto.response.MenuItem;
import com.channel360.menu.application.MenuApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthMenuProvider {

    private final MenuApplicationService menuApplicationService;

    public List<MenuItem> getCurrentUserMenu() {
        return menuApplicationService.getCurrentUserMenu();
    }
}
