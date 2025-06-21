package com.example.quickrx.controller;

import com.example.quickrx.dto.CategoryRequestDto;
import com.example.quickrx.dto.CategoryResponseDto;
import com.example.quickrx.model.Category;
import com.example.quickrx.repository.CategoryRepository;
import com.example.quickrx.service.FileStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional; // Important for test data cleanup

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Rollback transactions after each test
public class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository; // For setup or verification if needed

    @MockBean
    private FileStorageService fileStorageService;

    private CategoryRequestDto categoryRequestDto;

    @BeforeEach
    void setUp() {
        categoryRequestDto = new CategoryRequestDto();
        categoryRequestDto.setName("Test Category");
        categoryRequestDto.setType(1); // Main category
        categoryRequestDto.setParentCatId(null);

        // Mock FileStorageService behavior
        when(fileStorageService.storeFile(any(MockMultipartFile.class), anyString()))
                .thenReturn("category_images/test_image.jpg");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"}) // Simulate an authenticated admin
    void createCategory_withImage_shouldReturnCreatedCategory() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test_image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        MockMultipartFile categoryJson = new MockMultipartFile(
                "category",
                "", // filename (not really needed for JSON part)
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(categoryRequestDto)
        );

        MvcResult result = mockMvc.perform(multipart("/api/categories")
                        .file(imageFile)
                        .file(categoryJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Test Category")))
                .andExpect(jsonPath("$.imageUrl", is("category_images/test_image.jpg")))
                .andReturn();

        // Optionally, verify the actual database state if not relying solely on response
        CategoryResponseDto responseDto = objectMapper.readValue(result.getResponse().getContentAsString(), CategoryResponseDto.class);
        assertNotNull(responseDto.getId());
        assertTrue(categoryRepository.existsById(responseDto.getId()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createCategory_withoutImage_shouldReturnCreatedCategory() throws Exception {
        MockMultipartFile categoryJson = new MockMultipartFile(
                "category",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(categoryRequestDto)
        );

        mockMvc.perform(multipart("/api/categories")
                        .file(categoryJson)) // No image file
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Test Category")))
                .andExpect(jsonPath("$.imageUrl").isEmpty()); // Or isNull() if that's the behavior
    }


    @Test
    @WithMockUser(username = "user", roles = {"USER"}) // Non-admin user
    void createCategory_byNonAdmin_shouldReturnForbidden() throws Exception {
         MockMultipartFile categoryJson = new MockMultipartFile(
                "category", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(categoryRequestDto)
        );
        mockMvc.perform(multipart("/api/categories").file(categoryJson))
                .andExpect(status().isForbidden());
    }

    @Test
    // No @WithMockUser, so anonymous
    void createCategory_byAnonymous_shouldReturnUnauthorized() throws Exception {
        MockMultipartFile categoryJson = new MockMultipartFile(
                "category", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(categoryRequestDto)
        );
        // Spring Security default is redirect to login for unauthorized if not handled by entry point for API
        // Our JwtAuthenticationEntryPoint should return 401
        mockMvc.perform(multipart("/api/categories").file(categoryJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCategoryById_publicAccess_shouldReturnCategory() throws Exception {
        // Setup: Create a category directly
        Category category = new Category();
        category.setName("Public Category");
        category.setType(1);
        category.setImageUrl("some/image.jpg");
        Category savedCategory = categoryRepository.save(category);

        mockMvc.perform(get("/api/categories/" + savedCategory.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Public Category")))
                .andExpect(jsonPath("$.imageUrl", is("some/image.jpg")));
    }
}
