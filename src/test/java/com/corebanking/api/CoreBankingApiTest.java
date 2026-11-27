package com.corebanking.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class CoreBankingApiTest {

	@LocalServerPort
	private int port;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@BeforeEach
	void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
		RestAssured.basePath = "/api/accounts";
	}
	
	@Test
	@Order(1)
	@DisplayName("TC01 - Verify GET all accounts returns 200 OK within SLA and non-empty list")
	void testGetAllAccounts() {
		given()
        .accept(ContentType.JSON)               
    .when()
        .get()                                  
    .then()
        .log().ifValidationFails()              
        .statusCode(200)                         
        .contentType(ContentType.JSON)           
        .time(lessThan(2000L))                   
        .body("size()", greaterThan(0))           
        .body("accountNo", hasItem("1001000001"));
	}
	
	@Test
	@Order(2)
	@DisplayName("TC02 - Verify Balance endpoint matches JSON Schema and returns accurate fields")
	void testGetAccountBalanceAndSchema() {
        String testAccount = "1001000001";

        given()
            .pathParam("accNo", testAccount)         
        .when()
            .get("/balance/{accNo}")                  
        .then()
            .statusCode(200)
            .body(matchesJsonSchemaInClasspath("schemas/balance-schema.json")) 
            .body("accountNo", equalTo(testAccount))
            .body("fullName", notNullValue())
            .body("status", equalTo("ACTIVE"));
    }
   
}