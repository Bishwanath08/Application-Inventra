package org.productApplication.Inventra.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DummyJsonProduct {
    private Long id;
    private String title;
    private String description;
    private Double price;
    private Double discountPercentage;
    private Double rating;
    private Integer stock;
    private String brand;
    private String category;
    private String thumbnail;
    private String[] images;
}
