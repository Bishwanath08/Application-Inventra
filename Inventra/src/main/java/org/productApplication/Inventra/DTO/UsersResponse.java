package org.productApplication.Inventra.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import org.productApplication.Inventra.models.Users;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsersResponse {
    private List<Users> users;
    private int total;
    private int skip;
    private int limit;
}