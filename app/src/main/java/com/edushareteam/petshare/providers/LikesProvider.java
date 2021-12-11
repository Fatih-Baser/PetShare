package com.edushareteam.petshare.providers;


import com.edushareteam.petshare.models.Like;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class LikesProvider {

    CollectionReference mCollection;

    public LikesProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Likes");
    }

    public Task<Void> create(Like like) {
        DocumentReference document = mCollection.document();
        String id = document.getId();
        like.setId(id);
        return document.set(like);
    }

    public Query getLikesByPost(String idPost) {
        return mCollection.whereEqualTo("idPost", idPost);
    }

    public Query getLikeByPostAndUser(String idPost, String idUser) {
        return mCollection.whereEqualTo("idPost", idPost).whereEqualTo("idUser", idUser);
    }

    public Query getLikeByPostByUser(String id) {
        return mCollection.whereEqualTo("idUser", id);
    }

    public Query getLikes(String idPost, String idLikesPost) {
        return mCollection.whereEqualTo("id", idPost).whereEqualTo("idPost", idLikesPost);
    }

    public Task<Void> delete(String id) {
        return mCollection.document(id).delete();
    }

    public Query getAll() {
        return mCollection.orderBy("timestamp", Query.Direction.DESCENDING);
    }

}


