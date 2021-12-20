package com.printtex.printtex_pos.controller;

import com.printtex.printtex_pos.model.Tiles;
import com.printtex.printtex_pos.model.User;
import com.printtex.printtex_pos.repository.TilesRepository;
import com.printtex.printtex_pos.repository.UserTilesRepository;
import com.printtex.printtex_pos.service.ModelMessageService;
import com.printtex.printtex_pos.service.RoleService;
import com.printtex.printtex_pos.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class LoginController {

    private final UserService userService;
    private final RoleService roleService;
    private final TilesRepository tilesRepository;
    private final ModelMessageService modelMessageService;
    private final UserTilesRepository userTilesRepository;

    public LoginController(UserService userService, RoleService roleService, TilesRepository tilesRepository, ModelMessageService modelMessageService, UserTilesRepository userTilesRepository) {
        this.userService = userService;
        this.roleService = roleService;
        this.tilesRepository = tilesRepository;
        this.modelMessageService = modelMessageService;
        this.userTilesRepository = userTilesRepository;
    }

    @RequestMapping(value = {"/", "/login"}, method = RequestMethod.GET)
    public String login(Model model, @RequestParam(value = "error", required = false) String error) {
        if (error != null) {
            modelMessageService.setModelMessage(model, "result", "Email or Password incorrect.\nPlease Try again");
            return "login";
        }
        modelMessageService.setEmptyResultMessage(model);
        return "login";
    }

    public Boolean isBranch(Model model) {
        if (getAuthenticatedUser().getRoles().stream().anyMatch(role -> roleService.getRoleByName("BRANCH").equals(role))) {
            modelMessageService.setModelMessage(model, "isBranch", "true");
            return true;
        }
        return false;
    }

    public User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.getUserByEmail(auth.getName());
    }

    @RequestMapping(value = {"/admin/home", "/branch/home"}, method = RequestMethod.GET)
    public String home(Model model) {
        User user = getAuthenticatedUser();
        List<String> permissionList = new ArrayList<>();
        if (user.getIsNeedPermission() != null && user.getIsNeedPermission()) {
            userTilesRepository.findAllByUserId(user.getId()).forEach(userTiles -> {
                Optional<Tiles> optionalTiles = tilesRepository.findById(userTiles.getTilesId());
                if (optionalTiles.isPresent()) {
                    Tiles tiles = optionalTiles.get();
                    permissionList.add(tiles.getTileId());
                }
            });
        } else {
            tilesRepository.findAll().forEach(tiles -> {
                permissionList.add(tiles.getTileId());
            });
        }

        modelMessageService.setModelMessage(model, "userName", user.getName() + "(" + user.getEmail() + ")");
        modelMessageService.setModelMessage(model, "adminMessage", "Content Available Only for Users with Admin Role");
        modelMessageService.setModelMessage(model, "userAccess", permissionList);
        if (isBranch(model)) {
            return "branch/home";
        }
        return "admin/home";
    }

}
