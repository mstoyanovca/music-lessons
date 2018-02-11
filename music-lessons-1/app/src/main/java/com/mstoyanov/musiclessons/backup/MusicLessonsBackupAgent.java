package com.mstoyanov.musiclessons.backup;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import android.os.ParcelFileDescriptor;

import com.mstoyanov.musiclessons.data.DataProvider;
import com.mstoyanov.musiclessons.data.SchoolContract;

import java.io.IOException;

public class MusicLessonsBackupAgent extends BackupAgentHelper {
    private static final String DB_NAME = "../databases/" + SchoolContract.DATABASE_NAME;
    // shared preferences file name:
    private static final String PREFS = "Settings";
    // helper keys:
    private static final String BU_KEY_DATA = "mydata";
    private static final String BU_KEY_PREFS = "myprefs";

    @Override
    public void onCreate() {
        FileBackupHelper dhelper = new FileBackupHelper(this, DB_NAME);
        addHelper(BU_KEY_DATA, dhelper);
        SharedPreferencesBackupHelper phelper = new SharedPreferencesBackupHelper(this, PREFS);
        addHelper(BU_KEY_PREFS, phelper);
    }

    @Override
    public void onBackup(
            ParcelFileDescriptor oldState,
            BackupDataOutput data,
            ParcelFileDescriptor newState) throws IOException {
        synchronized (DataProvider.sDataLock) {
            super.onBackup(oldState, data, newState);
        }
    }

    @Override
    public void onRestore(
            BackupDataInput data,
            int appVersionCode,
            ParcelFileDescriptor newState) throws IOException {
        synchronized (DataProvider.sDataLock) {
            super.onRestore(data, appVersionCode, newState);
        }
    }
}