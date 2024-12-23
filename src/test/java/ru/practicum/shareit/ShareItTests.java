package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ShareItTests {

	@Autowired
	private ItemService itemService;

	@Autowired
	private UserRepository userRepository;

	private User savedUser;

	private ItemDto testItemDto;

	@BeforeEach
	void setUp() {

		User user = new User();
		user.setName("Test User");
		user.setEmail("testuser@example.com");
		savedUser = userRepository.save(user);

		testItemDto = ItemDto.builder()
				.name("Some Item")
				.description("Just a test item")
				.available(true)
				.build();

	}

	@AfterEach
	void prepare() {
		userRepository.clear();
		itemService.clear();
	}

	@Test
	void contextLoads() {
		assertNotNull(itemService);
		assertNotNull(userRepository);
	}

	@Test
	void createItem() {
		ItemDto createdItem = itemService.create(testItemDto, savedUser.getId());

		assertNotNull(createdItem.getId());
		assertEquals(testItemDto.getName(), createdItem.getName());
		assertEquals(testItemDto.getDescription(), createdItem.getDescription());
		assertTrue(createdItem.getAvailable());
	}

	@Test
	void createItemWithNonExistUser() {
		assertThrows(NotFoundException.class, () -> {
			itemService.create(testItemDto, 100L);
		});
	}

	@Test
	void editItemName() {
		ItemDto updateRequest = ItemDto.builder()
				.name("New name")
				.build();

		ItemDto savedItemDto = itemService.create(testItemDto, savedUser.getId());

		ItemDto updated = itemService.editOne(savedItemDto.getId(), updateRequest, savedUser.getId());

		assertEquals(savedItemDto.getId(), updated.getId());
		assertEquals("New name", updated.getName());
		assertEquals("Just a test item", updated.getDescription());
		assertTrue(updated.getAvailable());
	}

	@Test
	void editItemDescription() {
		ItemDto updateRequest = ItemDto.builder()
				.description("New description")
				.build();

		ItemDto savedItemDto = itemService.create(testItemDto, savedUser.getId());

		ItemDto updated = itemService.editOne(savedItemDto.getId(), updateRequest, savedUser.getId());

		assertEquals(savedItemDto.getId(), updated.getId());
		assertEquals("Some Item", updated.getName());
		assertEquals("New description", updated.getDescription());
		assertTrue(updated.getAvailable());
	}

	@Test
	void editItemAvailable() {
		ItemDto updateRequest = ItemDto.builder()
				.available(false)
				.build();

		ItemDto savedItemDto = itemService.create(testItemDto, savedUser.getId());

		ItemDto updated = itemService.editOne(savedItemDto.getId(), updateRequest, savedUser.getId());

		assertEquals(savedItemDto.getId(), updated.getId());
		assertEquals("Some Item", updated.getName());
		assertEquals("Just a test item", updated.getDescription());
		assertFalse(updated.getAvailable());
	}

	@Test
	void search() {
		ItemDto savedItemDto = itemService.create(testItemDto, savedUser.getId());
		List<ItemDto> items = itemService.searchByText("Some");
		assertEquals(1, items.size());
	}

}
