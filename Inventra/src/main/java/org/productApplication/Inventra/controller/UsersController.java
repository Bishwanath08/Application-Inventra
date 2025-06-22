package org.productApplication.Inventra.controller;

import org.productApplication.Inventra.DTO.UsersResponse;
import org.productApplication.Inventra.jwt.JwtTokenUtil;
import org.productApplication.Inventra.models.TblUsers;
import org.productApplication.Inventra.models.Users;
import org.productApplication.Inventra.service.GroupService;
import org.productApplication.Inventra.service.UserService;
import org.productApplication.Inventra.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Controller
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UsersService usersService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public String listAllUsers( @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "25") int size,
                                Model model, @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {
        try {
            String email = jwtTokenUtil.extractEmail(jwtToken);
            TblUsers users = userService.getUsersDetails(email);
            List<String> allPermissions = groupService.getPermissionsForGroup(users.getGroupId());

            UsersResponse response = usersService.getUsers(page, size);

            model.addAttribute("username", users.getUserName());
            model.addAttribute("email", users.getEmail());
            model.addAttribute("avatarUrl", "/images/avatar.jpeg");
            model.addAttribute("role", users.getRole());


            if (response != null) {
                model.addAttribute("allPermissions", allPermissions);

                model.addAttribute("users", response.getUsers());
                model.addAttribute("currentPage", page);
                model.addAttribute("pageSize", size);
                model.addAttribute("totalUsers", response.getTotal());
                int totalPages = (int) Math.ceil((double) response.getTotal() / size);
                model.addAttribute("totalPages", totalPages);

                return "users/users_list";
            } else {
                model.addAttribute("error", "Failed to fetch users");
                return "error_page";
            }
        }catch (Exception e){
            e.printStackTrace();
            model.addAttribute("error", "Error fetching users: " + e.getMessage());
            return "error_page";
        }
    }


    @GetMapping("/add")
    public String showAddUserForm(Model model, @CookieValue(value = "jwtToken",
            defaultValue = "Guest") String jwtToken) {
        try {
            String email = jwtTokenUtil.extractEmail(jwtToken);
            TblUsers users = userService.getUsersDetails(email);

            List<String> allPermissions = groupService.getPermissionsForGroup(users.getGroupId());
            model.addAttribute("allPermissions", allPermissions);
            model.addAttribute("username", users.getUserName());
            model.addAttribute("email", users.getEmail());
            model.addAttribute("avatarUrl", "/images/avatar.jpeg");
            model.addAttribute("role", users.getRole());
            model.addAttribute("user", new Users());
            return "users/add_user";
        }catch (Exception e){
            e.printStackTrace();
            model.addAttribute("error", "Error fetching users: " + e.getMessage());
            return "error_page";
        }
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute("user") Users user, Model model) {
        Users createdUser = usersService.addUser(user);

        if (createdUser != null) {
            model.addAttribute("message", "User added successfully!");
            return "redirect:/users/list";
        } else {
            model.addAttribute("error", "Failed to add user");
            return "error_page";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable("id") Long id, Model model, @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {
        try {
            String email = jwtTokenUtil.extractEmail(jwtToken);
            TblUsers loggedInUser = userService.getUsersDetails(email);

            List<String> allPermissions = groupService.getPermissionsForGroup(loggedInUser.getGroupId());
            model.addAttribute("allPermissions", allPermissions);
            model.addAttribute("username", loggedInUser.getUserName());
            model.addAttribute("email", loggedInUser.getEmail());
            model.addAttribute("avatarUrl", "/images/avatar.jpeg");
            model.addAttribute("role", loggedInUser.getRole());

            Users userToEdit = usersService.getUser(id);
            if (userToEdit == null) {
                model.addAttribute("error", "User not found with ID: " + id);
                return "error_page";
            }

            model.addAttribute("user", userToEdit);
            return "users/update_user";

        } catch (Exception e) {

            e.printStackTrace();
            model.addAttribute("error", "Error fetching user details: " + e.getMessage());
            return "error_page";
        }
    }


    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") Long id, @ModelAttribute("user") Users user, Model model) {
        try {
            usersService.updateUser(user);
            return "redirect:/users/list";
        } catch (Exception e) {
            model.addAttribute("error", "Error updating user: " + e.getMessage());
            return "error_page";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, Model model) {
        boolean deleted = usersService.deleteUser(id);

        if (deleted) {
            model.addAttribute("message", "User deleted successfully!");
        } else {
            model.addAttribute("error", "Failed to delete user.");
        }

        return "redirect:/users/list";
    }


}


