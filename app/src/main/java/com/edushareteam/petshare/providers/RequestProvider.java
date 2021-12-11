package com.edushareteam.petshare.providers;

import androidx.annotation.NonNull;

import com.edushareteam.petshare.models.Post;
import com.edushareteam.petshare.models.Request;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class RequestProvider {
    CollectionReference mCollection;
    DatabaseReference databaseReference;

    public RequestProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Requests");
    }

    public Task<Void> save(Request request) {
        return mCollection.document().set(request);
    }

    public Query getAll() {
        return mCollection.orderBy("timestamp", Query.Direction.DESCENDING);
    }

    public Query getPostByCategoryAndTimestamp(String category) {
        return mCollection.whereEqualTo("title", category).orderBy("timestamp", Query.Direction.DESCENDING);
    }
    public DatabaseReference getCategoryForSpinner() {
        return databaseReference = FirebaseDatabase.getInstance().getReference("title");
    }

    public Query getPostByTitle(String title) {
        return mCollection.orderBy("title").startAt(title).endAt(title+'\uf8ff');
    }

    public Query getPostByUser(String id) {
        return mCollection.whereEqualTo("idUser", id);
    }

    public Task<Void> updateRequest(@NonNull Request post) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", post.getTitle());
        map.put("bio", post.getBio());
        map.put("imageProfile", post.getImageProfile());
        map.put("timestamp", post.getTimestamp());
        return mCollection.document(post.getId()).update(map);
    }

    public Task<DocumentSnapshot> getPostById(String id) {
        return mCollection.document(id).get();
    }

    public Task<Void> delete(String id) {
        return mCollection.document(id).delete();
    }

}
