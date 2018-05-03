package com.nel.aidl.cb;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nel.aidl.MyAidlService;
import com.nel.aidl.R;

/**
 * Description :
 * CreateTime : 2018/5/3 15:57
 *
 * @author ningerlei@danale.com
 * @version <v1.0>
 */

public class TestActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "TestActivity";

    private static final String REMOTE_PKG_NAME = "com.yellow.demo.remotecallbackdemoserver";
    private static final String REMOTE_CLS_NAME = "com.yellow.demo.remotecallbackdemoserver.RemoteService";
    private static final String REMOTE_SERVICE_ACTION = "com.yellow.demo.remotecallbackdemoserver.REMOTESERVICE";

    private Button mRegisterBt, mUnregisterBt;
    private TextView mResultTv;

    private IMyAidlInterfaceCallback mRemoteCallback;
    private IMyAidlInterfaceService mRemoteService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        initView();
        initCallback();
        bindRemoteService();
    }

    private void initView() {
        mRegisterBt = findViewById(R.id.register);
        mUnregisterBt = findViewById(R.id.unregister);
        mResultTv = findViewById(R.id.display);

        mRegisterBt.setOnClickListener(this);
        mUnregisterBt.setOnClickListener(this);
    }

    private void initCallback() {
        mRemoteCallback = new IMyAidlInterfaceCallback.Stub() {
            @Override
            public void onCallback(final int result) throws RemoteException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mResultTv.setText("The result is : " + result);
                    }
                });
            }
        };
    }

    private void bindRemoteService() {
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mRemoteService = IMyAidlInterfaceService.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

//        Intent intent = new Intent(REMOTE_SERVICE_ACTION);
//        intent.setComponent(new ComponentName(REMOTE_PKG_NAME, REMOTE_CLS_NAME));
//        bindService(intent, serviceConnection, BIND_AUTO_CREATE);

        Intent intent = new Intent(getApplicationContext(), RemoteService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                registerRemoteCallback();
                break;
            case R.id.unregister:
                unregisterRemoteCallback();
                break;
        }
    }

    private void registerRemoteCallback() {
        if (mRemoteService != null) {
            try {
                mRemoteService.registerCallback(mRemoteCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void unregisterRemoteCallback() {
        if (mRemoteService != null) {
            try {
                mRemoteService.unregisterCallback(mRemoteCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
