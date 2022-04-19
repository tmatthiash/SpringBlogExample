package com.springboot.blog;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test"})
public class TestSetup {
    
    @Autowired
    public JdbcTemplate jdbcTemplate;

    @LocalServerPort
    Integer port;

    @BeforeAll
    void beforeAll() {
        RestAssured.port = port;
    }

    @BeforeEach
    protected void setUp() {

        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute("TRUNCATE comments");
        jdbcTemplate.execute("TRUNCATE posts");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");

    }
}
