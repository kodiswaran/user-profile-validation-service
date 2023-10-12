package com.intuit.userprofile.datasource.mysql.dao.impl;

import com.intuit.userprofile.datasource.mysql.dao.IProductDAO;
import com.intuit.userprofile.datasource.mysql.model.ProductApiModel;
import com.intuit.userprofile.datasource.mysql.model.ProductModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component("database")
@Primary
public class ProductDAO implements IProductDAO {

    // get
    private static final String QUERY_GET_ALL_PRODUCTS = "select * from product";
    private static final String QUERY_GET_PRODUCT_USING_PRODUCT_ID = "select * from product where id = ?";
    private static final String QUERY_GET_PRODUCT_USING_PRODUCT_NAME = "select * from product where name = ?";
    private static final String QUERY_GET_PRODUCT_WITH_API_DETAILS_USING_PRODUCT_ID = "SELECT p.id, p.name, " +
        "p.description, p.is_active, pc.url,pc.x_api_id,pc.x_api_key from product p, product_validate_api_config pc " +
        "where p.id = pc.product_id and p.id = ?";

    private final Connection connection;

    @Autowired
    public ProductDAO( final Connection connection ) {
        this.connection = connection;
    }

    // insert
    private static final String QUERY_ADD_NEW_PRODUCT = "INSERT into product (name, description) VALUES (?, ?)";
    private static final String QUERY_ADD_PRODUCT_VALIDATE_API_DETAIL = "INSERT into product_validate_api_config (product_id, url, x_api_id, x_api_key) VALUES (?, ?, ?, ?)";

    @Override
    public List<ProductModel> getAllProduct() {

        List<ProductModel> productModels = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(QUERY_GET_ALL_PRODUCTS);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                String lastName = rs.getString("description");
                boolean isActive = rs.getBoolean("is_active");
                productModels.add(ProductModel.builder().id(id).name(name).description(lastName).isActive(isActive).build());
            }
        } catch ( Exception e) {
            log.error("Exception: " + e);
            throw new RuntimeException(e);
        }

        return productModels;
    }

    @Override
    public ProductModel getProduct( final long productId ) {
        ProductModel productModel = null;
        try {
            PreparedStatement ps = connection.prepareStatement(QUERY_GET_PRODUCT_USING_PRODUCT_ID);
            ps.setLong(1, productId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                String lastName = rs.getString("description");
                boolean isActive = rs.getBoolean("is_active");
                productModel = ProductModel.builder().id(id).name(name).description(lastName).isActive(isActive).build();
            }
        } catch ( Exception e) {
            log.error("Exception: " + e);
            throw new RuntimeException(e);
        }

        return productModel;
    }

    @Override
    public ProductModel getProductUsingName( final String productName ) {
        ProductModel productModel = null;
        try {
            PreparedStatement ps = connection.prepareStatement(QUERY_GET_PRODUCT_USING_PRODUCT_NAME);
            ps.setString(1, productName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                String lastName = rs.getString("description");
                boolean isActive = rs.getBoolean("is_active");
                productModel = ProductModel.builder().id(id).name(name).description(lastName).isActive(isActive).build();
            }
        } catch ( Exception e) {
            log.error("Exception: " + e);
            throw new RuntimeException(e);
        }

        return productModel;
    }

    @Override
    public ProductApiModel getProductWithApiDetail( final long productId ) {
        ProductApiModel productApiModel = null;
        try {
            PreparedStatement ps = connection.prepareStatement(QUERY_GET_PRODUCT_WITH_API_DETAILS_USING_PRODUCT_ID);
            ps.setLong(1, productId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                boolean isActive = rs.getBoolean("is_active");
                String url = rs.getString("url");
                String xApiId = rs.getString("x_api_id");
                String xApiKey = rs.getString("x_api_key");
                productApiModel = ProductApiModel.builder().id(id).name(name).description(description)
                    .isActive(isActive).url(url).xApiId(xApiId).xApiKey(xApiKey)
                    .build();
            }
        } catch ( Exception e) {
            log.error("Exception: " + e);
            throw new RuntimeException(e);
        }

        return productApiModel;
    }

    @Override
    public ProductApiModel addProduct( final ProductApiModel productApiModel ) {
        try {
            PreparedStatement ps1 = connection.prepareStatement(QUERY_ADD_NEW_PRODUCT, new String[] { "id" });
            ps1.setString(1, productApiModel.getName());
            ps1.setString(2, productApiModel.getDescription());
            ps1.executeUpdate();
            ResultSet generatedKeys = ps1.getGeneratedKeys();

            if (generatedKeys.next())
                productApiModel.setId(generatedKeys.getLong(1));
            else {
                connection.rollback();
                log.error("error in creating product");
                throw new RuntimeException("error in creating product");
            }

            PreparedStatement ps2 = connection.prepareStatement(QUERY_ADD_PRODUCT_VALIDATE_API_DETAIL);
            ps2.setLong(1, productApiModel.getId());
            ps2.setString(2, productApiModel.getUrl());
            ps2.setString(3, productApiModel.getXApiId());
            ps2.setString(4, productApiModel.getXApiKey());
            ps2.execute();

            // commit the changes only after successfully execution of both insert queries
            connection.commit();

            return productApiModel;

        } catch ( Exception e) {
            log.error("Exception: ", e);
            throw new RuntimeException(e);
        }
    }
}
