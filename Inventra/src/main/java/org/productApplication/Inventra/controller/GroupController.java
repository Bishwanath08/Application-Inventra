package org.productApplication.Inventra.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.productApplication.Inventra.jwt.JwtTokenUtil;
import org.productApplication.Inventra.models.TblGroups;
import org.productApplication.Inventra.models.TblPermissions;
import org.productApplication.Inventra.models.TblUsers;
import org.productApplication.Inventra.service.GroupService;
import org.productApplication.Inventra.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/group")
public class GroupController extends BaseController {

        @Autowired
        private GroupService groupService;

        @Autowired
        private UserService userService;

        @Autowired
        private JwtTokenUtil jwtTokenUtil;

        @GetMapping("/add")
        public String getAddGroupPage(Model model, @CookieValue(value = "jwtToken",
                defaultValue = "Guest") String jwtToken) {
            try {
                String email = jwtTokenUtil.extractEmail(jwtToken);
                TblUsers users = userService.getUsersDetails(email);
                List<TblPermissions> dbPermission = groupService.getPermissionList();
                List<String> permissions = groupService.getPermissionsForGroup(users.getGroupId());
                model.addAttribute("allPermissions", permissions);
                model.addAttribute("dbPermission", dbPermission);
                model.addAttribute("newGroup", new TblGroups());
                model.addAttribute("username", users.getUserName());
                model.addAttribute("email", users.getEmail());
                model.addAttribute("avatarUrl", "/images/avatar.jpeg");
                model.addAttribute("role", users.getRole());
                return "groups/add_group_permissions";
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("error", "Error fetching permissions: " + e.getMessage());
                return "error_page";
            }
        }

        @PostMapping("/add")
        public String saveGroup(
                HttpServletRequest request,
                @ModelAttribute("newGroup") TblGroups newGroup,
                @RequestParam(value = "permissionIds", required = false) List<Long> permissionIds, Model model,
                RedirectAttributes redirectAttributes, @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {
            try {
                TblUsers loggedUser = getLoggedUserViaRequest(request,null, jwtToken);
                TblGroups existingGroup = groupService.getGroupByName(newGroup.getName());

                if (existingGroup != null) {
                    model.addAttribute("error", "A group with the same name already exists.");
                    model.addAttribute("newGroup", newGroup);
                    List<TblPermissions> allPermissions = groupService.getPermissionList();
                    model.addAttribute("allPermissions", allPermissions);
                    return "groups/add_group_permissions";
                }

                TblGroups group = groupService.saveGroupWithPermission(loggedUser.getId(), newGroup.getName(), permissionIds);

                if (group != null && group.getId() != null && group.getId() > 0) {
                    redirectAttributes.addFlashAttribute("message", "Group Successfully created");
                    return "redirect:/group/list";
                } else {
                    model.addAttribute("error", "Something went wrong, group not created successfully");
                    model.addAttribute("newGroup", newGroup);
                    List<TblPermissions> allPermissions = groupService.getPermissionList();
                    model.addAttribute("allPermissions", allPermissions);
                    return "groups/add_group_permissions";
                }

            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("error", "Error saving group: " + e.getMessage());
                model.addAttribute("newGroup", newGroup);
                List<TblPermissions> allPermissions = groupService.getPermissionList();
                model.addAttribute("allPermissions", allPermissions);
                return "groups/add_group_permissions";
            }
        }


        @GetMapping("/list")
        public String getGroupListData(@RequestParam(defaultValue = "0", required = false) int page,
                                       @RequestParam(defaultValue = "10", required = false) int size,
                                       @RequestParam(defaultValue = "name", required = false) String sortBy,
                                       @RequestParam(defaultValue = "asc", required = false) String sortDir,
                                       Model model, @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {
            try {
                String email = jwtTokenUtil.extractEmail(jwtToken);
                TblUsers users = userService.getUsersDetails(email);
                List<String> permissions = groupService.getPermissionsForGroup(users.getGroupId());
                model.addAttribute("allPermissions", permissions);
                Page<TblGroups> groupsList = groupService.getGroupListData(page, size, sortBy, sortDir);
                model.addAttribute("groupsList", groupsList);
                model.addAttribute("newGroup", new TblGroups());
                model.addAttribute("username", users.getUserName());
                model.addAttribute("email", users.getEmail());
                model.addAttribute("avatarUrl", "/images/avatar.jpeg");
                model.addAttribute("role", users.getRole());
                model.addAttribute("title", "Group List");
                return "groups/group_list";

            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("error", "Error fetching group list: " + e.getMessage());
                return "error_page";
            }
        }

        @GetMapping("/update/{groupId}")
        public String getEditGroupPage(@PathVariable Long groupId, Model model,
                                       @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {
            try {
                String email = jwtTokenUtil.extractEmail(jwtToken);
                TblUsers users = userService.getUsersDetails(email);

                List<String> permissions = groupService.getPermissionsForGroup(users.getGroupId());
                model.addAttribute("allPermissions", permissions);


                List<TblPermissions> dbPermission = groupService.getPermissionList();
                model.addAttribute("dbPermission", dbPermission);


                Optional<TblGroups> groupOptional = groupService.getGroupById(groupId);
                if (groupOptional.isEmpty()) {
                    model.addAttribute("error", "Group not found.");
                    return "groups/group_list";
                }
                TblGroups group = groupOptional.get();
                model.addAttribute("group", group);


                Set<Long> selectedPermissionIds = group.getPermissions().stream()
                        .map(TblPermissions::getId)
                        .collect(Collectors.toSet());
                model.addAttribute("selectedPermissionIds", selectedPermissionIds);

                return "groups/Update_group_permissions";

            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("error", "Error fetching group or permissions: " + e.getMessage());
                return "error_page";
            }
        }


        @PostMapping("/update")
        public String updateGroup(
                HttpServletRequest request,
                @ModelAttribute("group") TblGroups group,
                @RequestParam(value = "permissionIds", required = false) List<Long> permissionIds,
                Model model,
                RedirectAttributes redirectAttributes, @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {
            try {
                TblUsers loggedUser = getLoggedUserViaRequest(request,null,jwtToken);
                groupService.updateGroup(loggedUser.getId(), group, permissionIds);
                redirectAttributes.addFlashAttribute("message", "Group Successfully Updated");
                return "redirect:/group/list";
            }catch (Exception e){
                e.printStackTrace();
                model.addAttribute("error", "Error updating group: " + e.getMessage());
                List<TblPermissions> allPermissions = groupService.getPermissionList();
                model.addAttribute("allPermissions", allPermissions);
                model.addAttribute("selectedPermissionIds", permissionIds != null ? permissionIds.stream().collect(Collectors.toSet()) : null);
                return "groups/Update_group_permissions";
            }
        }

        @GetMapping("/delete/{groupId}")
        public String deleteGroup(@PathVariable Long groupId, Model model, @CookieValue(value = "jwtToken",
                defaultValue = "Guest") String jwtToken) {
            try {
                String email = jwtTokenUtil.extractEmail(jwtToken);
                TblUsers users = userService.getUsersDetails(email);
                List<TblPermissions> dbPermission = groupService.getPermissionList();
                List<String> permissions = groupService.getPermissionsForGroup(users.getGroupId());
                model.addAttribute("allPermissions", permissions);
                model.addAttribute("dbPermission", dbPermission);
                groupService.deleteGroup(groupId);
                model.addAttribute("message", "Group successfully deleted.");
                Page<TblGroups> groupsList = groupService.getGroupListData(0,10,"name","asc" );
                model.addAttribute("groupsList", groupsList);
                model.addAttribute("title", "Group List");
                model.addAttribute("newGroup", new TblGroups());
                return "groups/group_list";
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("error", "Error deleting group: " + e.getMessage());
            return "groups/group_list";
        }
        }
}
