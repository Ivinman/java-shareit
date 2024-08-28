package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestRespDtoTest {
    private final JacksonTester<ItemRequestRespDto> json;

    @Test
    void testComment() throws Exception {
        ItemRequestRespDto itemRequestRespDto = new ItemRequestRespDto();
        itemRequestRespDto.setDescription("desc");
        itemRequestRespDto.setRequestor(new User());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        itemRequestRespDto.setCreated(LocalDateTime.parse("2022.07.03 19:55:10", formatter));

        JsonContent<ItemRequestRespDto> result = json.write(itemRequestRespDto);

        assertThat(result).extractingJsonPathNumberValue("$.requestor.id").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("desc");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(LocalDateTime.parse("2022.07.03 19:55:10", formatter).toString());
    }
}