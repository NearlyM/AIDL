package com.nel.aidl.cb;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

/**
 * Description :
 * CreateTime : 2018/5/3 15:41
 *
 * @author ningerlei@danale.com
 * @version <v1.0>
 */

public class RemoteService extends Service {

    private static final String TAG = RemoteService.class.getSimpleName();

    private RemoteCallbackList<IMyAidlInterfaceCallback> myRemoteCallbackList;

    private OnDataChangeListener mOnDataChangeListener;

    boolean mTag = true;

    @Override
    public void onCreate() {
        super.onCreate();
        initMember();
        doWork();
    }

    private void initMember() {
        myRemoteCallbackList = new RemoteCallbackList<>();
    }

    private void doWork() {
        new Thread(new Runnable() {

            int result = 0;

            @Override
            public void run() {
                while (mTag) {
                    result++;
                    notifyDataChanged(result);
                    broadCastItem(result);
                    Log.i(TAG, "The current result is : " + result);

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    private void broadCastItem(int result) {
        /**
         * 其实RemoteCallbackList类似于java中{@link java.util.Observable}，用来批量处理接口回调对象，
         * 其实如果确保只有一个客户端会bind到这个服务，只需要保存一个IMyAidlInterfaceCallback即可。
         * 但是如果有多个，强烈推荐使用其实RemoteCallbackList
         */

        int callbackSize = myRemoteCallbackList.beginBroadcast();
        if (callbackSize == 0) {
            return;
        }

        /**
         * 逐一进行回调
         */
        for (int i = 0; i < callbackSize; i++) {
            try {
                myRemoteCallbackList.getBroadcastItem(i).onCallback(result);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        /**
         * 回调完成之后一定要确保调用finishBroadcast，不然在下一次执行beginBroadcast会抛出
         * IllegalStateException异常。这类似与{@link Observable#setChanged()}
         * 和{@link Observable#clearChanged()}，但是RemoteCallbackList设计的更加谨慎，
         * 为了确保一次Broadcast仅正对当前的状态或者数据变化。
         */
        myRemoteCallbackList.finishBroadcast();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    private void notifyDataChanged(int data) {
        if (mOnDataChangeListener != null) {
            mOnDataChangeListener.onDataChanged(data);
        }
    }

    public void registerListener(OnDataChangeListener listener) {
        mOnDataChangeListener = listener;
    }

    public void unRegisterListener() {
        mOnDataChangeListener = null;
    }

    public class MyBinder extends IMyAidlInterfaceService.Stub {

        @Override
        public void registerCallback(IMyAidlInterfaceCallback callback) throws RemoteException {
            myRemoteCallbackList.register(callback);
        }

        @Override
        public void unregisterCallback(IMyAidlInterfaceCallback callback) throws RemoteException {
            myRemoteCallbackList.unregister(callback);
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy !!!");
        mTag = false;
        super.onDestroy();
    }
}
