package com.intuit.userprofile.service;

import com.intuit.userprofile.datasource.mysql.dao.IProductDAO;
import com.intuit.userprofile.datasource.mysql.model.ProductApiModel;
import com.intuit.userprofile.datasource.mysql.model.ProductModel;
import com.intuit.userprofile.helper.ILockHelper;
import com.intuit.userprofile.model.exception.UserProfileKnownException;
import com.intuit.userprofile.model.apisignature.AddProductRequest;
import com.intuit.userprofile.model.apisignature.ProductResponse;
import com.intuit.userprofile.model.exception.ErrorCode;
import com.intuit.userprofile.util.UnmarshallerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductService {

    IProductDAO productDAO;

    ILockHelper redisLockHelper;

    @Autowired
    public ProductService( @Qualifier("cache") final IProductDAO productDAO, final ILockHelper redisLockHelper ) {
        this.productDAO = productDAO;
        this.redisLockHelper = redisLockHelper;
    }

    public AddProductRequest createProduct( AddProductRequest addProductRequest) {
        ProductApiModel productApiModelResponse = redisLockHelper.executeInLock("CREATE_PRODUCT_"+addProductRequest.getName(), () -> {
            ProductModel productModel = productDAO.getProductUsingName(addProductRequest.getName());
            if ( null != productModel ) {
                throw new UserProfileKnownException(ErrorCode.PRODUCT_NAME_ALREADY_IN_USE,
                    "product with similar name already exists");
            }

            ProductApiModel productApiModel = UnmarshallerUtil.PRODUCT_MODEL_MAPPER.apply(addProductRequest);
            return productDAO.addProduct(productApiModel);
        });
        return UnmarshallerUtil.PRODUCT_API_MAPPER.apply(productApiModelResponse);
    }

    public List<ProductResponse> getAllProducts() {
        List<ProductModel> allProduct = productDAO.getAllProduct();
        return allProduct.stream()
            .map(model -> ProductResponse.builder().id(model.getId()).name(model.getName())
                .description(model.getDescription()).isActive(model.isActive()).build())
            .collect(Collectors.toList());
    }
}
