package com.nel.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.nel.aidl.bean.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Description :
 * CreateTime : 2018/5/3 14:21
 *
 * @author ningerlei@danale.com
 * @version <v1.0>
 */

public class MyAidlService extends Service {

    private final String TAG = getClass().getSimpleName();

    private ArrayList<Person> mPersons;

    private IBinder mIBinder = new IMyAidl.Stub() {
        @Override
        public void addPerson(Person person) throws RemoteException {
            mPersons.add(person);
        }

        @Override
        public List<Person> getPersonList() throws RemoteException {
            return mPersons;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        mPersons = new ArrayList<>();
        Log.d(TAG, "MyAidlService onBind");
        return mIBinder;
    }
}
