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
import androidx.recyclerview.widget.RecyclerView;


import com.edushareteam.petshare.R;
import com.edushareteam.petshare.activities.ProductDetailActivity;
import com.edushareteam.petshare.models.Like;
import com.edushareteam.petshare.providers.AuthProvider;
import com.edushareteam.petshare.providers.LikesProvider;
import com.edushareteam.petshare.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.ListenerRegistration;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class FavoriteAdapter extends FirestoreRecyclerAdapter<Like, FavoriteAdapter.ViewHolder> {

    Context context;
    UsersProvider mUsersProvider;
    LikesProvider mLikeProvider;
    AuthProvider mAuthProvider;
    TextView mTextViewNumberOfFavoriteItem;
    ListenerRegistration mListener;

    public FavoriteAdapter(FirestoreRecyclerOptions<Like> options, Context context, TextView textView) {
        super(options);
        this.context = context;
        mUsersProvider = new UsersProvider();
        mLikeProvider = new LikesProvider();
        mAuthProvider = new AuthProvider();
        mTextViewNumberOfFavoriteItem = textView;
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Like like) {
        //TODO: Favori kisminda veri olmadiginda usera bilgi verme
        if (mTextViewNumberOfFavoriteItem != null) {
            int numberFilter = getSnapshots().size();
            mTextViewNumberOfFavoriteItem.setText(String.valueOf(numberFilter));
        }



        switch (like.getPrice()) {
            case "0":
                holder.imageViewValue.setImageResource(R.drawable.heart_c);
                break;


        }

        switch (like.getCategory()) {
            case "Cats":
                holder.imageViewPet.setImageResource(R.drawable.cat);
                break;
            case "Dogs":
                holder.imageViewPet.setImageResource(R.drawable.dog);
                break;
            case "Birds":
                holder.imageViewPet.setImageResource(R.drawable.bird);
                break;
            case "Rabbits":
                holder.imageViewPet.setImageResource(R.drawable.rabbit);
                break;
            case "Horses":
                holder.imageViewPet.setImageResource(R.drawable.horse);
                break;
            case "Ferrets":
                holder.imageViewPet.setImageResource(R.drawable.ferret);
                break;
            case "Fish":
                holder.imageViewPet.setImageResource(R.drawable.fish);
                break;
            case "Guinea Pigs":
                holder.imageViewPet.setImageResource(R.drawable.pig);
                break;
            case "Rats and Mice":
                holder.imageViewPet.setImageResource(R.drawable.rat);
                break;
            case "Amphibians":
                holder.imageViewPet.setImageResource(R.drawable.turtle);
                break;
            case "Reptiles":
                holder.imageViewPet.setImageResource(R.drawable.chameleon);
                break;
        }

        holder.delete.setOnClickListener(view -> deletePost(like.getId()));
        holder.textViewTitle.setText(like.getTitle());
        //holder.textViewCategory.setText(like.getCategory());

        if (like.getImage() != null) {
            if (!like.getImage().isEmpty()) {
                Picasso.with(context).load(like.getImage()).into(holder.imageViewPost, new Callback() {
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
        holder.viewHolder.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("id", like.getIdPost());
           context.startActivity(intent);
        });


    }

    public ListenerRegistration getListener() {
        return mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardviewfavorite, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewCategory;

        ImageView imageViewPost;

        ImageView imageViewValue;
        ImageView imageViewPet;
        ImageView delete;
        View viewHolder;
        ProgressBar bar;

        public ViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.textViewTitlePostCard);

            delete = view.findViewById(R.id.imageViewLike);
            imageViewPost = view.findViewById(R.id.imageViewPostCard);

            imageViewValue=view.findViewById(R.id.imageViewValue);
            imageViewPet=view.findViewById(R.id.imageViewPets);
            bar = view.findViewById(R.id.postLoading);
            viewHolder = view;
        }
    }

    private void deletePost(String postId) {
        mLikeProvider.delete(postId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Yayın başarıyla kaldırıldı", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Gönderi silinemedi", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
