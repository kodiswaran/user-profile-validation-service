package com.intuit.userprofile.listener.productvalidation.model;

import lombok.Getter;

@Getter
public enum EventType {
    CREATE,     // a new user have been created
    UPDATE,     // an update in the user profile details
    SUBSCRIBE   // the user has subscribed to a new product
}
