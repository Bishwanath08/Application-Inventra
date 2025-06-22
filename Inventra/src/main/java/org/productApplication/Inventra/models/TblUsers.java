package org.productApplication.Inventra.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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


    @Column(name ="parent_id")
    Integer parentId;

    String avatarUrl;

    @Column(name ="name", unique = true, nullable = false)
    String name;

    @Column(name ="user_name", unique = true, nullable = false)
    String userName;

    @Column(name="email", unique = true, nullable = false)
    String email;

    private boolean isVerified;

    @Column(unique = true)
    private String token;

    @Column(name="user_role")
    private String role;

    @Column(name="mobile", unique = true, nullable = false)
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Please provide valid mobile number.")
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
