package com.telenor.assignement.util;

import com.telenor.assignement.dto.ResponseDto;
import lombok.SneakyThrows;
import org.assertj.core.api.SoftAssertions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class DataUtilTest {

    @InjectMocks
    private DataHelper dataHelper;

    @SneakyThrows
    @BeforeClass
    public static void setEnv() {
        System.setProperty("csvPath", new ClassPathResource("data.csv").getFile().getAbsolutePath());
    }

    @SneakyThrows
    @AfterClass
    public static void cleanUp() {
        System.clearProperty("csvPath");
    }

    @Test
    public void emptyParamtersShouldReturnAllRecords() {
        ResponseDto actualResponse = dataHelper.getFilteredResponse(null, null, null, null, null, null, null);
        assertEquals(100, actualResponse.getData().size(), "response mismatch!");
    }

    @Test
    public void filteringByTypePhoneShouldReturnPhonesOnly() {
        ResponseDto actualResponse = dataHelper.getFilteredResponse("phone", null, null, null, null, null, null);
        assertEquals(42, actualResponse.getData().size(), "response mismatch!");
    }

    @Test
    public void filteringByTypeSubsShouldReturnSubsOnly() {
        ResponseDto actualResponse = dataHelper.getFilteredResponse("subscription", null, null, null, null, null, null);
        assertEquals(58, actualResponse.getData().size(), "response mismatch!");
    }

    @Test
    public void filteringByWrongTypeShouldReturnEmptyList() {
        ResponseDto actualResponse = dataHelper.getFilteredResponse("wrong", null, null, null, null, null, null);
        assertEquals(0, actualResponse.getData().size(), "response mismatch!");
    }

    @Test
    public void filteringWithNegativeMinPriceRetunsAllItems() {
        ResponseDto actualResponse = dataHelper.getFilteredResponse(null, new BigDecimal(-1), null, null, null, null, null);
        assertEquals(100, actualResponse.getData().size(), "response mismatch!");
    }

    @Test
    public void filteringWithZeroMinPriceRetunsAllItems() {
        ResponseDto actualResponse = dataHelper.getFilteredResponse(null, BigDecimal.ZERO, null, null, null, null, null);
        assertEquals(100, actualResponse.getData().size(), "response mismatch!");
    }

    @Test
    public void filteringWithTooHighMinPriceRetunsAllItems() {
        ResponseDto actualResponse = dataHelper.getFilteredResponse(null, new BigDecimal(Integer.MAX_VALUE), null, null, null, null, null);
        assertEquals(0, actualResponse.getData().size(), "response mismatch!");
    }

    @Test
    public void filteringWithNegativeMaxPriceRetunsEmptyList() {
        ResponseDto actualResponse = dataHelper.getFilteredResponse(null, null, new BigDecimal(-1), null, null, null, null);
        assertEquals(0, actualResponse.getData().size(), "response mismatch!");
    }

    @Test
    public void filteringWithZeroMaxPriceRetunsNoitems() {
        ResponseDto actualResponse = dataHelper.getFilteredResponse(null, null, BigDecimal.ZERO, null, null, null, null);
        assertEquals(0, actualResponse.getData().size(), "response mismatch!");
    }

    @Test
    public void filteringWithTooHighMaxPriceRetunsAllItems() {
        ResponseDto actualResponse = dataHelper.getFilteredResponse(null, null, new BigDecimal(Integer.MAX_VALUE), null, null, null, null);
        assertEquals(100, actualResponse.getData().size(), "response mismatch!");
    }

    @Test
    public void filteringWithPriceRangeReturnsItemsWithValidPricesOnly() {
        BigDecimal minPrice = new BigDecimal(102);
        BigDecimal maxPrice = new BigDecimal(497);
        ResponseDto actualResponse = dataHelper.getFilteredResponse(null, minPrice, maxPrice, null, null, null, null);
        SoftAssertions softAssertions = new SoftAssertions();
        actualResponse.getData().forEach(d -> softAssertions.assertThat(d.getPrice()).isBetween(minPrice, maxPrice));
        softAssertions.assertAll();
    }

    @Test
    public void filteringWithPricesNotOverlappingReutrnsEmptyList() {
        BigDecimal minPrice = new BigDecimal(102);
        BigDecimal maxPrice = new BigDecimal(101);
        ResponseDto actualResponse = dataHelper.getFilteredResponse(null, minPrice, maxPrice, null, null, null, null);
        assertEquals(0, actualResponse.getData().size(), "response mismatch!");
    }

    @Test
    public void filteringWithFullCityNameShouldReturnOnlyValidCities() {
        String city = "Malmö";
        ResponseDto actualResponse = dataHelper.getFilteredResponse(null, null, null, city, null, null, null);
        SoftAssertions softAssertions = new SoftAssertions();
        actualResponse.getData().forEach(d -> softAssertions.assertThat(d.getAddress()).containsIgnoringCase(city));
        softAssertions.assertAll();
    }

    @Test
    public void filteringWithPartialCityNameShouldReturnEmptyList() {
        String city = "Malm";
        ResponseDto actualResponse = dataHelper.getFilteredResponse(null, null, null, city, null, null, null);
        assertEquals(0, actualResponse.getData().size(), "response mismatch!");
    }

    @Test
    public void filteringWithValidStreetNameOnlyShouldReturnEmptyList() {
        String city = "Juston gärdet";
        ResponseDto actualResponse = dataHelper.getFilteredResponse(null, null, null, city, null, null, null);
        assertEquals(0, actualResponse.getData().size(), "response mismatch!");
    }

    @Test
    public void filteringByPropsWithValidColorShouldReturnOnlyValidRecords() {
        String color = "grå";
        ResponseDto actualResponse = dataHelper.getFilteredResponse(null, null, null, null, color, null, null);
        SoftAssertions softAssertions = new SoftAssertions();
        actualResponse.getData().forEach(d -> softAssertions.assertThat(d.getPropValue()).isEqualToIgnoringCase(color));
        softAssertions.assertAll();
    }

    @Test
    public void filteringByPropsWithPartialColorShouldReturnEmptyList() {
        String color = "gr";
        ResponseDto actualResponse = dataHelper.getFilteredResponse(null, null, null, null, color, null, null);
        assertEquals(0, actualResponse.getData().size(), "response mismatch!");
    }

    @Test
    public void filteringByMinGbLimitWithNegativeValueShouldReturnAllSubscriptions() {
        Integer minLimit = -1;
        ResponseDto actualResponse = dataHelper.getFilteredResponse(null, null, null, null, null, minLimit, null);
        assertEquals(58, actualResponse.getData().size(), "response mismatch!");
    }

    @Test
    public void filteringByMinGbLimitWithZeroValueShouldReturnAllSubscriptions() {
        Integer minLimit = 0;
        ResponseDto actualResponse = dataHelper.getFilteredResponse(null, null, null, null, null, minLimit, null);
        assertEquals(58, actualResponse.getData().size(), "response mismatch!");
    }

    @Test
    public void filteringByMinGbLimitWithTooHighValueShouldReturnEmptyList() {
        Integer minLimit = Integer.MAX_VALUE;
        ResponseDto actualResponse = dataHelper.getFilteredResponse(null, null, null, null, null, minLimit, null);
        assertEquals(0, actualResponse.getData().size(), "response mismatch!");
    }

    @Test
    public void filteringByMaxGbLimitWithNegativeValueShouldReturnEmptyList() {
        Integer maxLimit = -1;
        ResponseDto actualResponse = dataHelper.getFilteredResponse(null, null, null, null, null, null, maxLimit);
        assertEquals(0, actualResponse.getData().size(), "response mismatch!");
    }

    @Test
    public void filteringByMaxGbLimitWithZeroValueShouldReturnEmptyList() {
        Integer maxLimit = 0;
        ResponseDto actualResponse = dataHelper.getFilteredResponse(null, null, null, null, null, null, maxLimit);
        assertEquals(0, actualResponse.getData().size(), "response mismatch!");
    }

    @Test
    public void filteringByMaxGbLimitWithTooHighValueShouldReturnAllSubscriptions() {
        Integer maxLimit = Integer.MAX_VALUE;
        ResponseDto actualResponse = dataHelper.getFilteredResponse(null, null, null, null, null, null, maxLimit);
        assertEquals(58, actualResponse.getData().size(), "response mismatch!");
    }

    @Test
    public void filteringByMinMaxLimitWithOverlappingValuesShouldReturnValuesWithinLimitRange() {
        Integer minLimit = 10;
        Integer maxLimit = 50;
        ResponseDto actualResponse = dataHelper.getFilteredResponse(null, null, null, null, null, minLimit, maxLimit);
        SoftAssertions softAssertions = new SoftAssertions();
        actualResponse.getData().forEach(d -> softAssertions.assertThat(Integer.valueOf(d.getPropValue())).isBetween(minLimit, maxLimit));
        softAssertions.assertAll();
    }

    @Test
    public void filteringByMinMaxGbLimitWithNonOverlappingIntervalsShouldReturnEmptyList() {
        Integer maxLimit = 10;
        Integer minLimit = 11;
        ResponseDto actualResponse = dataHelper.getFilteredResponse(null, null, null, null, null, minLimit, maxLimit);
        assertEquals(0, actualResponse.getData().size(), "response mismatch!");
    }

    @Test
    public void filteringWithMutuallyExclusiveValidPropsShouldReturnEMptyList() {
        Integer maxLimit = 0;
        Integer minLimit = 100;
        String color = "grå";
        ResponseDto actualResponse = dataHelper.getFilteredResponse(null, null, null, null, color, minLimit, maxLimit);
        assertEquals(0, actualResponse.getData().size(), "response mismatch!");
    }

    @Test
    public void filteringWithMutuallyExclusivePhoneAndGbLimitShouldReturnEmptyList() {
        Integer maxLimit = 0;
        Integer minLimit = 100;
        String type = "phone";
        ResponseDto actualResponse = dataHelper.getFilteredResponse(type, null, null, null, null, minLimit, maxLimit);
        assertEquals(0, actualResponse.getData().size(), "response mismatch!");
    }

    @Test
    public void filteringWithMutuallyExclusiveSubAndColorShouldReturnEmptyList() {
        String color = "grå";
        String type = "subscription";
        ResponseDto actualResponse = dataHelper.getFilteredResponse(type, null, null, null, color, null, null);
        assertEquals(0, actualResponse.getData().size(), "response mismatch!");
    }
}
