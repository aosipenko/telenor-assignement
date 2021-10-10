package com.telenor.assignement.controller;

import com.telenor.assignement.util.DataHelper;
import com.telenor.assignement.dto.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class HttpController {

    @Autowired
    private DataHelper dataHelper;

    @GetMapping("/product")
    public ResponseEntity<ResponseDto> getFilteredProducts(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "min_price", required = false) BigDecimal priceMin,
            @RequestParam(name = "max_price", required = false) BigDecimal priceMax,
            @RequestParam(name = "city", required = false) String city,
            @RequestParam(name = "color", required = false) String color,
            @RequestParam(name = "gb_limit_min", required = false) Integer memLimitMin,
            @RequestParam(name = "gb_limit_max", required = false) Integer memLimitMax
    ) {
        return ResponseEntity.ok(dataHelper.getFilteredResponse(type, priceMin, priceMax, city, color, memLimitMin, memLimitMax));
    }
}
