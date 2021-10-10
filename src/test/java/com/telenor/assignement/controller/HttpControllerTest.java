package com.telenor.assignement.controller;

import com.telenor.assignement.dto.ResponseDto;
import com.telenor.assignement.util.DataHelper;
import lombok.SneakyThrows;
import org.assertj.core.util.Lists;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class HttpControllerTest {

    @InjectMocks
    private HttpController httpController;

    @Mock
    private DataHelper dataHelper;

    @SneakyThrows
    @BeforeClass
    public static void setEnv() {
        System.setProperty("csvPath", new ClassPathResource("data.csv").getFile().getAbsolutePath());
    }

    @Test
    public void emptyParamtersShouldReturnAllRecords() {
        when(dataHelper.getFilteredResponse(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new ResponseDto(Lists.emptyList()));
        ResponseEntity<ResponseDto> response = httpController.getFilteredProducts(null, null, null, null, null, null, null);
        assertEquals(0, Objects.requireNonNull(response.getBody()).getData().size(), "response must be empty");
        verify(dataHelper, times(1)).getFilteredResponse(null, null, null, null, null, null, null);
    }
}
