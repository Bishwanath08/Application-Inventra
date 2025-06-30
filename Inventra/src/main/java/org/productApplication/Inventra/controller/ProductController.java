package org.productApplication.Inventra.controller;

import org.productApplication.Inventra.jwt.JwtTokenUtil;
import org.productApplication.Inventra.models.DummyJsonProduct;
import org.productApplication.Inventra.models.DummyJsonResponse;
import org.productApplication.Inventra.models.TblProducts;
import org.productApplication.Inventra.models.TblUsers;
import org.productApplication.Inventra.repository.ProductRepository;
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
public class ProductController  extends  BaseController{

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

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


            List<TblProducts> dbProductsPage = productRepository.findAll();
            DummyJsonResponse response = productService.getProducts(0, 1000);
            List<TblProducts> apiProducts = (response != null) ? response.getProducts() : new ArrayList<>();

            List<TblProducts> combinedProducts = new ArrayList<>();
            for (int i = 0; i < dbProductsPage.size() ; i++){
                combinedProducts.add(dbProductsPage.get(i));
            }

            int totalApiProducts = (response != null) ? response.getTotal() : 0;
            int totalDbProducts = (int) productRepository.count();
            int totalProducts = totalApiProducts + totalDbProducts;
            int totalPages = (int) Math.ceil((double) totalProducts / size);



                for (int i = 0; i < apiProducts.size() ; i++){
                    combinedProducts.add(apiProducts.get(i));
                }

            List<TblProducts> finalList = new ArrayList<>();
            for (int i = (page * size); i < ((page + 1) * size) ; i++) {
                finalList.add(combinedProducts.get(i));
            }


            getBasicDeitals(model, jwtToken);
                model.addAttribute("allPermissions", allPermissions);
                model.addAttribute("products",finalList);
                model.addAttribute("currentPage", page);
                model.addAttribute("pageSize", size);
                model.addAttribute("totalProducts", totalProducts);
                model.addAttribute("totalPages", totalPages);

                return "products/product_list";
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
            getBasicDeitals(model, jwtToken);
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
                TblProducts tblProducts = new TblProducts();
                tblProducts.setTitle(product.getTitle());
                tblProducts.setDescription(product.getDescription());
                tblProducts.setPrice(product.getPrice());
                tblProducts.setDiscountPercentage(product.getDiscountPercentage());
                tblProducts.setRating(product.getRating());
                tblProducts.setStock(product.getStock());
                tblProducts.setBrand(product.getBrand());
                tblProducts.setCategory(product.getCategory());
                tblProducts.setThumbnail(product.getThumbnail());

                productRepository.save(tblProducts);

                model.addAttribute("message", "Product added successfully!");
            } else {
                model.addAttribute("error", "Failed to add product: " + response.getStatusCode());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Exception during API call: " + e.getMessage());
        }
        return "redirect:/product/list";
    }


    @GetMapping("/edit/{id}/{dataType}")
    public String showEditProductForm(@PathVariable("id") Long id,
                                      @PathVariable("dataType") String dataType,
                                      Model model, @CookieValue(value = "jwtToken",
            defaultValue = "Guest") String jwtToken) {
        try {
            String email = jwtTokenUtil.extractEmail(jwtToken);
            TblUsers users = userService.getUsersDetails(email);
            List<String> allPermissions = groupService.getPermissionsForGroup(users.getGroupId());
            model.addAttribute("allPermissions", allPermissions);
            getBasicDeitals(model, jwtToken);

            if ("DB".equalsIgnoreCase(dataType)){
                TblProducts product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
                model.addAttribute("product" , product);
                model.addAttribute("dataType", "DB");
                return "products/update_product";

            }else if ("API".equalsIgnoreCase(dataType)) {
                String apiUrl = "https://dummyjson.com/products/" + id;
                DummyJsonProduct product = restTemplate.getForObject(apiUrl, DummyJsonProduct.class);
                model.addAttribute("product", product);
                model.addAttribute("dataType", "API");
                return "products/update_product";

            } else {
                model.addAttribute("error", "Invalid data type: " + dataType);
                return "error_page";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error fetching products: " + e.getMessage());
            return "error_page";}
    }


    @PostMapping("/update/{id}/{dataType}")
    public String updateProduct(@PathVariable("id") Long id,
                                @PathVariable("dataType") String dataType,
                                @ModelAttribute("product") Object product,
                                Model model){
        try {
            if ("DB".equalsIgnoreCase(dataType)){
                TblProducts tblProduct = (TblProducts) product;
                TblProducts existingProduct = productRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("Invalid product Id:" + id));

                existingProduct.setTitle(tblProduct.getTitle());
                existingProduct.setDescription(tblProduct.getDescription());
                existingProduct.setPrice(tblProduct.getPrice());
                existingProduct.setThumbnail(tblProduct.getThumbnail());

                productRepository.save(existingProduct);
                model.addAttribute("message" , "DataBase product Update Successfully");
            } else if ("API".equalsIgnoreCase(dataType)) {
                DummyJsonProduct apiProduct = (DummyJsonProduct) product;
                String apiUrl = "https://dummyjson.com/products/" + id;

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<DummyJsonProduct> request = new HttpEntity<>(apiProduct, headers);

                ResponseEntity<DummyJsonProduct> response = restTemplate.exchange(apiUrl, HttpMethod.PUT, request, DummyJsonProduct.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    model.addAttribute("message", "API product updated successfully!");
                } else {
                    model.addAttribute("error", "Failed to update API product: " + response.getStatusCode());
                }
            } else {
                model.addAttribute("error", "Invalid data type: " + dataType);
                return "error_page";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error updating product: " + e.getMessage());
            return "error_page";
        }
        return dataType;
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
