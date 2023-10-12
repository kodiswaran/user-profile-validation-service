package com.intuit.userprofile.controller;

import com.intuit.userprofile.model.exception.UserProfileKnownException;
import com.intuit.userprofile.model.apisignature.AddProductRequest;
import com.intuit.userprofile.model.apisignature.CommonResponse;
import com.intuit.userprofile.model.apisignature.ProductResponse;
import com.intuit.userprofile.model.exception.ErrorCode;
import com.intuit.userprofile.service.ProductService;
import com.intuit.userprofile.util.Constants;
import com.intuit.userprofile.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v1/product")
public class ProductController {

    ProductService productService;

    @Autowired
    public ProductController( ProductService productService ) {
        this.productService = productService;
    }

    /**
     * request to add a product to the system
     *
     * @param addProductRequest request to add a product to the system
     * @return add product response
     */
    @PutMapping("")
    public ResponseEntity<CommonResponse<AddProductRequest>> createProduct( @RequestBody AddProductRequest addProductRequest ) {
        try {
            return ResponseEntity.ok(CommonResponse.<AddProductRequest>builder().status(Constants.API_STATUS_SUCCESS)
                .data(productService.createProduct(addProductRequest)).build());
        }
        catch ( Exception e ) {
            log.error("ERROR: {}", e.getMessage(), e);
            return ExceptionUtil.createExceptionResponse(e, AddProductRequest.class);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<CommonResponse<List<ProductResponse>>> getAllProducts() {
        try {
            return ResponseEntity.ok(CommonResponse.<List<ProductResponse>>builder().status(Constants.API_STATUS_SUCCESS)
                .data(productService.getAllProducts()).build());
        }
        catch ( Exception e ) {
            log.error("ERROR: {}", e.getMessage(), e);
            ErrorCode errorCode = Optional.of(e)
                .filter(UserProfileKnownException.class::isInstance)
                .map(UserProfileKnownException.class::cast)
                .map(UserProfileKnownException::getErrorCode)
                .orElse(ErrorCode.INTERNAL_SERVER_ERROR);

            CommonResponse<List<ProductResponse>> failure = CommonResponse.<List<ProductResponse>>builder()
                .status("FAILURE")
                .message(errorCode.getErrorMessage())
                .errorCode(errorCode.getErrorCode())
                .build();

            log.error("ERROR: {}", e.getMessage(), e);
            return ResponseEntity.status(errorCode.getStatusCode()).body(failure);
        }
    }
}
