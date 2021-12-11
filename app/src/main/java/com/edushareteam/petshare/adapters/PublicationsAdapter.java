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
import androidx.recyclerview.widget.RecyclerView;


import com.edushareteam.petshare.R;
import com.edushareteam.petshare.activities.UserProfileActivity;
import com.edushareteam.petshare.models.Like;
import com.edushareteam.petshare.models.Post;
import com.edushareteam.petshare.models.Request;
import com.edushareteam.petshare.models.User;
import com.edushareteam.petshare.providers.AuthProvider;
import com.edushareteam.petshare.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PublicationsAdapter extends FirestoreRecyclerAdapter<Request, PublicationsAdapter.ViewHolder> {

    Context context;
    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    TextView mTextViewNumberFilter;
    ListenerRegistration mListener;

    public PublicationsAdapter(FirestoreRecyclerOptions<Request> options, Context context) {
        super(options);
        this.context = context;
        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Request request) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String userId = document.getId();
        User user = new User();
        request.setImageProfile(user.getImage());
        holder.textViewTitle.setText(request.getTitle());
        holder.textViewBio.setText(request.getBio());

        if (request.getImageProfile() != null) {
            if (!request.getImageProfile().isEmpty()) {
                Picasso.with(context).load(request.getImageProfile()).into(holder.imageViewUser, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.bar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        holder.imageViewUser.setImageResource(R.drawable.ic_baseline_error_24);
                        holder.bar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }
        holder.viewHolder.setOnClickListener(view -> {
            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra("idUser", request.getIdUser());
            context.startActivity(intent);
        });

    }

    public ListenerRegistration getListener() {
        return mListener;
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

        ImageView imageViewUser;

        View viewHolder;
        ProgressBar bar;

        public ViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.textViewTitle);
            textViewBio = view.findViewById(R.id.textViewbio);
            imageViewUser = view.findViewById(R.id.imageViewProfil);
            bar = view.findViewById(R.id.postLoading);
            viewHolder = view;
        }
    }

}
