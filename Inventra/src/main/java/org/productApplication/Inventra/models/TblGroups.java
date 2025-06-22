package org.productApplication.Inventra.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "tbl_groups")
public class TblGroups {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'active'")
    private String status = "active";

    @Column(name = "is_deleted", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'false'")
    private String isDeleted = "false";

    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    @Column(name = "created_by", nullable = false)
    private Integer createdBy;

    @Column(name = "updated_at")
    private Long updatedAt;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private TblUsers creator;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tbl_group_permissions",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<TblPermissions> permissions;
}