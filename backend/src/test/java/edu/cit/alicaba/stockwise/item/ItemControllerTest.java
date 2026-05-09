package edu.cit.alicaba.stockwise.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ItemControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetAllItemsReturnsSuccess() {
        // Simple automated regression test to ensure the endpoint works after refactoring
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/items", String.class);
        
        // Assert that the API is up and returns a 200 OK status
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}