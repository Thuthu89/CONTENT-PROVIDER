package com.example.baitapc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_READ_SMS = 100;
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 101;
    private static final int PERMISSION_REQUEST_READ_CALL_LOG = 102;
    private ListView listViewMessages;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewMessages = findViewById(R.id.listViewMessages);
        Button btnReadMessages = findViewById(R.id.btnReadMessages);
        Button btnReadContacts = findViewById(R.id.btnReadContacts);
        Button btnReadCallLog = findViewById(R.id.btnReadCallLog);

        btnReadMessages.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                readSmsMessages();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, PERMISSION_REQUEST_READ_SMS);
            }
        });

        btnReadContacts.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                readContacts();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
            }
        });

        btnReadCallLog.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                readCallLog();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, PERMISSION_REQUEST_READ_CALL_LOG);
            }
        });
    }

    private void readSmsMessages() {
        ArrayList<String> messagesList = new ArrayList<>();
        Uri smsUri = Telephony.Sms.CONTENT_URI;
        String[] projection = new String[]{
                Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY
        };

        try (Cursor cursor = getContentResolver().query(smsUri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int addressIndex = cursor.getColumnIndex(Telephony.Sms.ADDRESS);
                int bodyIndex = cursor.getColumnIndex(Telephony.Sms.BODY);

                do {
                    String address = cursor.getString(addressIndex);
                    String body = cursor.getString(bodyIndex);
                    messagesList.add("From: " + address + "\nMessage: " + body);
                } while (cursor.moveToNext());
            } else {
                Toast.makeText(this, "Không có tin nhắn nào.", Toast.LENGTH_SHORT).show();
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messagesList);
        listViewMessages.setAdapter(adapter);
    }

    private void readContacts() {
        ArrayList<String> contactsList = new ArrayList<>();
        Uri contactsUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        try (Cursor cursor = getContentResolver().query(contactsUri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                do {
                    String name = cursor.getString(nameIndex);
                    String number = cursor.getString(numberIndex);
                    contactsList.add("Name: " + name + "\nPhone: " + number);
                } while (cursor.moveToNext());
            } else {
                Toast.makeText(this, "Không có danh bạ nào.", Toast.LENGTH_SHORT).show();
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactsList);
        listViewMessages.setAdapter(adapter);
    }

    private void readCallLog() {
        ArrayList<String> callLogList = new ArrayList<>();
        Uri callLogUri = CallLog.Calls.CONTENT_URI;
        String[] projection = new String[]{
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION
        };

        try (Cursor cursor = getContentResolver().query(callLogUri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
                int typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
                int dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
                int durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION);

                do {
                    String number = cursor.getString(numberIndex);
                    String type = cursor.getString(typeIndex);
                    String date = cursor.getString(dateIndex);
                    String duration = cursor.getString(durationIndex);
                    callLogList.add("Number: " + number + "\nType: " + type + "\nDate: " + date + "\nDuration: " + duration);
                } while (cursor.moveToNext());
            } else {
                Toast.makeText(this, "Không có lịch sử cuộc gọi.", Toast.LENGTH_SHORT).show();
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, callLogList);
        listViewMessages.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_READ_SMS && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            readSmsMessages();
        } else if (requestCode == PERMISSION_REQUEST_READ_CONTACTS && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            readContacts();
        } else if (requestCode == PERMISSION_REQUEST_READ_CALL_LOG && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            readCallLog();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
