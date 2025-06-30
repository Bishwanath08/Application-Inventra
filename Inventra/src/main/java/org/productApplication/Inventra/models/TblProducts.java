package org.productApplication.Inventra.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tbl_products")
public class TblProducts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "price")
    private Double price;

    private String description;

//    @Column(name = "status")
//    String status;
//
//    @Column(name = "is_deleted")
//    Boolean isDeleted;
//
//    @Column(name = "created_at")
//    Long createdAt;
//
//    @Column(name = "created_by")
//    Long createdBy;
//
//    @Column(name = "updated_at")
//    Long updatedAt;
//
//    @Column(name = "updated_by")
//    Integer updatedBy;
//
//    @Column(name = "image")
//    String image;

    private Double discountPercentage;
    private Double rating;
    private Integer stock;
    private String brand;
    private String category;
    private String thumbnail;

}
