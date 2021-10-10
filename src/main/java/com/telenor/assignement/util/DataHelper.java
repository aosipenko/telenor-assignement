package com.telenor.assignement.util;

import com.opencsv.bean.CsvToBeanBuilder;
import com.telenor.assignement.dto.ProductDataDto;
import com.telenor.assignement.dto.ResponseDto;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class DataHelper {

    private final static String GB_LIMIT_PREFIX = "gb_limit:";
    private final static String COLOR_PREFIX = "color:";
    private final static String CSV_PROP = "csvPath";
    private final static String CSV_DOCKER_LOCATION = "/entry/data.csv";

    private final static String CSV_PATH = System.getProperty(CSV_PROP, CSV_DOCKER_LOCATION);


    public ResponseDto getFilteredResponse(String type, BigDecimal minPrice, BigDecimal maxPrice, String city,
                                           String color, Integer gbLimitMin, Integer gbLimitMax) {
        return new ResponseDto(getFilteredData(type, minPrice, maxPrice, city, color, gbLimitMin, gbLimitMax));
    }

    private List<ProductDataDto> getFilteredData(String type, BigDecimal minPrice, BigDecimal maxPrice, String city,
                                                 String color, Integer gbLimitMin, Integer gbLimitMax) {

        return getData().stream()
                // if type is not set - grab all types, other filters will sort it out
                .filter(dto -> {
                    if (StringUtils.isEmpty(type)) {
                        return true;
                    } else {
                        return dto.getType().equalsIgnoreCase(type);
                    }
                })
                // if city is empty - search in every city
                .filter(dto -> {
                    if (StringUtils.isEmpty(city)) {
                        return true;
                    } else {
                        //extract city name
                        return dto.getCity().equalsIgnoreCase(city);
                    }
                })
                // if prices are not set - get all prices
                .filter(dto -> dto.getPrice().compareTo(minPrice == null ? BigDecimal.ZERO : minPrice) >= 0)
                .filter(dto -> dto.getPrice().compareTo(maxPrice == null ? BigDecimal.valueOf(Long.MAX_VALUE) : maxPrice) <= 0)
                .filter(dto -> filterProps(dto, color, gbLimitMin, gbLimitMax)).collect(Collectors.toList());
    }

    private boolean filterProps(ProductDataDto dto, String color, Integer gbLimitMin, Integer gbLimitMax) {
        // if no filters reqeusted - do not filter at all
        if (StringUtils.isEmpty(color) && gbLimitMin == null && gbLimitMax == null) {
            return true;
        }
        // color filter is mutually exclusive with any of gb limit filters
        if (!StringUtils.isEmpty(color) && (gbLimitMin != null || gbLimitMax != null)) {
            return false;
        }
        if (StringUtils.isEmpty(color) && dto.getProp().contains(GB_LIMIT_PREFIX)) {
            // no color filtering is request - filter by gb limits
            int limit = Integer.parseInt(dto.getPropValue());
            return limit >= (gbLimitMin == null ? 0 : gbLimitMin) &&
                    limit <= (gbLimitMax == null ? Integer.MAX_VALUE : gbLimitMax);
        } else if (!StringUtils.isEmpty(color) && dto.getProp().contains(COLOR_PREFIX)) {
            // only color filter is present by this point. Filter by colors.
            return dto.getPropValue().equalsIgnoreCase(color);
        } else {
            // so far we eliminated all viable possibilities, and any request that got here is inconsistent with our data structure
            return false;
        }
    }

    @SneakyThrows
    private List<ProductDataDto> getData() {
        return new CsvToBeanBuilder<ProductDataDto>
                (new FileReader(CSV_PATH))
                .withType(ProductDataDto.class)
                .withSkipLines(1)
                .build()
                .parse();
    }
}
