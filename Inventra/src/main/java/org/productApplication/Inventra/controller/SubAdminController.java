package org.productApplication.Inventra.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.productApplication.Inventra.jwt.JwtTokenUtil;
import org.productApplication.Inventra.models.TblGroups;
import org.productApplication.Inventra.models.TblUsers;
import org.productApplication.Inventra.service.GroupService;
import org.productApplication.Inventra.service.SubAdminService;
import org.productApplication.Inventra.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import jakarta.validation.Valid;
import java.util.Optional;


@Controller
@RequestMapping("/admins")
public class SubAdminController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(SubAdminController.class);

    @Autowired
    private SubAdminService subAdminService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private GroupService groupService;

    @GetMapping("/list")
    public String getSubAdminList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model, @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {
        try {
            String email = jwtTokenUtil.extractEmail(jwtToken);
            TblUsers users = userService.getUsersDetails(email);
            List<String> allPermissions = groupService.getPermissionsForGroup(users.getGroupId());

            Page<TblUsers> subAdminList = subAdminService.getSubAdminListData(page, size, sortBy, sortDir);

            model.addAttribute("username", users.getUserName());
            model.addAttribute("email", users.getEmail());
            model.addAttribute("avatarUrl", "/images/avatar.jpeg");
            model.addAttribute("role", users.getRole());
            model.addAttribute("newUser", new TblUsers());
            model.addAttribute("subAdminList", subAdminList);
            model.addAttribute("allPermissions", allPermissions);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

            return "admin/subAdmin_list";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error fetching sub-admin list: " + e.getMessage());
            return "error_page";
        }
    }


    @GetMapping("/add")
    public String getAddUserPage(Model model, @CookieValue(value = "jwtToken",
            defaultValue = "Guest") String jwtToken) {
        try {
            String email = jwtTokenUtil.extractEmail(jwtToken);
            TblUsers users = userService.getUsersDetails(email);

            List<String> allPermissions = groupService.getPermissionsForGroup(users.getGroupId());
            model.addAttribute("allPermissions", allPermissions);

            model.addAttribute("newGroup", new TblGroups());
            model.addAttribute("username", users.getUserName());
            model.addAttribute("email", users.getEmail());
            model.addAttribute("avatarUrl", "/images/avatar.jpeg");
            model.addAttribute("role", users.getRole());
            model.addAttribute("newUser", new TblUsers());
            List<TblGroups> groups = subAdminService.getAllGroups();
            model.addAttribute("groups", groups);

            return "admin/subAdmin_add";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error fetching subAdmin: " + e.getMessage());
            return "error_page";
        }
    }


    @PostMapping("/add")
    public String saveUser(
            HttpServletRequest request,
            @Valid @ModelAttribute("newUser") TblUsers newUser,
            BindingResult result,
            @RequestParam("groupId") Long groupId,
            @RequestParam(value = "permissionIds", required = false) List<Long> permissionIds,
            Model model,
            RedirectAttributes redirectAttributes,
            @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken
    ) {
        TblUsers loggedUser = null;
        try {
            loggedUser = getLoggedUserViaRequest(request, null, jwtToken);

            if (result.hasErrors()) {
                model.addAttribute("error", "Please correct the errors below.");
                List<TblGroups> groups = subAdminService.getAllGroups();
                model.addAttribute("groups", groups);
                List<String> allPermissions = groupService.getPermissionsForGroup(loggedUser.getGroupId());
                model.addAttribute("allPermissions", allPermissions);
                return "admin/subAdmin_add";
            }

            TblUsers existingUser = subAdminService.getUserByEmail(newUser.getEmail());
            if (existingUser != null) {
                model.addAttribute("error", "A user with this email already exists.");
                model.addAttribute("newUser", newUser);
                List<TblGroups> groups = subAdminService.getAllGroups();
                List<String> allPermissions = groupService.getPermissionsForGroup(loggedUser.getGroupId());
                model.addAttribute("allPermissions", allPermissions);
                model.addAttribute("groups", groups);
                return "admin/subAdmin_add";
            }

            if (!newUser.isPasswordMatching()) {
                result.rejectValue("confirmPassword", "error.user", "Passwords do not match");
                List<TblGroups> groups = subAdminService.getAllGroups();
                List<String> allPermissions = groupService.getPermissionsForGroup(loggedUser.getGroupId());
                model.addAttribute("allPermissions", allPermissions);
                model.addAttribute("groups", groups);
                return "admin/subAdmin_add";
            }

            TblUsers savedUser = subAdminService.saveUser(loggedUser.getId(), newUser, groupId, permissionIds);

            if (savedUser != null && savedUser.getId() != null && savedUser.getId() > 0) {
                redirectAttributes.addFlashAttribute("message", "Sub-Admin successfully created.");
                return "redirect:/admins/list";
            } else {

                model.addAttribute("error", "Something went wrong. Sub-Admin not created.");
                model.addAttribute("newUser", newUser);
                List<TblGroups> groups = subAdminService.getAllGroups();
                List<String> allPermissions = groupService.getPermissionsForGroup(loggedUser.getGroupId());
                model.addAttribute("allPermissions", allPermissions);
                model.addAttribute("groups", groups);
                return "admin/subAdmin_add";
            }

        } catch (Exception e) {
            logger.error("Error saving user", e);
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            model.addAttribute("newUser", newUser);
            List<TblGroups> groups = subAdminService.getAllGroups();
            List<String> allPermissions = groupService.getPermissionsForGroup(loggedUser.getGroupId());
            model.addAttribute("allPermissions", allPermissions);
            model.addAttribute("groups", groups);
            return "admin/subAdmin_add";
        }
    }


    @GetMapping("/update/{userId}")
    public String getEditSubAdminPage(@PathVariable("userId") Long userId, Model model,
                                      RedirectAttributes redirectAttributes,
                                      @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {
        try {
            String email = jwtTokenUtil.extractEmail(jwtToken);
            TblUsers users = userService.getUsersDetails(email);

            List<String> allPermissions = groupService.getPermissionsForGroup(users.getGroupId());
            model.addAttribute("allPermissions", allPermissions);

            model.addAttribute("newGroup", new TblGroups());
            model.addAttribute("username", users.getUserName());
            model.addAttribute("email", users.getEmail());
            model.addAttribute("avatarUrl", "/images/avatar.jpeg");
            model.addAttribute("role", users.getRole());

            Optional<TblUsers> userOptional = subAdminService.getUserById(userId);
            if (userOptional.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "SubAdmin not found.");
                return "redirect:/admins/list";
            }
            TblUsers user = userOptional.get();
            model.addAttribute("user", user);

            List<TblGroups> groups = subAdminService.getAllGroups();
            model.addAttribute("groups", groups);

            return "admin/Update_subAdmin";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error fetching SubAdmin: " + e.getMessage());
            return "error_page";
        }
    }

    @PostMapping("/update")
    public String updateSubAdmin(
            HttpServletRequest request,
            @Valid @ModelAttribute("user") TblUsers user,
            BindingResult result,
            @RequestParam("groupId") Long groupId,
            @RequestParam(value = "permissionIds", required = false) List<Long> permissionIds,
            Model model,
            RedirectAttributes redirectAttributes,
            @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {
        TblUsers loggedUser = null;
        try {
            loggedUser = getLoggedUserViaRequest(request, null, jwtToken);

            if (result.hasErrors()) {
                model.addAttribute("error", "Please correct the errors below.");
                List<TblGroups> groups = subAdminService.getAllGroups();
                model.addAttribute("groups", groups);
                List<String> allPermissions = groupService.getPermissionsForGroup(loggedUser.getGroupId());
                model.addAttribute("allPermissions", allPermissions);

                return "admin/Update_subAdmin";
            }

            subAdminService.updateUser(loggedUser.getId(), user, groupId, permissionIds);

            redirectAttributes.addFlashAttribute("message", "SubAdmin Successfully Updated");
            return "redirect:/admins/list";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error updating subAdmin: " + e.getMessage());
            List<TblGroups> groups = subAdminService.getAllGroups();
            model.addAttribute("groups", groups);
            List<String> allPermissions = groupService.getPermissionsForGroup(loggedUser.getGroupId());
            model.addAttribute("allPermissions", allPermissions);
            return "admin/Update_subAdmin";
        }
    }



    @GetMapping("/delete/{userId}")
    public String deleteSubAdmin(@PathVariable Long userId, HttpServletRequest request,
                                 Model model, RedirectAttributes redirectAttributes,
                                 @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {
        try {
            String email = jwtTokenUtil.extractEmail(jwtToken);
            TblUsers users = userService.getUsersDetails(email);

            List<String> allPermissions = groupService.getPermissionsForGroup(users.getGroupId());
            model.addAttribute("allPermissions", allPermissions);

            if (!allPermissions.contains("subAdmin_delete")) {
                redirectAttributes.addFlashAttribute("error", "You do not have permission to delete SubAdmins.");
                return "redirect:/admins/list";
            }
            subAdminService.deleteUser(userId);
            model.addAttribute("message", "SubAdmin successfully deleted.");
            return "redirect:/admins/list";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error deleting sub-admin: " + e.getMessage());
            return "redirect:/admins/list";
        }
    }

}
