package org.productApplication.Inventra.controller;

import org.productApplication.Inventra.jwt.JwtTokenUtil;
import org.productApplication.Inventra.models.DummyJsonProduct;
import org.productApplication.Inventra.models.DummyJsonResponse;
import org.productApplication.Inventra.models.TblUsers;
import org.productApplication.Inventra.service.GroupService;
import org.productApplication.Inventra.service.ProductService;
import org.productApplication.Inventra.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private GroupService groupService;
    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public String listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            Model model, @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {
        try {
            String email = jwtTokenUtil.extractEmail(jwtToken);
            TblUsers users = userService.getUsersDetails(email);
            List<String> allPermissions = groupService.getPermissionsForGroup(users.getGroupId());

            DummyJsonResponse response = productService.getProducts(page, size);

            model.addAttribute("username", users.getUserName());
            model.addAttribute("email", users.getEmail());
            model.addAttribute("avatarUrl", "/images/avatar.jpeg");
            model.addAttribute("role", users.getRole());

            if (response != null) {

            model.addAttribute("allPermissions", allPermissions);
            model.addAttribute("products", response.getProducts());
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("totalProducts", response.getTotal());
            int totalPages = (int) Math.ceil((double) response.getTotal() / size);
            model.addAttribute("totalPages", totalPages);

            return "products/product_list";
        } else {
            model.addAttribute("error", "Failed to fetch products");
            return "error_page";
        }
    }catch (Exception e){
        e.printStackTrace();
        model.addAttribute("error", "Error fetching products: " + e.getMessage());
        return "error_page";
        }
    }


    @GetMapping("/add")
    public String showAddProductForm(Model model, @CookieValue(value = "jwtToken",
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
            model.addAttribute("product", new DummyJsonProduct());
            return "products/add_product";
        }catch (Exception e){
            e.printStackTrace();
            model.addAttribute("error", "Error fetching products: " + e.getMessage());
            return "error_page";
        }
    }

    @PostMapping("/add")
    public String addProduct(@ModelAttribute("product") DummyJsonProduct product, Model model) {
        String apiUrl = "https://dummyjson.com/products/add";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<DummyJsonProduct> request = new HttpEntity<>(product, headers);

        try {
            ResponseEntity<DummyJsonProduct> response = restTemplate.postForEntity(apiUrl, request, DummyJsonProduct.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                model.addAttribute("message", "Product added successfully!");
            } else {
                model.addAttribute("error", "Failed to add product: " + response.getStatusCode());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Exception during API call: " + e.getMessage());
        }
        return "redirect:/product/list";
    }


    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable("id") Long id, Model model, @CookieValue(value = "jwtToken",
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
            String apiUrl = "https://dummyjson.com/products/" + id;
            DummyJsonProduct product = restTemplate.getForObject(apiUrl, DummyJsonProduct.class);
            model.addAttribute("product", product);
            return "products/update_product";
        } catch (Exception e) {
        e.printStackTrace();
        model.addAttribute("error", "Error fetching products: " + e.getMessage());
        return "error_page";}
    }


    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable("id") Long id, @ModelAttribute("product") DummyJsonProduct product, Model model) {
        String apiUrl = "https://dummyjson.com/products/" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<DummyJsonProduct> request = new HttpEntity<>(product, headers);

        try {
            ResponseEntity<DummyJsonProduct> response = restTemplate.exchange(apiUrl, HttpMethod.PUT, request, DummyJsonProduct.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                model.addAttribute("message", "Product updated successfully!");
            } else {
                model.addAttribute("error", "Failed to update product: " + response.getStatusCode());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Exception during API call: " + e.getMessage());
        }
        return "redirect:/product/list";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, Model model) {

        String apiUrl = "https://dummyjson.com/products/" + id;

        try {
            restTemplate.delete(apiUrl);
            model.addAttribute("message", "Product deleted successfully!");
            return "redirect:/product/list";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to delete product: " + e.getMessage());
        }
        return "redirect:/product/list";
    }
}
