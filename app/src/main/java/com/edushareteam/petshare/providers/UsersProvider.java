package com.edushareteam.petshare.providers;


import com.edushareteam.petshare.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UsersProvider {

    private final CollectionReference mCollection;

    DatabaseReference databaseReference;

    public UsersProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Users");


    }

    public Task<DocumentSnapshot> getUser(String id) {
        return mCollection.document(id).get();
    }

    public DocumentReference getUserRealtime(String id) {
        return mCollection.document(id);
    }

    public Task<Void> create(User user) {
        return mCollection.document(user.getId()).set(user);
    }

    public Task<Void> updateProfile(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", user.getUsername());
        map.put("bio", user.getBio());
        map.put("city", user.getCity());
        map.put("image", user.getImage());
        map.put("timestamp", user.getTimestamp());
        return mCollection.document(user.getId()).update(map);
    }
    public Task<Void> updateOnline(String idUser, boolean status) {
        Map<String, Object> map = new HashMap<>();
        map.put("online", status);
        map.put("lastConnect", new Date().getTime());
        return mCollection.document(idUser).update(map);
    }

    public Query getUserByUsername(String username) {
        return mCollection.orderBy("username").startAt(username).endAt(username+'\uf8ff');
    }

    public Query getUserByDepartment(String department) {
        return mCollection.orderBy("university").startAt(department).endAt(department+'\uf8ff');
    }

    public Query getAll() {
        return mCollection.orderBy("timestamp", Query.Direction.DESCENDING);
    }
    public DatabaseReference getUniversiteisForSpinner() {
        return databaseReference = FirebaseDatabase.getInstance().getReference("universities");
    }
}

