package com.printtex.printtex_pos.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.printtex.printtex_pos.model.*;
import com.printtex.printtex_pos.repository.TilesRepository;
import com.printtex.printtex_pos.repository.UserRepository;
import com.printtex.printtex_pos.repository.UserTilesRepository;
import com.printtex.printtex_pos.service.*;
import com.printtex.printtex_pos.util.DbInit;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;


@Controller
public class UserController {
    private final UserService userService;
    private final RoleService roleService;
    private final TilesRepository tilesRepository;
    private final ModelMessageService modelMessageService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EventLogService eventLogService;
    private final BranchService branchService;
    private final DbInit dbInit;
    private final UserTilesRepository userTilesRepository;
    private final UserRepository userRepository;

    public UserController(UserService userService, BranchService branchService, RoleService roleService, TilesRepository tilesRepository, ModelMessageService modelMessageService, BCryptPasswordEncoder bCryptPasswordEncoder, EventLogService eventLogService, DbInit dbInit, UserTilesRepository userTilesRepository, UserRepository userRepository) {
        this.userService = userService;
        this.branchService = branchService;
        this.roleService = roleService;
        this.tilesRepository = tilesRepository;
        this.modelMessageService = modelMessageService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.eventLogService = eventLogService;
        this.dbInit = dbInit;
        this.userTilesRepository = userTilesRepository;
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/admin/user_management")
    public String getUserView() {
        return "userView";
    }

    @GetMapping(value = "/admin/get_all_tiles")
    @ResponseBody
    public List<Tiles> getAllTiles(Model model) {
        List<Tiles> tilesId = tilesRepository.findAll();
        modelMessageService.setModelMessage(model, "all_tiles", tilesId);
        return tilesId;
    }

    @GetMapping(value = "/admin/adduser")
    public String createUser(Model model) {
        modelMessageService.setModelMessage(model, "branchList", branchService.getAllBranch());
        modelMessageService.setModelMessage(model, "user", new User());
        modelMessageService.setEmptyResultMessage(model);
        return "adduser";

    }

    @PostMapping("/admin/adduser")
    public String checkPersonInfo(@Valid @ModelAttribute User user, BindingResult bindingResult, Model model, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            modelMessageService.setModelMessage(model, "result", "Wrong input");
            return "adduser";
        }
        int countUserExist = userRepository.countByEmailAndActive(user.getEmail(), 1);
        if (countUserExist > 0) {
            modelMessageService.setModelMessage(model, "result", "User email alreday used.");
            return "adduser";
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(1);
        String roleName = "ADMIN";
        if (user.getRole().equals("3")) {
            roleName = "BRANCH";
        }
        List<Tiles> tilesList;
        if (user.getRole().equals("2")) {
            tilesList = tilesRepository.findAllByTileIdIn(dbInit.oldTilesList());
            user.setIsNeedPermission(true);
        } else {
            tilesList = tilesRepository.findAll();
        }
        Role userRole = roleService.getRoleByName(roleName);
        user.setRoles(new HashSet<>(Collections.singletonList(userRole)));
        user = userService.addUser(user);
        @Valid User finalUser = user;
//        List<UserTiles> userTilesList = new ArrayList<>();
        tilesList.forEach(tiles -> {
            UserTiles userTiles = new UserTiles();
            userTiles.setTilesId(tiles.getId());
            userTiles.setUserId(finalUser.getId());
//            userTilesList.add(userTiles);
            userTilesRepository.save(userTiles);
        });
//        userTilesRepository.saveAll(userTilesList);
        modelMessageService.setModelMessage(model, "result", "User Created Successfully.");
        ObjectMapper mapper = new ObjectMapper();
        try {
            String userNewJson = mapper.writeValueAsString(user);
            eventLogService.saveInsertLog(userNewJson, User.class, String.valueOf(user.getId()), request);
        } catch (JsonProcessingException e) {

        }
        return "adduser";
    }

    @GetMapping("/admin/branchList")
    @ResponseBody
    public List<Branch> getBranchList(@RequestParam("roleId") Long roleId) {
        List<Branch> branches = branchService.getAllMainBranches();
        if (roleId.equals(Long.valueOf("3"))) {
            branches = branchService.getAllSubBranches();
        }
        return branches;
    }

    @GetMapping("/admin/changeStatus")
    public String inactiveUser(Model model, HttpServletRequest request) {
        modelMessageService.setModelMessage(model, "userList", userService.getAllUser());
        return "changestatus";
    }

    @GetMapping("/admin/getAllUser")
    public String getAllUser(Model model) {
        List<User> users = userService.getAllUserAndBranch();
        modelMessageService.setModelMessage(model, "userList", users);
        return "getAllUser";
    }


    @PutMapping("/admin/changeStatus")
    @ResponseBody
    public String changeStatus(@RequestParam("userId") Integer userId, @RequestParam("status") String status, HttpServletRequest request) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return "userNotFound";
        }
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getRole().equals("ADMIN"));
        if (isAdmin && (user.getIsNeedPermission() == null || !user.getIsNeedPermission())) {
            return "You can't change admin user";
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            String oldJson = mapper.writeValueAsString(user);
            if (status.equals("active")) {
                user.setActive(1);
            }
            if (status.equals("inactive")) {
                user.setActive(0);
            }
            user = userService.addUser(user);
            if (user != null) {
                String newJson = mapper.writeValueAsString(user);
                eventLogService.saveUpdateLog(newJson, oldJson, User.class, String.valueOf(user.getId()), request);
                return "successfullySaved";
            }
            return "failedToSave";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "failedToChangeStatus";
        }

    }

    @GetMapping("/admin/addtiles")
    public String addTiesPermission(Model model) {
        modelMessageService.setModelMessage(model, "userList", userService.getAllMainBranchUser());
        modelMessageService.setModelMessage(model, "tilesList", tilesRepository.findAllByTileIdIn(dbInit.newTilesList()));
        return "addtiles";
    }

    @PostMapping("/admin/addtiles")
    @ResponseBody
    public String saveTilesPermission(@RequestParam("userId") int userId, @RequestParam("tiles") String[] tilesIds, @RequestParam("isRevoke") Boolean isRevoke, HttpServletRequest request) {
        ObjectMapper mapper = new ObjectMapper();
        User user = userService.getUserById(userId);
        String oldUserJson = null;
        try {
            oldUserJson = mapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (user == null) {
            return "Requested user not found";
        }
        if (isRevoke) {
            List<Long> tilesIdList = new ArrayList<>();
            for (String tilesId : tilesIds) {
                Tiles tiles = tilesRepository.findByTileId(tilesId);
                tilesIdList.add(tiles.getId());
            }
            List<UserTiles> userTilesList = userTilesRepository.findAllByUserIdAndTilesIdIn(user.getId(), tilesIdList);
            userTilesRepository.deleteAll(userTilesList);
            return "savedSuccessfully";
        }
        List<Tiles> permittedTiles = new ArrayList<>();
        for (String tilesId : tilesIds) {
            Tiles tiles = tilesRepository.findByTileId(tilesId);
            if (tiles != null) {
                List<UserTiles> existingPermittedTiles = userTilesRepository.findAllByUserId(user.getId());
                if (existingPermittedTiles == null || existingPermittedTiles.size() == 0) {
                    permittedTiles.add(tiles);
                } else {
                    boolean isExistPermission = existingPermittedTiles.stream().anyMatch(userTiles -> userTiles.getTilesId().equals(tiles.getId()));
                    if (!isExistPermission) {
                        permittedTiles.add(tiles);
                    }
                }
            }
        }
        List<UserTiles> userTilesList = new ArrayList<>();
        permittedTiles.forEach(tiles -> {
            UserTiles userTiles = new UserTiles();
            userTiles.setTilesId(tiles.getId());
            userTiles.setUserId(user.getId());
            userTilesList.add(userTiles);
        });
        userTilesRepository.saveAll(userTilesList);
        if (userTilesList.size() > 0) {
            try {
                String newUserJson = mapper.writeValueAsString(userTilesList);
                eventLogService.saveInsertLog(newUserJson, UserTiles.class, userTilesList.get(0).getId().toString(), request);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return "savedSuccessfully";
        }
        return "failedToSavePermission";
    }

    @PostMapping("/admin/getPermittedTiles")
    @ResponseBody
    public List<Tiles> getPermittedTilesList(@RequestParam("userId") int userId) {
        List<UserTiles> userTilesList = userTilesRepository.findAllByUserId(userId);
        List<Long> tilesIdList = new ArrayList<>();
        userTilesList.forEach(userTiles -> {
            tilesIdList.add(userTiles.getTilesId());
        });
        return tilesRepository.findAllByIdIn(tilesIdList);
    }
}
