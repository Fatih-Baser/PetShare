package com.edushareteam.petshare.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


import com.edushareteam.petshare.R;
import com.edushareteam.petshare.activities.ProductDetailActivity;
import com.edushareteam.petshare.models.Like;
import com.edushareteam.petshare.models.Post;
import com.edushareteam.petshare.providers.AuthProvider;
import com.edushareteam.petshare.providers.LikesProvider;
import com.edushareteam.petshare.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class PostsAdapter extends FirestoreRecyclerAdapter<Post, PostsAdapter.ViewHolder> {

    Context context;
    UsersProvider mUsersProvider;
    LikesProvider mLikesProvider;
    AuthProvider mAuthProvider;
    TextView mTextViewNumberFilter;
    ListenerRegistration mListener;

    public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context context) {
        super(options);
        this.context = context;
        mUsersProvider = new UsersProvider();
        mLikesProvider = new LikesProvider();
        mAuthProvider = new AuthProvider();
    }

    public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context context, TextView textView) {
        super(options);
        this.context = context;
        mUsersProvider = new UsersProvider();
        mLikesProvider = new LikesProvider();
        mAuthProvider = new AuthProvider();
        mTextViewNumberFilter = textView;
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Post post) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String postId = document.getId();

        if (mTextViewNumberFilter != null) {
            int numberFilter = getSnapshots().size();
            mTextViewNumberFilter.setText(String.valueOf(numberFilter));
        }

        holder.textViewTitle.setText(post.getTitle());
        //holder.textViewCategory.setText(post.getPet());
        if (post.getImage1() != null) {
            if (!post.getImage1().isEmpty()) {
                Picasso.with(context).load(post.getImage1()).into(holder.imageViewPost, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.bar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        holder.imageViewPost.setImageResource(R.drawable.ic_baseline_error_24);
                        holder.bar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }
        holder.price.setText(post.getPrice());
        switch (post.getPrice()) {
            case "0":
                holder.imageviewvalue.setImageResource(R.drawable.heart_c);
                break;


        }


        holder.pet.setText(post.getPet());
        switch (post.getPet()) {
            case "Cats":
                holder.imageviewPets.setImageResource(R.drawable.cat);
                break;
            case "Dogs":
                holder.imageviewPets.setImageResource(R.drawable.dog);
                break;
            case "Birds":
                holder.imageviewPets.setImageResource(R.drawable.bird);
                break;
            case "Rabbits":
                holder.imageviewPets.setImageResource(R.drawable.rabbit);
                break;
            case "Horses":
                holder.imageviewPets.setImageResource(R.drawable.horse);
                break;
            case "Ferrets":
                holder.imageviewPets.setImageResource(R.drawable.ferret);
                break;
            case "Fish":
                holder.imageviewPets.setImageResource(R.drawable.fish);
                break;
            case "Guinea Pigs":
                holder.imageviewPets.setImageResource(R.drawable.pig);
                break;
            case "Rats and Mice":
                holder.imageviewPets.setImageResource(R.drawable.rat);
                break;
            case "Amphibians":
                holder.imageviewPets.setImageResource(R.drawable.turtle);
                break;
            case "Reptiles":
                holder.imageviewPets.setImageResource(R.drawable.chameleon);
                break;
        }

        holder.viewHolder.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
           intent.putExtra("id", postId);
           context.startActivity(intent);
        });

        holder.imageViewLike.setOnClickListener(view -> {
            Like like = new Like();
            like.setIdUser(mAuthProvider.getUid());
            like.setIdPost(postId);
            like.setTitle(post.getTitle());
            like.setCategory(post.getPet());
            like.setImage(post.getImage1());
            like.setPrice(post.getPrice());
            like.setTimestamp(new Date().getTime());
            like(like, holder);
        });

        // getUserInfo(post.getIdUser(), holder);
        getNumberLikesByPost(postId, holder);
        checkIfExistLike(postId, mAuthProvider.getUid(), holder);
    }


    private void getNumberLikesByPost(String idPost, final ViewHolder holder) {
        mListener = mLikesProvider.getLikesByPost(idPost).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    int numberLikes = queryDocumentSnapshots.size();
                    //holder.textViewLikes.setText(String.valueOf(numberLikes) + " BEĞENİ");
                }
            }
        });
    }

    private void like(final Like like, final ViewHolder holder) {
        mLikesProvider.getLikeByPostAndUser(like.getIdPost(), mAuthProvider.getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            int numberDocuments = queryDocumentSnapshots.size();
            if (numberDocuments > 0) {
                String idLike = queryDocumentSnapshots.getDocuments().get(0).getId();
                holder.imageViewLike.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
                mLikesProvider.delete(idLike);
            } else {
                holder.imageViewLike.setImageResource(R.drawable.ic_baseline_bookmark_24);
                mLikesProvider.create(like);
            }
        });

    }

    private void checkIfExistLike(String idPost, String idUser, final ViewHolder holder) {
        mLikesProvider.getLikeByPostAndUser(idPost, idUser).get().addOnSuccessListener(queryDocumentSnapshots -> {
            int numberDocuments = queryDocumentSnapshots.size();
            if (numberDocuments > 0) {
                holder.imageViewLike.setImageResource(R.drawable.ic_baseline_bookmark_24);
            } else {
                holder.imageViewLike.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
            }
        });

    }

    public ListenerRegistration getListener() {
        return mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //TODO: burda bazen uygulama patlıyor kayıt ve giriş yaparken kardeşimin telefonunda oldu android 6.0.1 alttaki iki satırı gösteriyor hata
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView pet;
        TextView price;
        ImageView imageViewPost;
        ImageView imageviewvalue;
        ImageView imageViewLike;
        ImageView imageviewPets;
        View viewHolder;
        ProgressBar bar;

        public ViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.textViewTitlePostCard);
           // textViewLikes = view.findViewById(R.id.textViewLikes);
            imageViewPost = view.findViewById(R.id.imageViewPostCard);
            imageviewvalue = view.findViewById(R.id.imageViewValue);
            imageviewPets=view.findViewById(R.id.imageViewPets);
            imageViewLike = view.findViewById(R.id.imageViewLike);
            pet=view.findViewById(R.id.pet);
            price=view.findViewById(R.id.price);
            bar = view.findViewById(R.id.postLoading);
            viewHolder = view;
        }
    }

}
