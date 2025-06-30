package org.productApplication.Inventra.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.productApplication.Inventra.jwt.JwtTokenUtil;
import org.productApplication.Inventra.models.TblGroups;
import org.productApplication.Inventra.models.TblPermissions;
import org.productApplication.Inventra.models.TblUsers;
import org.productApplication.Inventra.service.GroupService;
import org.productApplication.Inventra.service.SubAdminService;
import org.productApplication.Inventra.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;


public  class BaseController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private SubAdminService subAdminService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    public TblUsers getLoggedUserViaRequest(HttpServletRequest request, Model model, @CookieValue(value = "jwtToken",
            defaultValue = "Guest") String jwtToken) {
        String email = jwtTokenUtil.extractEmail(jwtToken);
        TblUsers users = subAdminService.getSubAdminDetails(email);
        return users;
    }

    public  void getBasicDeitals(Model model, @CookieValue(value = "jwtToken",
            defaultValue = "Guest") String jwtToken) {
        String email = jwtTokenUtil.extractEmail(jwtToken);
        TblUsers users = userService.getUsersDetails(email);
        List<TblPermissions> dbPermission = groupService.getPermissionList();
        List<String> permissions = groupService.getPermissionsForGroup(users.getGroupId());
        model.addAttribute("allPermissions", permissions);
        model.addAttribute("dbPermission",   dbPermission);
        model.addAttribute("newGroup",       new TblGroups());
        model.addAttribute("username", users.getUserName());
        model.addAttribute("email", users.getEmail());
        model.addAttribute("avatarUrl", "/images/avatar.jpeg");
        model.addAttribute("role", users.getRole());

    }


    void addCommonAttributes(Model model, TblUsers newUser, SubAdminService subAdminService, GroupService groupService, TblUsers loggedUser) {
        model.addAttribute("newUser", newUser);
        List<TblGroups> groups = subAdminService.getAllGroups();
        model.addAttribute("groups", groups);
        List<String> allPermissions = groupService.getPermissionsForGroup(loggedUser.getGroupId());
        model.addAttribute("allPermissions", allPermissions);
    }
}
