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

    public void ReadFirebase(String tableName, String filter, String[] selectionArgs, DataCallback callback) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(tableName);

        Query query = database;
        // Nếu có filter, chúng ta sẽ sử dụng phương thức orderByChild để lọc dữ liệu
        if (filter != null && !filter.isEmpty()) {
//            database = (DatabaseReference) database.orderByChild(filter);  // Đặt điều kiện lọc theo trường filter
            query = database.orderByChild(filter);
        }

        // Lắng nghe dữ liệu từ Firebase
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<List<String>> results = new ArrayList<>();

                // Duyệt qua các node con (bản ghi) trong Firebase
                for (DataSnapshot data : snapshot.getChildren()) {
                    List<String> row = new ArrayList<>();

                    // Nếu selectionArgs không phải null, chỉ lấy các trường có trong selectionArgs
                    if (selectionArgs != null) {
                        for (String fieldName : selectionArgs) {
                            DataSnapshot fieldSnapshot = data.child(fieldName);
                            if (fieldSnapshot.exists()) {
                                // Kiểm tra kiểu dữ liệu
                                Object fieldValue = fieldSnapshot.getValue();
                                if (fieldValue instanceof Long) {
                                    row.add(String.valueOf(fieldValue)); // Chuyển Long thành String
                                } else if (fieldValue instanceof String) {
                                    row.add((String) fieldValue);  // Dữ liệu đã là String
                                } else {
                                    row.add(null);  // Không hỗ trợ kiểu dữ liệu này
                                }
                            } else {
                                row.add(null);
                            }
                        }

                    } else {

                        for (DataSnapshot field : data.getChildren()) {
                            Object fieldValue = field.getValue();
                            if (fieldValue instanceof Long) {
                                row.add(String.valueOf(fieldValue));
                            } else if (fieldValue instanceof String) {
                                row.add((String) fieldValue);
                            } else {
                                row.add(null);
                            }
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

    public void updateFirebase(String tableName, String recordId, String[] fields, Object[] values, DataCallback callback) {
        if (fields == null || values == null || fields.length != values.length) {
            Log.e("Firebase", "Fields and values must be non-null and have the same length.");
            return;
        }

        DatabaseReference database = FirebaseDatabase.getInstance().getReference(tableName);

        // Chuyển đổi fields và values thành một Map
        Map<String, Object> updatedValues = new HashMap<>();
        for (int i = 0; i < fields.length; i++) {
            updatedValues.put(fields[i], values[i]);
        }

        // Truy cập bản ghi cần cập nhật và thực hiện cập nhật
        database.child(recordId).updateChildren(updatedValues)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Record updated successfully!");
                    callback.onDataLoaded(Collections.singletonList(Collections.singletonList("Success")));
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Error updating record: " + e.getMessage());
                });
    }

//    public void deleteRecordFromFirebase(String tableName, String[] fields, Object[] values, DataCallback callback) {
//        if (fields == null || values == null || fields.length != values.length) {
//            Log.e("Firebase", "Fields and values must be non-null and have the same length.");
//            return;
//        }
//
//        DatabaseReference database = FirebaseDatabase.getInstance().getReference(tableName);
//
//        database.orderByChild(fields[0]).equalTo(values[0].toString()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    for (DataSnapshot data : snapshot.getChildren()) {
//                        boolean matches = true;
//
//                        // Kiểm tra từng trường và giá trị để đảm bảo tất cả đều khớp
//                        for (int i = 0; i < fields.length; i++) {
//                            DataSnapshot fieldSnapshot = data.child(fields[i]);
//                            if (fieldSnapshot.exists()) {
//                                Object fieldValue = fieldSnapshot.getValue();
//                                if (fieldValue == null || !values[i].toString().equals(fieldValue.toString())) {
//                                    matches = false;
//                                    break;
//                                }
//                            } else {
//                                matches = false;
//                                break;
//                            }
//                        }
//
//                        // Nếu bản ghi khớp với mọi điều kiện, xóa bản ghi đó
//                        if (matches) {
//                            data.getRef().removeValue()
//                                    .addOnSuccessListener(aVoid -> {
//                                        Log.d("Firebase", "Record deleted successfully!");
//                                        callback.onDataLoaded(Collections.singletonList(Collections.singletonList("Success")));
//                                    })
//                                    .addOnFailureListener(e -> {
//                                        Log.e("Firebase", "Error deleting record: " + e.getMessage());
//                                    });
//                        }
//                    }
//                } else {
//                    Log.d("Firebase", "No records found to delete.");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("Firebase", "Error loading data: " + error.getMessage());
//            }
//        });
//    }

    public void deleteFirebase(String tableName, String[] fields, Object[] values, DataCallback callback) {
        if (fields == null || values == null || fields.length != values.length) {
            Log.e("Firebase", "Fields and values must be non-null and have the same length.");
            return;
        }

        DatabaseReference database = FirebaseDatabase.getInstance().getReference(tableName);

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean recordDeleted = false;

                // Lặp qua từng bản ghi để kiểm tra các điều kiện
                for (DataSnapshot data : snapshot.getChildren()) {
                    boolean matchesAllConditions = true;

                    // Kiểm tra từng trường và giá trị
                    for (int i = 0; i < fields.length; i++) {
                        DataSnapshot fieldSnapshot = data.child(fields[i]);

                        // Kiểm tra nếu trường không tồn tại hoặc không khớp giá trị
                        if (!fieldSnapshot.exists() || !fieldSnapshot.getValue().toString().equals(values[i].toString())) {
                            matchesAllConditions = false;
                            break;
                        }
                    }

                    // Nếu bản ghi khớp với tất cả điều kiện, xóa nó
                    if (matchesAllConditions) {
                        data.getRef().removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firebase", "Record deleted successfully!");
                                    callback.onDataLoaded(Collections.singletonList(Collections.singletonList("Success")));
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firebase", "Error deleting record: " + e.getMessage());
                                });
                        recordDeleted = true;
                    }
                }

                if (!recordDeleted) {
                    Log.d("Firebase", "No records found to delete.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error loading data: " + error.getMessage());
            }
        });
    }







    public void deleteFirebaseID(String tableName, String recordId, DataCallback callback) {
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

    public void readFirebaseIntIndex(String tableName, String filter, String filterValue, String[] selectionArgs, DataCallback callback) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(tableName);

        // Nếu có filter, thực hiện truy vấn
        if (filter != null && filterValue != null) {
            database = (DatabaseReference) database.orderByChild(filter).equalTo(filterValue);
        }

        // Đọc dữ liệu từ Firebase
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<List<String>> results = new ArrayList<>();

                for (DataSnapshot data : snapshot.getChildren()) {
                    List<String> row = new ArrayList<>();

                    if (selectionArgs != null) {
                        for (String fieldName : selectionArgs) {
                            DataSnapshot fieldSnapshot = data.child(fieldName);
                            row.add(fieldSnapshot.exists() ? fieldSnapshot.getValue(String.class) : null);
                        }
                    } else {
                        for (DataSnapshot field : data.getChildren()) {
                            row.add(field.getValue(String.class));
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

//    public void createFirebase(String tableName, String[] fields, Object[] values, DataCallback callback) {
//        if (fields == null || values == null || fields.length != values.length) {
//            Log.e("Firebase", "Fields and values must be non-null and of the same length!");
//            return;
//        }
//
//        DatabaseReference database = FirebaseDatabase.getInstance().getReference(tableName);
//
//        // Tạo Map để lưu trữ các cặp field-value
//        Map<String, Object> data = new HashMap<>();
//        for (int i = 0; i < fields.length; i++) {
//            data.put(fields[i], values[i]);
//        }
//
//        // Tạo một bản ghi mới trong Firebase
//        database.push().setValue(data)
//                .addOnSuccessListener(unused -> {
//                    // Thành công, gọi callback
//                    callback.onDataLoaded(Collections.singletonList(Collections.singletonList("Success")));
//                    Log.d("Firebase", "Record added successfully!");
//                })
//                .addOnFailureListener(e -> {
//                    // Thất bại, log lỗi
//                    Log.e("Firebase", "Failed to add record: " + e.getMessage());
//                });
//    }


    public interface DataMapCallback {
        void onDataLoaded(List<Map<String, String>> data);
    }

}
