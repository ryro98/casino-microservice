package com.casino;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
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
public class CashApplicationTests {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private CashRepository cashRepository;
    @BeforeEach
    public void beforeTest() {
        cashRepository.deleteAll();
    }

    @Test
    public void testGetAllCash_ok() throws Exception {
        mvc.perform(post("/api/v1/cash?userId=1"))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/v1/cash?userId=2"))
                .andExpect(status().isCreated());
        mvc.perform(get("/api/v1/cash"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userId", is(1)))
                .andExpect(jsonPath("$[0].cash", is(0)))
                .andExpect(jsonPath("$[1].userId", is(2)))
                .andExpect(jsonPath("$[1].cash", is(0)));
    }
    @Test
    public void testGetAllCash_notFound() throws Exception {
        mvc.perform(get("/api/v1/cash"))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testGetCashById_ok() throws Exception {
        mvc.perform(post("/api/v1/cash?userId=1"))
                .andExpect(status().isCreated());
        mvc.perform(get("/api/v1/cash/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.cash", is(0)));
    }
    @Test
    public void testGetCashById_notFound() throws Exception {
        mvc.perform(get("/api/v1/cash/{id}", 1))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testGetCashByUserId_ok() throws Exception {
        mvc.perform(post("/api/v1/cash?userId=1"))
                .andExpect(status().isCreated());
        mvc.perform(get("/api/v1/cash/user/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.cash", is(0)));
    }
    @Test
    public void testGetCashByUserId_notFound() throws Exception {
        mvc.perform(get("/api/v1/cash/user/{id}", 1))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testCreateCash_created() throws Exception {
        mvc.perform(post("/api/v1/cash?userId=1"))
                .andExpect(status().isCreated());
    }
    @Test
    public void testCreateCash_conflict_userIdExists() throws Exception {
        mvc.perform(post("/api/v1/cash?userId=1"))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/v1/cash?userId=1"))
                .andExpect(status().isConflict());
    }
    @Test
    public void testAddCash_ok() throws Exception {
        mvc.perform(post("/api/v1/cash?userId=1"))
                .andExpect(status().isCreated());
        mvc.perform(put("/api/v1/cash/user/{userId}", 1))
                .andExpect(status().isOk());
        mvc.perform(get("/api/v1/cash/user/{userId}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cash", is(500)));
    }
    @Test
    public void testAddCash_badRequest_tooSoon() throws Exception {
        mvc.perform(post("/api/v1/cash?userId=1"))
                .andExpect(status().isCreated());
        mvc.perform(put("/api/v1/cash/user/{userId}", 1))
                .andExpect(status().isOk());
        mvc.perform(put("/api/v1/cash/user/{userId}", 1))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void testAddCash_notFound() throws Exception {
        mvc.perform(put("/api/v1/cash/user/{userId}", 1))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testGambleCash_ok() throws Exception {
        mvc.perform(post("/api/v1/cash?userId=1"))
                .andExpect(status().isCreated());
        mvc.perform(put("/api/v1/cash/user/{userId}", 1))
                .andExpect(status().isOk());
        mvc.perform(put("/api/v1/cash/gamble/{userId}?money=200", 1))
                .andExpect(status().isOk());
        mvc.perform(get("/api/v1/cash/user/{userId}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cash", is(oneOf(300, 700))));
    }
    @Test
    public void testGambleCash_badRequest_tooSoon() throws Exception {
        mvc.perform(post("/api/v1/cash?userId=1"))
                .andExpect(status().isCreated());
        mvc.perform(put("/api/v1/cash/user/{userId}", 1))
                .andExpect(status().isOk());
        mvc.perform(put("/api/v1/cash/gamble/{userId}?money=200", 1))
                .andExpect(status().isOk());
        mvc.perform(put("/api/v1/cash/gamble/{userId}?money=200", 1))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void testGambleCash_badRequest_tooMuchMoney() throws Exception {
        mvc.perform(post("/api/v1/cash?userId=1"))
                .andExpect(status().isCreated());
        mvc.perform(put("/api/v1/cash/user/{userId}", 1))
                .andExpect(status().isOk());
        mvc.perform(put("/api/v1/cash/gamble/{userId}?money=500", 1))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void testGambleCash_notFound() throws Exception {
        mvc.perform(put("/api/v1/cash/gamble/{userId}?money=200", 1))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testDeleteCash_noContent() throws Exception {
        mvc.perform(post("/api/v1/cash?userId=1"))
                .andExpect(status().isCreated());
        mvc.perform(delete("/api/v1/cash/{id}", 1))
                .andExpect(status().isNoContent());
    }
    @Test
    public void testDeleteCash_notFound() throws Exception {
        mvc.perform(delete("/api/v1/cash/{id}", 1))
                .andExpect(status().isNotFound());
    }
}
