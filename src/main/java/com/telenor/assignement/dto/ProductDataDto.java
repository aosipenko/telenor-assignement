package com.telenor.assignement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDataDto {

    @CsvBindByPosition(position = 0)
    @JsonProperty("type")
    private String type;
    @CsvBindByPosition(position = 1)
    @JsonProperty("properties")
    private String prop;
    @CsvBindByPosition(position = 2)
    @JsonProperty("price")
    private BigDecimal price;
    @CsvBindByPosition(position = 3)
    @JsonProperty("store_address")
    private String address;

    public String getPropValue() {
        return this.prop.split(":")[1];
    }

    public String getCity() {
        return this.address.split(",")[1].trim();
    }
}
