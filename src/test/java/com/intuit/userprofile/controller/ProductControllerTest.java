package com.intuit.userprofile.controller;

import com.intuit.userprofile.controller.ProductController;
import com.intuit.userprofile.model.apisignature.AddProductRequest;
import com.intuit.userprofile.model.apisignature.CommonResponse;
import com.intuit.userprofile.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class ProductControllerTest {

    @Mock
    private ProductService productService;

    private ProductController productController;

    @BeforeEach
    public void setUp() {
        productService = Mockito.mock(ProductService.class);
        productController = new ProductController(productService);
    }

    @Test
    public void createProductSuccess() {
        // Mock the behavior of productService.createProduct()
        AddProductRequest request = new AddProductRequest(/* initialize request fields */);
//        AddProductResponse response = new AddProductResponse(/* initialize response fields */);
//        when(productService.createProduct(eq(request))).thenReturn(response);

        // Call the controller method
        ResponseEntity<CommonResponse<AddProductRequest>> entity = productController.createProduct(request);

        // Verify the response
        assertNotNull(entity);
        assertEquals("SUCCESS", entity.getBody().getStatus());
        assertEquals(request, entity.getBody().getData());
    }

    @Test
    public void createProductError() {
        // Mock productService.createProduct() to throw an exception
        AddProductRequest request = new AddProductRequest(/* initialize request fields */);
        when(productService.createProduct(eq(request))).thenThrow(new RuntimeException("Test exception message"));

        // Call the controller method and catch the exception
        try {
            productController.createProduct(request);
            fail("Expected an exception to be thrown");
        }
        catch ( Exception e ) {
            // Handle the exception or assert its properties
            assertTrue(e.getCause() instanceof RuntimeException);
            assertEquals("Test exception message", e.getCause().getMessage());
        }
    }
}