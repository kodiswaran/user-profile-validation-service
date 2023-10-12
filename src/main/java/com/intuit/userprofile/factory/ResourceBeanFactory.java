package com.intuit.userprofile.factory;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;

@Configuration
public class ResourceBeanFactory {

    @Bean
    public RedissonClient getRedissonClient() {
        // Initialize Redisson configuration
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379");

        // Create a RedissonClient instance
        return Redisson.create(config);
    }

    @Bean
    public Connection getMysqlConnection() throws Exception {

        // Initialize Redisson configuration
        Connection connection = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/user_profile_validation_db?serverTimezone=UTC", "admin", "admin");

        // set auto commit false
        connection.setAutoCommit(false);
        return connection;
    }


    @Bean
    public DynamoDBMapper getDynamoDbConnection() {
        AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
            .withEndpointConfiguration(new EndpointConfiguration("http://localhost:8000", "us-east-1"))
            .build();
        return new DynamoDBMapper(amazonDynamoDB);
    }
}
