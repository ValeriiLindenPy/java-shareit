package ru.practicum.shareit;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.Validation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.ValidationMarker;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ShareItTests {
	private Validator validator;

	@Autowired
	private ItemService itemService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	private User savedUser;

	private ItemDto testItemDto;

	@BeforeEach
	void setUp() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
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
	void createUser() {
		UserDto user = UserDto.builder().name("Test User")
				.email("testusernew@example.com").build();


		UserDto newUser = userService.create(user);

		assertNotNull(newUser.getId());
		assertEquals(newUser.getName(), user.getName());
		assertEquals(newUser.getEmail(), user.getEmail());
	}

	@Test
	void createUserFailId() {
		UserDto user = UserDto.builder().id(2L).name("Test User")
				.email("testusernew@example.com").build();


		Set<ConstraintViolation<UserDto>> violations = validator
				.validate(user, ValidationMarker.OnCreate.class);

		assertThat(violations).isNotEmpty().extracting(ConstraintViolation::getMessage)
				.containsExactlyInAnyOrder("Id should be null");
	}

	@Test
	void createUserFailName() {
		UserDto user = UserDto.builder().name("")
				.email("testusernew@example.com").build();


		Set<ConstraintViolation<UserDto>> violations = validator
				.validate(user, ValidationMarker.OnCreate.class);

		assertThat(violations).isNotEmpty().extracting(ConstraintViolation::getMessage)
				.containsExactlyInAnyOrder("Name can't be blank");
	}

	@Test
	void createUserFailEmail() {
		UserDto user = UserDto.builder().name("Name")
				.email("").build();


		Set<ConstraintViolation<UserDto>> violations = validator
				.validate(user, ValidationMarker.OnCreate.class);

		assertThat(violations).isNotEmpty().extracting(ConstraintViolation::getMessage)
				.containsExactlyInAnyOrder("Email can't be blank");
	}

	@Test
	void createUserFailEmailFormat() {
		UserDto user = UserDto.builder().name("Name")
				.email("testuserexamplecom").build();


		Set<ConstraintViolation<UserDto>> violations = validator
				.validate(user, ValidationMarker.OnCreate.class,
						ValidationMarker.OnUpdate.class);

		assertThat(violations).isNotEmpty().extracting(ConstraintViolation::getMessage)
				.containsExactlyInAnyOrder("Wrong email format");
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
	void createItemFailName() {
		ItemDto item = ItemDto.builder()
				.name("")
				.description("Just a test item")
				.available(true)
				.build();

		Set<ConstraintViolation<ItemDto>> violations = validator
				.validate(item, ValidationMarker.OnCreate.class);

		assertThat(violations).isNotEmpty().extracting(ConstraintViolation::getMessage)
				.containsExactlyInAnyOrder("Name can't be blank");
	}

	@Test
	void createItemFailIdNull() {
		ItemDto item = ItemDto.builder()
				.id(2L)
				.name("Item")
				.description("Just a test item")
				.available(true)
				.build();

		Set<ConstraintViolation<ItemDto>> violations = validator.validate(item, ValidationMarker.OnCreate.class);

		assertThat(violations).isNotEmpty().extracting(ConstraintViolation::getMessage)
				.containsExactlyInAnyOrder("Id should be null");
	}

	@Test
	void createItemFailDescription() {
		ItemDto item = ItemDto.builder()
				.name("Item")
				.description("")
				.available(true)
				.build();

		Set<ConstraintViolation<ItemDto>> violations = validator.validate(item, ValidationMarker.OnCreate.class);

		assertThat(violations).isNotEmpty().extracting(ConstraintViolation::getMessage)
				.containsExactlyInAnyOrder("Description can't be blank");
	}

	@Test
	void createItemFailAvailable() {
		ItemDto item = ItemDto.builder()
				.name("Item")
				.description("Item desc")
				.build();

		Set<ConstraintViolation<ItemDto>> violations = validator.validate(item, ValidationMarker.OnCreate.class);

		assertThat(violations).isNotEmpty().extracting(ConstraintViolation::getMessage)
				.containsExactlyInAnyOrder("available shouldn't be null");
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
		itemService.create(testItemDto, savedUser.getId());
		List<ItemDto> items = itemService.searchByText("Some");
		assertEquals(1, items.size());
	}
}
