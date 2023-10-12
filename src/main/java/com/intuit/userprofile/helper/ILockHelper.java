package com.intuit.userprofile.helper;

import java.util.function.Supplier;

public interface ILockHelper {

    public <T> T executeInLock(String key, Supplier<T> supplier);

}
