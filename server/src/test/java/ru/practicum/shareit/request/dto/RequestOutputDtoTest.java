package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class RequestOutputDtoTest {

    @Autowired
    private JacksonTester<RequestOutputDto> jsonTester;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerializeWithCustomDateFormat() throws Exception {
        LocalDateTime testTime = LocalDateTime.of(2025, 1, 1, 12, 30, 0);

        RequestOutputDto dto = RequestOutputDto.builder()
                .id(1L)
                .description("Test description")
                .items(List.of(
                        ItemDto.builder().id(100L).name("Item name").description("Item desc").available(true).build()
                ))
                .created(testTime)
                .build();

        String jsonResult = jsonTester.write(dto).getJson();

        assertThat(jsonResult).contains("\"created\":\"2025-01-01 12:30:00\"");

        assertThat(jsonTester.write(dto))
                .extractingJsonPathStringValue("$.created")
                .isEqualTo("2025-01-01 12:30:00");
    }
}