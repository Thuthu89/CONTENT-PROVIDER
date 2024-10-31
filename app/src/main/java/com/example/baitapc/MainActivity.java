package com.example.baitapc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
    private ListView listViewMessages;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewMessages = findViewById(R.id.listViewMessages);
        Button btnReadMessages = findViewById(R.id.btnReadMessages);

        btnReadMessages.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                readSmsMessages();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, PERMISSION_REQUEST_READ_SMS);
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
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) {
                        int addressIndex = cursor.getColumnIndex(Telephony.Sms.ADDRESS);
                        int bodyIndex = cursor.getColumnIndex(Telephony.Sms.BODY);

                        if (addressIndex != -1 && bodyIndex != -1) {
                            do {
                                String address = cursor.getString(addressIndex);
                                String body = cursor.getString(bodyIndex);
                                messagesList.add("From: " + address + "\nMessage: " + body);
                            } while (cursor.moveToNext());
                        } else {
                            Toast.makeText(this, "Không tìm thấy các cột cần thiết trong Cursor.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(this, "Không có tin nhắn nào.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Không thể truy xuất dữ liệu tin nhắn.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messagesList);
        listViewMessages.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_READ_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readSmsMessages();
            } else {
                Toast.makeText(this, "Permission denied to read SMS", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
