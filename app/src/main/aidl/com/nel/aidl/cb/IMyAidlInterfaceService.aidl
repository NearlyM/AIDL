// IMyAidlInterfaceService.aidl
package com.nel.aidl.cb;

// Declare any non-default types here with import statements
import com.nel.aidl.cb.IMyAidlInterfaceCallback;

interface IMyAidlInterfaceService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

    void registerCallback(IMyAidlInterfaceCallback callback);

    void unregisterCallback(IMyAidlInterfaceCallback callback);
}
