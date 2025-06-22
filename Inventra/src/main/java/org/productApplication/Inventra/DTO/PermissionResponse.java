package org.productApplication.Inventra.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public  class PermissionResponse {
    private List<String> permissions;

    public PermissionResponse(List<String> permissions) {
        this.permissions = permissions;
    }

}

