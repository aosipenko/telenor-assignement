package com.telenor.assignement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ResponseDto {
    private List<ProductDataDto> data;
}
