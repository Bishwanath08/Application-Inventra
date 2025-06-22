package org.productApplication.Inventra.service;

import org.productApplication.Inventra.models.DummyJsonResponse;
import org.productApplication.Inventra.models.TblProducts;
import org.productApplication.Inventra.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;


@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

//    public void saveProduct(TblProducts products) {
//        productRepository.save(product);
//    }
//
//    public Optional<TblProducts> getProductById(Long id) {
//        return productRepository.findById(id);
//    }
//
//    public List<TblProducts> getAllProducts() {
//        return productRepository.findAll();
//    }
//
//    public void deleteProduct(Long productId) {
//        productRepository.deleteById(productId);
//    }
//
//
//
//    public Page<TblProducts> getAllProductsPaged(int pageNumber, int pageSize, String sortField, String sortDirection) {
//        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
//        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
//        return this.productRepository.findAll(pageable);
//    }

    private final String API_URL = "https://dummyjson.com/products";

    public List<TblProducts> fetchProductsFromApi() {
        RestTemplate restTemplate = new RestTemplate();
        ApiResponse response = restTemplate.getForObject(API_URL, ApiResponse.class);

        if (response != null && response.getProducts() != null) {
            return Arrays.asList(response.getProducts());
        } else {
            return List.of();
        }
    }

    private static class ApiResponse {
        private TblProducts[] products;

        public TblProducts[] getProducts() {
            return products;
        }

        public void setProducts(TblProducts[] products) {
            this.products = products;
        }
    }

    public List<TblProducts> fetchAddProductsFromApi() {
        RestTemplate restTemplate = new RestTemplate();
        TblProducts[] products = restTemplate.getForObject(API_URL, TblProducts[].class);
        return products != null ? List.of(products) : List.of();
    }

    public Long addProductToApi(TblProducts product) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TblProducts> request = new HttpEntity<>(product, headers);

        try {
            TblProducts apiResponse = restTemplate.postForObject(API_URL + "/add", request, TblProducts.class);

            if (apiResponse != null && apiResponse.getId() != null) {
                return apiResponse.getId();
            } else {
                System.err.println("Error adding product to API or API did not return an ID!");
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error adding product to API: " + e.getMessage());
            return null;
        }
    }

    public DummyJsonResponse getProducts(int page, int pageSize) {
        RestTemplate restTemplate = new RestTemplate();
        int skip = page * pageSize;
        String url = API_URL + "?limit=" + pageSize + "&skip=" + skip;

        try {
            ResponseEntity<DummyJsonResponse> response = restTemplate.getForEntity(url, DummyJsonResponse.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                System.err.println("Error fetching products: " + response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Exception during API call: " + e.getMessage());
            return null;
        }
    }
}
