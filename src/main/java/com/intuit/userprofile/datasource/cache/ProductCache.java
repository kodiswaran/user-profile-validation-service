package com.intuit.userprofile.datasource.cache;

import com.intuit.userprofile.datasource.mysql.dao.IProductDAO;
import com.intuit.userprofile.datasource.mysql.dao.impl.ProductDAO;
import com.intuit.userprofile.datasource.mysql.model.ProductApiModel;
import com.intuit.userprofile.datasource.mysql.model.ProductModel;
import com.intuit.userprofile.util.SerializerUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component("cache")
public class ProductCache implements IProductDAO {

    IProductDAO productDAO;

    RedissonClient redissonClient;

    @Autowired
    public ProductCache( final ProductDAO productDAO, final RedissonClient redissonClient) {
        this.productDAO = productDAO;
        this.redissonClient = redissonClient;
    }

    private List<ProductModel> getAllProduct(boolean forceCacheUpdate) {
        RList<String> productsCache = redissonClient.getList("PRODUCTS");
        List<ProductModel> productModels = null;
        if ( forceCacheUpdate || productsCache.isEmpty() ) {
            List<ProductModel> allProduct = productDAO.getAllProduct();

            List<String> collect = new ArrayList<>();
            for ( final ProductModel productModel : allProduct ) {
                String serializedValue = SerializerUtil.serialize(productModel);
                redissonClient.getBucket("PRODUCT_NAME:"+productModel.getName()).set(serializedValue);
                redissonClient.getBucket("PRODUCT_ID:"+productModel.getId()).set(serializedValue);
                collect.add(serializedValue);
            }

            // reset the list
            RList<String> products = redissonClient.getList("PRODUCTS");
            products.clear();
            products.addAll(collect);
            return allProduct;
        } else {
            productModels = productsCache.stream().map(str -> SerializerUtil.deSerialize(str, ProductModel.class)).toList();
        }

        return productModels;
    }

    @Override
    public List<ProductModel> getAllProduct() {
        return getAllProduct(false);
    }

    @Override
    public ProductModel getProduct( final long productId ) {
        RBucket<String> bucket = redissonClient.getBucket("PRODUCT_ID:" + productId);
        ProductModel productModel = null;
        if ( bucket.isExists() ) {
            productModel = SerializerUtil.deSerialize(bucket.get(), ProductModel.class);
        }
        return productModel;
    }

    @Override
    public ProductModel getProductUsingName( final String productName ) {
        RBucket<String> bucket = redissonClient.getBucket("PRODUCT_NAME:" + productName);
        ProductModel productModel = null;
        if ( bucket.isExists() ) {
            productModel = SerializerUtil.deSerialize(bucket.get(), ProductModel.class);
        }
        return productModel;
    }

    @Override
    public ProductApiModel getProductWithApiDetail( final long productId ) {
        RBucket<String> bucket = redissonClient.getBucket("PRODUCT_API_DETAIL:" + productId);
        ProductApiModel model;
        if ( !bucket.isExists() ) {
            model = productDAO.getProductWithApiDetail(productId);
            redissonClient.getBucket("PRODUCT_API_DETAIL:" + productId).set(SerializerUtil.serialize(model));
        } else {
            model = SerializerUtil.deSerialize(bucket.get(), ProductApiModel.class);
        }
        return model;
    }


    public ProductApiModel addProduct(ProductApiModel productApiModel) {
        ProductApiModel productApiModelResponse = productDAO.addProduct(productApiModel);
        // refresh the cache
        getAllProduct(true);
        // return the response
        return productApiModelResponse;
    }
}
