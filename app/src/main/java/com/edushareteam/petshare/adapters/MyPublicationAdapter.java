package com.edushareteam.petshare.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.edushareteam.petshare.R;
import com.edushareteam.petshare.activities.EditProductActivity;
import com.edushareteam.petshare.activities.ProductDetailActivity;
import com.edushareteam.petshare.models.Post;
import com.edushareteam.petshare.models.Request;
import com.edushareteam.petshare.providers.AuthProvider;
import com.edushareteam.petshare.providers.LikesProvider;
import com.edushareteam.petshare.providers.PostProvider;
import com.edushareteam.petshare.providers.UsersProvider;
import com.edushareteam.petshare.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MyPublicationAdapter extends FirestoreRecyclerAdapter<Request, MyPublicationAdapter.ViewHolder> {

    Context context;
    UsersProvider mUsersProvider;
    LikesProvider mLikesProvider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;

    public MyPublicationAdapter(FirestoreRecyclerOptions<Request> options, Context context) {
        super(options);
        this.context = context;
        mUsersProvider = new UsersProvider();
        mLikesProvider = new LikesProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Request request) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String postId = document.getId();
        String relativeTime = RelativeTime.getTimeAgo(request.getTimestamp(), context);
        holder.textViewRelativeTime.setText(relativeTime);
        holder.textViewTitle.setText(request.getTitle());
        holder.textViewBio.setText(request.getBio());

        if (request.getImageProfile() != null) {
            if (!request.getImageProfile().isEmpty()) {
                Picasso.with(context).load(request.getImageProfile()).into(holder.circleImagePost, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });
            }
        }

       /* holder.viewHolder.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("id", postId);
            context.startActivity(intent);
        });*/

       /* holder.imageViewDelete.setOnClickListener(view -> showConfirmDelete(postId));
        holder.imageViewEdit.setOnClickListener(view -> {
            Intent intent =  new Intent(context, EditProductActivity.class);
            intent.putExtra("id",postId);
            context.startActivity(intent);
        });*/
    }

    private void showConfirmDelete(final String postId) {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Gönderiyi sil")
                .setMessage("Bu eylemi gerçekleştireceğinizden emin misiniz?")
                .setPositiveButton("Evet", (dialogInterface, i) -> deletePost(postId))
                .setNegativeButton("Hayir", null)
                .show();
    }

    private void deletePost(String postId) {
        mPostProvider.delete(postId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Yayın başarıyla kaldırıldı", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Gönderi silinemedi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_publication, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewBio;
        TextView textViewRelativeTime;
        ImageView circleImagePost;
        ImageView imageViewDelete;
        ImageView imageViewEdit;
        View viewHolder;

        ProgressBar bar;
        public ViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.publicationTitle);
            textViewBio = view.findViewById(R.id.publicationDescription);
            textViewRelativeTime = view.findViewById(R.id.textViewRelativeTimeMyPost);
            circleImagePost = view.findViewById(R.id.imageViewPublicationProfileImage);
            /*imageViewDelete = view.findViewById(R.id.imageViewDeleteMyPost);
            imageViewEdit = view.findViewById(R.id.imageViewEditMyPost);
            bar = view.findViewById(R.id.postLoading);*/
            viewHolder = view;
        }
    }

}
