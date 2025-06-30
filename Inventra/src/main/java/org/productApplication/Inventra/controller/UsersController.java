package org.productApplication.Inventra.controller;

import org.productApplication.Inventra.DTO.UsersResponse;
import org.productApplication.Inventra.jwt.JwtTokenUtil;
import org.productApplication.Inventra.models.TblUsers;
import org.productApplication.Inventra.models.Users;
import org.productApplication.Inventra.repository.UserRepository;
import org.productApplication.Inventra.service.GroupService;
import org.productApplication.Inventra.service.UserService;
import org.productApplication.Inventra.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/users")
public class UsersController extends  BaseController {

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

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/list")
    public String listAllUsers( @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "25") int size,
                                Model model, @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {
        try {
            String email = jwtTokenUtil.extractEmail(jwtToken);
            TblUsers users = userService.getUsersDetails(email);
            List<String> allPermissions = groupService.getPermissionsForGroup(users.getGroupId());

            List<Users> dbUserPage = userRepository.findAll();
            UsersResponse response = usersService.getUsers(0, 1000);
            List<Users> apiUsers = (response!= null) ? response.getUsers() : new ArrayList<>();

            List<Users> combinedUsers = new ArrayList<>();
            for (int i = 0; i < dbUserPage.size(); i++) {
                combinedUsers.add(dbUserPage.get(i));
            }

            int totalApiUser = (response != null) ? response.getTotal() : 0;
            int totalDbUser = (int) userRepository.count();
            int totalUser = totalApiUser + totalDbUser;
            int totalPages = (int) Math.ceil((double) totalUser / size);

            for (int i = 0; i < apiUsers.size(); i++) {
                combinedUsers.add(apiUsers.get(i));
            }

            List<Users> finalList = new ArrayList<>();
            for (int i = (page * size); i < ((page + 1) * size) ; i++) {
                finalList.add(combinedUsers.get(i));
            }

            getBasicDeitals(model, jwtToken);
            model.addAttribute("allPermissions", allPermissions);
            model.addAttribute("users", finalList);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("totalUsers", totalUser);
            model.addAttribute("totalPages", totalPages);

                return "users/users_list";

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
           getBasicDeitals(model, jwtToken);
            model.addAttribute("user", new Users());
            return "users/add_user";
        }catch (Exception e){
            e.printStackTrace();
            model.addAttribute("error", "Error fetching users: " + e.getMessage());
            return "error_page";
        }
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute("user") Users newUser, Model model) {
//        Users createdUser = usersService.addUser(user);
//
//        if (createdUser != null) {
//            model.addAttribute("message", "User added successfully!");
//            return "redirect:/users/list";
//        } else {
//            model.addAttribute("error", "Failed to add user");
//            return "error_page";
//        }
        String url = "https://dummyjson.com/users/add";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Users> requestEntity = new HttpEntity<>(newUser, headers);

        try {
            ResponseEntity<Users> responseEntity = restTemplate.postForEntity(url, requestEntity, Users.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                Users users = new Users();
                users.setFirstName(newUser.getFirstName());
                users.setLastName(newUser.getLastName());
                users.setEmail(newUser.getEmail());
                users.setPhone(newUser.getPhone());

                userRepository.save(users);

                model.addAttribute("message", "Product added successfully");
            } else {
                model.addAttribute("error", "Failed to add product: " + responseEntity.getStatusCode());
            }
        }catch (Exception e ) {
            model.addAttribute("error ", "Exception during API call: " + e.getMessage());
        }
        return "redirect:/users/list";
    }

    @GetMapping("/edit/{id}/{dataType}")
    public String showEditUserForm(@PathVariable("id") Long id,
                                   @PathVariable("dataType") String dataType,
                                   Model model,
                                   @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {
        try {
            String email = jwtTokenUtil.extractEmail(jwtToken);
            TblUsers loggedInUser = userService.getUsersDetails(email);
            List<String> allPermissions = groupService.getPermissionsForGroup(loggedInUser.getGroupId());
            model.addAttribute("allPermissions", allPermissions);
            getBasicDeitals(model, jwtToken);

            if ("DB".equalsIgnoreCase(dataType)) {
                Users user = userRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid user Id: " + id));
                model.addAttribute("user", user);  // Use "user" consistently
            } else if ("API".equalsIgnoreCase(dataType)) {
                String apiUrl = "https://dummyjson.com/users/" + id;
                Users user = restTemplate.getForObject(apiUrl, Users.class);
                model.addAttribute("user", user); // Use "user" consistently
            } else {
                model.addAttribute("error", "Invalid data type: " + dataType);
                return "error_page";
            }
            model.addAttribute("dataType", dataType); // Add data type to the model
            return "users/update_user";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error fetching user details: " + e.getMessage());
            return "error_page";
        }
    }



    @PostMapping("/update/{id}/{dataType}")
    public String updateUser(@PathVariable("id") Long id, @PathVariable("dataType") String dataType,
                             @ModelAttribute("user") Users user, Model model) {
        try {
            if ("DB".equalsIgnoreCase(dataType)) {

                Users existingUser = userRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid user Id: " + id));
                existingUser.setFirstName(user.getFirstName());
                existingUser.setLastName(user.getLastName());
                existingUser.setEmail(user.getEmail());
                existingUser.setPhone(user.getPhone());

                userRepository.save(existingUser);
                model.addAttribute("message", "DataBase users Update successfully");

            } else if ("API".equalsIgnoreCase(dataType)) {
                String apiUrl = "https://dummyjson.com/users/" + id;

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Users> request = new HttpEntity<>(user, headers); // Use the 'user' object

                ResponseEntity<Users> response = restTemplate.exchange(apiUrl, HttpMethod.PUT, request, Users.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    model.addAttribute("message", "API users Updated Successfully!");
                } else {
                    model.addAttribute("error", "Failed to update API users: " + response.getStatusCode());
                }
            } else {
                model.addAttribute("error", "Invalid data type: " + dataType);
                return "error_page";
            }
            return "redirect:/users/list"; // Redirect to the list page
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


