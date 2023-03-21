package com.casino;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserApplicationTests {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepository;
    private final ResourceFileLoader resourceFileLoader = new ResourceFileLoader();
    @BeforeEach
    public void beforeTest() {
        userRepository.deleteAll();
    }
    @Test
    public void testGetAllUsers_ok() throws Exception {
        mvc.perform(post("/api/v1/users")
                        .contentType(APPLICATION_JSON)
                        .content(resourceFileLoader.getJson("create_user_entry.json")))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/v1/users")
                        .contentType(APPLICATION_JSON)
                        .content(resourceFileLoader.getJson("create_user2_entry.json")))
                .andExpect(status().isCreated());
        mvc.perform(get("/api/v1/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("test")))
                .andExpect(jsonPath("$[1].name", is("test2")));
    }
    @Test
    public void testGetAllUsers_notFound() throws Exception {
        mvc.perform(get("/api/v1/users"))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testGetUserById_ok() throws Exception {
        mvc.perform(post("/api/v1/users")
                        .contentType(APPLICATION_JSON)
                        .content(resourceFileLoader.getJson("create_user_entry.json")))
                .andExpect(status().isCreated());
        mvc.perform(get("/api/v1/users/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("test")));
    }
    @Test
    public void testGetUserById_notFound() throws Exception {
        mvc.perform(get("/api/v1/users/{id}", 1))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testGetUserByName_ok() throws Exception {
        mvc.perform(post("/api/v1/users")
                        .contentType(APPLICATION_JSON)
                        .content(resourceFileLoader.getJson("create_user_entry.json")))
                .andExpect(status().isCreated());
        mvc.perform(get("/api/v1/users/name/{name}", "test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("test")));
    }
    @Test
    public void testGetUserByName_notFound() throws Exception {
        mvc.perform(get("/api/v1/users/name/{name}", "test"))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testCreateUser_ok() throws Exception {
        mvc.perform(post("/api/v1/users")
                        .contentType(APPLICATION_JSON)
                        .content(resourceFileLoader.getJson("create_user_entry.json")))
                .andExpect(status().isCreated());
    }
    @Test
    public void testCreateUser_badRequest_emptyName() throws Exception {
        mvc.perform(post("/api/v1/users")
                        .contentType(APPLICATION_JSON)
                        .content(resourceFileLoader.getJson("create_user_empty_name_entry.json")))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void testCreateUser_conflict_nameExists() throws Exception {
        mvc.perform(post("/api/v1/users")
                        .contentType(APPLICATION_JSON)
                        .content(resourceFileLoader.getJson("create_user_entry.json")))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/v1/users")
                        .contentType(APPLICATION_JSON)
                        .content(resourceFileLoader.getJson("create_user_entry.json")))
                .andExpect(status().isConflict());
    }
    @Test
    public void testUpdateUser_ok() throws Exception {
        mvc.perform(post("/api/v1/users")
                        .contentType(APPLICATION_JSON)
                        .content(resourceFileLoader.getJson("create_user_entry.json")))
                .andExpect(status().isCreated());
        mvc.perform(put("/api/v1/users/{id}", 1)
                .contentType(APPLICATION_JSON)
                .content(resourceFileLoader.getJson("create_user2_entry.json")))
                .andExpect(status().isOk());
        mvc.perform(get("/api/v1/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("test2")));
    }
    @Test
    public void testUpdateUser_notFound() throws Exception {
        mvc.perform(put("/api/v1/users/{id}", 1)
                        .contentType(APPLICATION_JSON)
                        .content(resourceFileLoader.getJson("create_user2_entry.json")))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testUpdateUser_conflict_nameExists() throws Exception {
        mvc.perform(post("/api/v1/users")
                        .contentType(APPLICATION_JSON)
                        .content(resourceFileLoader.getJson("create_user_entry.json")))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/v1/users")
                        .contentType(APPLICATION_JSON)
                        .content(resourceFileLoader.getJson("create_user2_entry.json")))
                .andExpect(status().isCreated());
        mvc.perform(put("/api/v1/users/{id}", 1)
                        .contentType(APPLICATION_JSON)
                        .content(resourceFileLoader.getJson("create_user2_entry.json")))
                .andExpect(status().isConflict());
    }
    @Test
    public void testDeleteUser_ok() throws Exception {
        mvc.perform(post("/api/v1/users")
                        .contentType(APPLICATION_JSON)
                        .content(resourceFileLoader.getJson("create_user_entry.json")))
                .andExpect(status().isCreated());
        mvc.perform(delete("/api/v1/users/{id}", 1))
                .andExpect(status().isNoContent());
    }
    @Test
    public void testDeleteUser_notFound() throws Exception {
        mvc.perform(delete("/api/v1/users/{id}", 1))
                .andExpect(status().isNotFound());
    }

}
