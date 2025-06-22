package org.productApplication.Inventra.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.productApplication.Inventra.jwt.JwtTokenUtil;
import org.productApplication.Inventra.models.TblUsers;
import org.productApplication.Inventra.service.SubAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


public  class BaseController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private SubAdminService subAdminService;

    public TblUsers getLoggedUserViaRequest(HttpServletRequest request, Model model, @CookieValue(value = "jwtToken",
            defaultValue = "Guest") String jwtToken) {
        String email = jwtTokenUtil.extractEmail(jwtToken);
        TblUsers users = subAdminService.getSubAdminDetails(email);
        return users;
    }

}
