package com.really.good.sir.dto;


import com.really.good.sir.annotation.*;
import com.really.good.sir.validation.group.*;

import javax.validation.GroupSequence;

@GroupSequence({
        ServiceDTO.class,
        NameValidGroup.class,
        NameUniqueGroup.class,
        PriceValidGroup.class
})
@UniqueServiceName(groups = NameUniqueGroup.class)
@ValidServicePrice(groups = PriceValidGroup.class)
public class ServiceDTO {
    private Integer id;
    @ValidServiceName(groups = NameValidGroup.class)
    private String name;

    private Integer price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "ServiceDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
