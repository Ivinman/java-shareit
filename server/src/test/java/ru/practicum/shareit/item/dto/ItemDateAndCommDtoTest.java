package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemDateAndCommDtoTest {
    private final JacksonTester<ItemDateAndCommDto> json;

    @Test
    void testComment() throws Exception {
        ItemDateAndCommDto itemDateAndCommDto = new ItemDateAndCommDto();
        itemDateAndCommDto.setId(1);
        itemDateAndCommDto.setName("itemName");
        itemDateAndCommDto.setDescription("desc");
        itemDateAndCommDto.setAvailable(true);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        itemDateAndCommDto.setLastBooking(LocalDateTime.parse("2022.07.03 19:55:10", formatter));
        itemDateAndCommDto.setNextBooking(LocalDateTime.parse("2022.08.03 19:55:10", formatter));
        itemDateAndCommDto.setComments(new ArrayList<>());

        JsonContent<ItemDateAndCommDto> result = json.write(itemDateAndCommDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("itemName");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("desc");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking")
                .isEqualTo(itemDateAndCommDto.getLastBooking().toString());
        assertThat(result).extractingJsonPathStringValue("$.nextBooking")
                .isEqualTo(itemDateAndCommDto.getNextBooking().toString());
        assertThat(result).extractingJsonPathArrayValue("$.comments")
                .isEqualTo(itemDateAndCommDto.getComments());
    }
}