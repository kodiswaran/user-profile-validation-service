package com.intuit.userprofile.datasource.mysql.dao.impl;

import com.intuit.userprofile.datasource.mysql.dao.IUserProductSubscriptionDAO;
import com.intuit.userprofile.datasource.mysql.model.UserProductModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class UserProductSubscriptionDAO implements IUserProductSubscriptionDAO {

    private static final String QUERY_SUBSCRIBE = "insert into user_product_subscription (user_id, product_id, is_active) values (?, ?, ?) on DUPLICATE KEY update is_active=VALUES(is_active)";
    private static final String QUERY_UNSUBSCRIBE = "update user_product_subscription set is_active = ? where user_id = ? and product_id = ?";
    private static final String QUERY_IS_SUBSCRIBE = "select * from user_product_subscription where user_id = ? and product_id = ? and is_active = ?";
    private static final String QUERY_IS_GET_ALL_SUBSCRIPTIONS = "select ups.user_id, ups.product_id, p.name, ups.is_active from user_product_subscription ups, user u, " +
        "product p where u.id = ups.user_id and p.id = ups.product_id and p.is_active = true and ups.is_active = true and u.id = ?";

    private final Connection connection;

    @Autowired
    public UserProductSubscriptionDAO( final Connection connection ) {
        this.connection = connection;
    }

    @Override
    public boolean subscribe( final long userId, final long productId ) {
        try {
            PreparedStatement ps = connection.prepareStatement(QUERY_SUBSCRIBE, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            ps.setBoolean(3, Boolean.TRUE);
            ps.executeUpdate();

            connection.commit();
        } catch ( Exception e) {
            log.error("Exception: " + e);
            throw new RuntimeException(e);
        }

        return true;
    }

    @Override
    public boolean unSubscribe( final long userId, final long productId ) {
        try {
            PreparedStatement ps = connection.prepareStatement(QUERY_UNSUBSCRIBE);
            ps.setBoolean(1, Boolean.FALSE);
            ps.setLong(2, userId);
            ps.setLong(3, productId);
            ps.executeUpdate();

            connection.commit();
        } catch ( Exception e) {
            log.error("Exception: " + e);
            throw new RuntimeException(e);
        }

        return true;
    }

    @Override
    public boolean isSubscribed( final long userId, final long productId ) {
        try {
            PreparedStatement ps = connection.prepareStatement(QUERY_IS_SUBSCRIBE);
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            ps.setBoolean(3, Boolean.TRUE);
            ResultSet resultSet = ps.executeQuery();
            return resultSet.next();
        } catch ( Exception e) {
            log.error("Exception: " + e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UserProductModel> getAllUserSubscriptions( final long userId ) {
        List<UserProductModel> userProductModels = new ArrayList<>();

        try {
            PreparedStatement ps = connection.prepareStatement(QUERY_IS_GET_ALL_SUBSCRIPTIONS);
            ps.setLong(1, userId);
            ResultSet resultSet = ps.executeQuery();
            while ( resultSet.next() ) {
                long userId1 = resultSet.getLong("user_id");
                long productId = resultSet.getLong("product_id");
                String productName = resultSet.getString("name");
                boolean isActive = resultSet.getBoolean("is_active");
                userProductModels.add(UserProductModel.builder().userId(userId1).productId(productId).productName(productName).isActive(isActive).build());
            }
        } catch ( Exception e) {
            log.error("Exception: " + e);
            throw new RuntimeException(e);
        }

        return userProductModels;
    }

}
