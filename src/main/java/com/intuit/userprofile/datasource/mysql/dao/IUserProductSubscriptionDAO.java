package com.intuit.userprofile.datasource.mysql.dao;

import com.intuit.userprofile.datasource.mysql.model.UserProductModel;

import java.util.List;

public interface IUserProductSubscriptionDAO {

    // subscribe a user to a product
    public boolean subscribe(long userId, long productId);

    // unsubscribe a user to a product
    public boolean unSubscribe(long userId, long productId);

    // check whether the user is subscribed to the product
    public boolean isSubscribed(long userId, long productId);

    // get all user subscribed products
    public List<UserProductModel> getAllUserSubscriptions( long userId );
}
