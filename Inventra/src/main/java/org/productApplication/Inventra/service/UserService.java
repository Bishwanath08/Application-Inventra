package org.productApplication.Inventra.service;


import org.productApplication.Inventra.jwt.JwtTokenUtil;
import org.productApplication.Inventra.models.TblUsers;
import org.productApplication.Inventra.repository.TblUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private TblUsersRepository usersRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;


//    public boolean authenticateUser(String email, String password) {
//        TblUsers user = usersRepository.findByEmail(email);
//        if (user == null) {
//            return false;
//        }
//        boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
//        System.out.println("Password matches: " + passwordMatches);
//        return passwordMatches;
//    }

    public String login(String email, String password) {
        TblUsers users = usersRepository.findByEmail(email);

        if (users != null) {

            if (passwordEncoder.matches(password, users.getPassword())) {
                String otp = generateOTP(4, users);
                usersRepository.save(users);
                return otp;
            }else {
                return null;
            }
        }else {
            return null;
        }
    }

    public String generateOTP(int length, TblUsers users) {
        String digits = "0123456789";
        StringBuilder otp = new StringBuilder(length);
        Random random = new Random();

        for (int i = 0; i < length; i++){
            otp.append(digits.charAt(random.nextInt(digits.length())));
        }

        String generatedOtp = "1111";
        users.setLoginOtp(generatedOtp);
        users.setOtpGeneratedAt(String.valueOf(LocalDateTime.now()));
        users.setVerified(false);

//        System.out.println("YOUR OTP IS ::::: " + generatedOtp);
        return "1111";
    }

    public String verifyOtp(String email, String otp) {
        System.out.println("verifyOtp called with email: " + email + ", otp: " + otp);

        TblUsers users = usersRepository.findByEmail(email);

        if (users != null) {
            System.out.println("TblUsers found: " + users.getEmail());

            if (users.getLoginOtp() != null && users.getLoginOtp().equals(otp)) {
                System.out.println("OTP is valid");

                try {
                    final UserDetails userDetails = loadUserByEmail(email);

                    System.out.println("Generating JWT token for user: " + email);
                    final String jwt = jwtUtil.generateToken(userDetails.getUsername(), "ROLE_ADMIN");
                    System.out.println("JWT token generated: " + jwt);
                    users.setWebSecurityToken(jwt);

                    System.out.println("Clearing OTP for user: " + email);
                    users.setLoginOtp(null);
                    usersRepository.save(users);
                    System.out.println("OTP cleared for user: " + email);

                    return jwt;

                } catch (Exception e) {
                    System.err.println("Exception during JWT generation: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            } else {
                System.out.println("OTP is invalid");
            }
        } else {
            System.out.println("TblUsers not found for email: " + email);
        }

        return null;
    }

    public UserDetails loadUserByEmail(String email) {
        TblUsers users = usersRepository.findByEmail(email);

        if (users != null) {
            return new User(users.getEmail(), users.getPassword(), new ArrayList<>());
        }
        return null;
    }

    public TblUsers getUsersDetails(String email) {
        if (email==null){
        }
        return  usersRepository.findByEmail(email);
    }
}
