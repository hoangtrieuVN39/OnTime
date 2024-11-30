package com.example.checkin;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListUtil;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CRUD {
    DatabaseReference database;

    public CRUD(Context context){
        FirebaseApp.initializeApp(context);
        database = FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getDatabase() {
        return database;
    }

    public void createFirebase(String tableName, String filter, String[] selectionArgs, DataCallback callback) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(tableName);

        // Nếu có filter, chúng ta sẽ sử dụng phương thức orderByChild để lọc dữ liệu
        if (filter != null && !filter.isEmpty()) {
            database = (DatabaseReference) database.orderByChild(filter);  // Đặt điều kiện lọc theo trường filter
        }


        // Lắng nghe dữ liệu từ Firebase
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<List<String>> results = new ArrayList<>();

                // Duyệt qua các node con (bản ghi) trong Firebase
                for (DataSnapshot data : snapshot.getChildren()) {
                    List<String> row = new ArrayList<>();

                    // Nếu selectionArgs không phải null, chỉ lấy các trường có trong selectionArgs
                    if (selectionArgs != null) {
                        for (String fieldName : selectionArgs) {
                            DataSnapshot fieldSnapshot = data.child(fieldName);  // Lấy giá trị của trường theo tên
                            if (fieldSnapshot.exists()) {
                                row.add(fieldSnapshot.getValue(String.class));  // Lấy giá trị trường dưới dạng String
                            } else {
                                row.add(null);  // Nếu trường không tồn tại, thêm giá trị null
                            }
                        }
                    } else {
                        // Nếu không có selectionArgs, lấy toàn bộ dữ liệu của bản ghi
                        for (DataSnapshot field : data.getChildren()) {
                            row.add(field.getValue(String.class));  // Lấy giá trị trường dưới dạng String
                        }
                    }

                    // Thêm dòng vào kết quả
                    results.add(row);
                }

                // Gọi callback trả về kết quả
                callback.onDataLoaded(results);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error loading data: " + error.getMessage());
            }
        });
    }

    public void updateFirebase(String tableName, String recordId, Map<String, Object> updatedValues, DataCallback callback) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(tableName);

        // Truy cập bản ghi cần cập nhật
        database.child(recordId).updateChildren(updatedValues)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Record updated successfully");
                    callback.onDataLoaded(Collections.singletonList(Collections.singletonList("Success")));
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Error updating record: " + e.getMessage());
                });
    }
    public void deleteFirebase(String tableName, String recordId, DataCallback callback) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(tableName);

        // Xóa bản ghi theo recordId
        database.child(recordId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Record deleted successfully");
                    callback.onDataLoaded(Collections.singletonList(Collections.singletonList("Deleted")));
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Error deleting record: " + e.getMessage());
                });
    }

//    public void readFirebaseIntIndex(String tableName, String filter, String filterValue, String[] selectionArgs, DataCallback callback) {
//        DatabaseReference database = FirebaseDatabase.getInstance().getReference(tableName);
//
//        // Nếu có filter, thực hiện truy vấn
//        if (filter != null && filterValue != null) {
//            database = (DatabaseReference) database.orderByChild(filter).equalTo(filterValue);
//        }
//
//        // Đọc dữ liệu từ Firebase
//        database.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                List<List<String>> results = new ArrayList<>();
//
//                for (DataSnapshot data : snapshot.getChildren()) {
//                    List<String> row = new ArrayList<>();
//
//                    if (selectionArgs != null) {
//                        for (String fieldName : selectionArgs) {
//                            DataSnapshot fieldSnapshot = data.child(fieldName);
//                            row.add(fieldSnapshot.exists() ? fieldSnapshot.getValue(String.class) : null);
//                        }
//                    } else {
//                        for (DataSnapshot field : data.getChildren()) {
//                            row.add(field.getValue(String.class));
//                        }
//                    }
//
//                    results.add(row);
//                }
//
//                callback.onDataLoaded(results);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("Firebase", "Error reading data: " + error.getMessage());
//            }
//        });
//    }

    public void readFirebaseStringIndex(String tableName, String filter, String filterValue, String[] selectionArgs, DataMapCallback callback) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(tableName);

        // Thực hiện lọc nếu có filter
        Query query = database;
        if (filter != null && filterValue != null) {
//            database = (DatabaseReference) database.orderByChild(filter).equalTo(filterValue);
            query = database.orderByChild(filter).equalTo(filterValue);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Map<String, String>> results = new ArrayList<>();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Map<String, String> row = new HashMap<>();

                    if (selectionArgs != null) {
                        for (DataSnapshot field : data.getChildren()) {
                            Object value = field.getValue();
                            if (value != null) {
                                row.put(field.getKey(), value.toString()); // Chuyển đổi sang String nếu có giá trị
                            } else {
                                row.put(field.getKey(), null); // Nếu không tồn tại, thêm null
                            }
                        }
                    } else {
                        for (DataSnapshot field : data.getChildren()) {
                            row.put(field.getKey(), field.getValue(String.class));
                        }
                    }

                    results.add(row);
                }

                callback.onDataLoaded(results);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error reading data: " + error.getMessage());
            }
        });
    }
    public interface DataMapCallback {
        void onDataLoaded(List<Map<String, String>> data);
    }




}
