package com.intuit.userprofile.dao;

import com.intuit.userprofile.datasource.mysql.dao.impl.ProductDAO;
import com.intuit.userprofile.datasource.mysql.model.ProductModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProductDaoTest {

    private ProductDAO productDao;

    @Mock
    private Connection connection;

    @BeforeEach
    public void setUp() {
        //connection = Mockito.mock(Connection.class);
        productDao = new ProductDAO(connection);
    }

    @Test
    public void testGetProductUsingName() throws SQLException {
        // Mocking objects for PreparedStatement and ResultSet
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        ResultSet rs = Mockito.mock(ResultSet.class);

        // Define test input
        String productName = "TestProduct";
        ProductModel expectedProduct = new ProductModel(1, "TestProduct", "TestDescription", true);

        // Mock behavior for the connection and prepared statement
        Mockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        Mockito.when(ps.executeQuery()).thenReturn(rs);
        Mockito.when(rs.next()).thenReturn(true); // Simulate a result in ResultSet
        Mockito.when(rs.getLong("id")).thenReturn(expectedProduct.getId());
        Mockito.when(rs.getString("name")).thenReturn(expectedProduct.getName());
        Mockito.when(rs.getString("description")).thenReturn(expectedProduct.getDescription());
        Mockito.when(rs.getBoolean("is_active")).thenReturn(expectedProduct.isActive());

        // Call the method to be tested
        ProductModel actualProduct = productDao.getProductUsingName(productName);

        // Verify the behavior
        assertNotNull(actualProduct);
        assertEquals(expectedProduct, actualProduct);
    }
}
