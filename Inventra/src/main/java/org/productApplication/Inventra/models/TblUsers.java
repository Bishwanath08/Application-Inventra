package org.productApplication.Inventra.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@Data
@Table(name = "tbl_users")
public class TblUsers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name ="group_id")
    Long groupId;


    String avatarUrl;

    @Column(name ="name", unique = true, nullable = false)
    @NotBlank(message = "Name  cannot be empty")
    String name;

    @Column(name ="user_name", unique = true, nullable = false)
    @NotBlank(message = "useName cannot be empty")
    String userName;

    @Column(name="email", unique = true, nullable = false)
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Invalid email address.")
    String email;

    private boolean isVerified;

    @Column(unique = true)
    private String token;

    @Column(name="user_role")
    private String role;

    @Column(name="mobile", unique = true, nullable = false)
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Please provide valid mobile number.")
    String mobile;

    @Column(name="password")
    String password;

    @Transient
    String confirmPassword;

    @Column(name="status")
    String status;

    @Column(name="is_deleted")
    String isDeleted;

    @Column(name="login_otp")
    String loginOtp;

    @Column(name="login_otp_generatedAt")
    String OtpGeneratedAt;

    @Column(name="created_ip")
    String createdIp;

    @Column(name="created_at")
    Long createdAt;

    @Column(name="created_by")
    Long createdBy;

    @Column(name="updated_at")
    Long updatedAt;

    @Column(name="updated_by")
    Integer updatedBy;

    @Column(name="last_login_at")
    Long lastLoginAt;

    @Transient
    private String webSecurityToken;

//    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tbl_user_permissions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<TblPermissions> permissions;
}
