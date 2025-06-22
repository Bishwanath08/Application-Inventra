package org.productApplication.Inventra.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DummyJsonResponse {
    private List<TblProducts> products;
    private Integer total;
    private Integer skip;
    private Integer limit;
}
