package com.edushareteam.petshare.providers;

import androidx.annotation.NonNull;

import com.edushareteam.petshare.models.Post;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class PostProvider {

    CollectionReference mCollection;
    DatabaseReference databaseReference;

    public PostProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Posts");
    }

    public Task<Void> save(Post post) {
        return mCollection.document().set(post);
    }

    public Query getAll() {
        return mCollection.orderBy("timestamp", Query.Direction.DESCENDING);
    }

    public Query getPostByCategoryAndTimestamp(String category) {
        return mCollection.whereEqualTo("pet", category).orderBy("timestamp", Query.Direction.DESCENDING);
    }
    public DatabaseReference getCategoryForSpinner() {
        return databaseReference = FirebaseDatabase.getInstance().getReference("categories");
    }

    public Query getPostByTitle(String title) {
        return mCollection.orderBy("title").startAt(title).endAt(title+'\uf8ff');
    }

    public Query getPostByUser(String id) {
        return mCollection.whereEqualTo("idUser", id);
    }

    public Task<Void> updatePost(@NonNull Post post) {
        Map<String, Object> map = new HashMap<>();
        map.put("pet", post.getPet());
        map.put("description", post.getDescription());
        map.put("image1", post.getImage1());
        map.put("image2", post.getImage2());
        map.put("quality", post.getQuality());
        map.put("title", post.getTitle());
        map.put("expireTime", post.getExpireTime());
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
