package com.intuit.userprofile.datasource.mysql.dao;

import com.intuit.userprofile.datasource.mysql.model.ProductApiModel;
import com.intuit.userprofile.datasource.mysql.model.ProductModel;

import java.util.List;

public interface IProductDAO {

    // get all products
    public List<ProductModel> getAllProduct();

    // get product detail
    public ProductModel getProduct(long productId);
    public ProductModel getProductUsingName(String productName);

    // get product with api detail
    public ProductApiModel getProductWithApiDetail(long productId);

    // add a product
    public ProductApiModel addProduct(ProductApiModel productApiModel);
}
