package org.productApplication.Inventra.service;

import org.productApplication.Inventra.models.TblUsers;
import org.productApplication.Inventra.models.Users;
import org.productApplication.Inventra.DTO.UsersResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class UsersService {

    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);

    @Autowired
    private RestTemplate restTemplate;


    public UsersResponse getUsers(int page, int size) {
        int skip = page * size;

        String url = UriComponentsBuilder.fromHttpUrl("https://dummyjson.com/users")
                .queryParam("limit", size)
                .queryParam("skip", skip)
                .toUriString();

        try {
            ResponseEntity<UsersResponse> response = restTemplate.getForEntity(url, UsersResponse.class);
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error fetching users: " + e.getMessage());
            return null;
        }
    }

    public Users addUser(Users newUser) {
        String url = "https://dummyjson.com/users/add";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Users> requestEntity = new HttpEntity<>(newUser, headers);

        try {
            ResponseEntity<Users> responseEntity = restTemplate.postForEntity(url, requestEntity, Users.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            } else {
                System.err.println("Error adding user: " + responseEntity.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error adding user: " + e.getMessage());
            return null;
        }
    }
//    public Users updateUser(Users newUser, String id) {
//        String url = "https://dummyjson.com/users/update/"+id;
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<Users> requestEntity = new HttpEntity<>(newUser, headers);
//        try {
//            ResponseEntity<Users> responseEntity = restTemplate.postForEntity(url, requestEntity, Users.class);
//            if (responseEntity.getStatusCode().is2xxSuccessful()) {
//                return responseEntity.getBody();
//            } else {
//                System.err.println("Error updating user: " + responseEntity.getStatusCode());
//                return null;
//            }
//        } catch (Exception e) {
//            System.err.println("Error updating user: " + e.getMessage());
//            return null;
//        }
//    }

    public Users getUser(Long id) {
        String apiUrl = "https://dummyjson.com/users/" + id;
        try {
            ResponseEntity<Users> response = restTemplate.getForEntity(apiUrl, Users.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                logger.warn("User not found or error fetching user from API: Status Code = {}", response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            logger.error("Error fetching user from API: ", e);
            return null;
        }
    }

    public void updateUser(Users user) {
        String apiUrl = "https://dummyjson.com/users/" + user.getId();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Users> requestEntity = new HttpEntity<>(user, headers);

        try {
            ResponseEntity<Users> response = restTemplate.exchange(apiUrl, HttpMethod.PUT, requestEntity, Users.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                logger.error("Failed to update user.  Status Code: {}", response.getStatusCode());

            }
        } catch (Exception e) {
            logger.error("Exception during API call: ", e);

        }
    }
    public boolean deleteUser(Long userId) {
        String apiUrl = "https://dummyjson.com/users/" + userId;

        try {
            restTemplate.delete(apiUrl);
            logger.info("User with ID {} deleted successfully", userId);
            return true; // Indicate successful deletion
        } catch (Exception e) {
            logger.error("Error deleting user with ID {}: {}", userId, e.getMessage());
            return false; // Indicate deletion failure
        }
    }

}

