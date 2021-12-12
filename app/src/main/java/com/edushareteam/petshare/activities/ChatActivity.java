package com.edushareteam.petshare.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.edushareteam.petshare.R;
import com.edushareteam.petshare.adapters.MessagesAdapter;
import com.edushareteam.petshare.databinding.ActivityChatBinding;
import com.edushareteam.petshare.models.Chat;
import com.edushareteam.petshare.models.FCMBody;
import com.edushareteam.petshare.models.FCMResponse;
import com.edushareteam.petshare.models.Message;
import com.edushareteam.petshare.providers.AuthProvider;
import com.edushareteam.petshare.providers.ChatsProvider;
import com.edushareteam.petshare.providers.MessagesProvider;
import com.edushareteam.petshare.providers.NotificationProvider;
import com.edushareteam.petshare.providers.TokenProvider;
import com.edushareteam.petshare.providers.UsersProvider;
import com.edushareteam.petshare.utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    String mExtraIdUser1;
    String mExtraIdUser2;
    String mExtraIdChat;

    long mIdNotificationChat;

    ChatsProvider mChatsProvider;
    MessagesProvider mMessagesProvider;
    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    NotificationProvider mNotificationProvider;
    TokenProvider mTokenProvider;

    CircleImageView mCircleImageProfile;
    TextView mTextViewUsername;
    TextView mTextViewRelativeTime;
    ImageView mImageViewBack;

    MessagesAdapter mAdapter;

    View mActionBarView;
    Button button;

    ConstraintLayout abc;
    LinearLayoutManager mLinearLayoutManager;

    ListenerRegistration mListener;

    String mMyUsername;
    String mUsernameChat;
    String mImageReceiver = "";
    String mImageSender = "";

    private ActivityChatBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //Provider
        mChatsProvider = new ChatsProvider();
        mMessagesProvider = new MessagesProvider();
        mAuthProvider = new AuthProvider();
        mUsersProvider = new UsersProvider();
        mNotificationProvider = new NotificationProvider();
        mTokenProvider = new TokenProvider();

        mLinearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mLinearLayoutManager.setStackFromEnd(true);
        binding.recyclerViewMessage.setLayoutManager(mLinearLayoutManager);


        mExtraIdUser1 = getIntent().getStringExtra("idUser1");
        mExtraIdUser2 = getIntent().getStringExtra("idUser2");
        mExtraIdChat  = getIntent().getStringExtra("idChat");

        getMyInfoUser();
        showCustomToolbar(R.layout.custom_chat_toolbar);
        getMyInfoUser();

        binding.imageViewSendMessage.setOnClickListener(view1 -> sendMessage());
        checkIfChatExist();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
        ViewedMessageHelper.updateOnline(true, ChatActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, ChatActivity.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListener != null) {
            mListener.remove();
        }
    }

    private void getMessageChat() {
        Query query = mMessagesProvider.getMessageByChat(mExtraIdChat);
        FirestoreRecyclerOptions<Message> options =
                new FirestoreRecyclerOptions.Builder<Message>()
                        .setQuery(query, Message.class)
                        .build();
        mAdapter = new MessagesAdapter(options, ChatActivity.this);
        binding.recyclerViewMessage.setAdapter(mAdapter);
        mAdapter.startListening();
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                updateViewed();
                int numberMessage = mAdapter.getItemCount();
                int lastMessagePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastMessagePosition == -1 || (positionStart >= (numberMessage -1) && lastMessagePosition == (positionStart - 1))) {
                    binding.recyclerViewMessage.scrollToPosition(positionStart);
                }
            }
        });
    }

    private void sendMessage() {
        String textMessage = binding.editTextMessage.getText().toString();
        if (!textMessage.isEmpty()) {
            final Message message = new Message();
            message.setIdChat(mExtraIdChat);
            if (mAuthProvider.getUid().equals(mExtraIdUser1)) {
                message.setIdSender(mExtraIdUser1);
                message.setIdReceiver(mExtraIdUser2);
            }
            else {
                message.setIdSender(mExtraIdUser2);
                message.setIdReceiver(mExtraIdUser1);
            }
            message.setTimestamp(new Date().getTime());
            message.setViewed(false);
            message.setIdChat(mExtraIdChat);
            message.setMessage(textMessage);

            mMessagesProvider.create(message).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    binding.editTextMessage.setText("");
                    mAdapter.notifyDataSetChanged();
                    getToken(message);
                }
                else {
                    Toast.makeText(ChatActivity.this, "Mesaj oluşturulamadı", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showCustomToolbar(int resource) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!= null){
            actionBar.setTitle("");
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mActionBarView = inflater.inflate(resource, null);
            actionBar.setCustomView(mActionBarView);
        }
        mCircleImageProfile = mActionBarView.findViewById(R.id.circleImageProfile);
        mTextViewUsername = mActionBarView.findViewById(R.id.textViewUsername);
        mTextViewRelativeTime = mActionBarView.findViewById(R.id.textViewRelativeTime);
        mImageViewBack = mActionBarView.findViewById(R.id.imageViewBack);

        abc=mActionBarView.findViewById(R.id.abc);

        abc.setOnClickListener(view1 -> goToShowProfile());


        mImageViewBack.setOnClickListener(view -> finish());
        getUserInfo();
    }
    private void goToShowProfile() {
        if (!mExtraIdUser1.equals(mAuthProvider.getUid())) {
            Intent intent = new Intent(ChatActivity.this, UserProfileActivity.class);
            intent.putExtra("idUser", mExtraIdUser1);
            System.out.println(mExtraIdUser1+"idUser");
            startActivity(intent);
        }
        else if(!mExtraIdUser2.equals(mAuthProvider.getUid())){
            Intent intent = new Intent(ChatActivity.this, UserProfileActivity.class);
            intent.putExtra("idUser", mExtraIdUser2);
            System.out.println(mExtraIdUser2+"idUser");
            startActivity(intent);

        }
        else {
            Toast.makeText(this, "Kullanıcı kimliği hala yüklenmedi", Toast.LENGTH_SHORT).show();
        }
    }
    private void getUserInfo() {
        String idUserInfo;
        if (mAuthProvider.getUid().equals(mExtraIdUser1)) {
            idUserInfo = mExtraIdUser2;
        }
        else {
            idUserInfo = mExtraIdUser1;
        }

        mListener = mUsersProvider.getUserRealtime(idUserInfo).addSnapshotListener((documentSnapshot, e) -> {
            assert documentSnapshot != null;
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("username")) {
                    mUsernameChat = documentSnapshot.getString("username");
                    mTextViewUsername.setText(mUsernameChat);
                }
                if (documentSnapshot.contains("image")) {
                    mImageReceiver = documentSnapshot.getString("image");
                    if (mImageReceiver != null) {
                        if (!mImageReceiver.equals("")) {
                            Picasso.with(ChatActivity.this).load(mImageReceiver).into(mCircleImageProfile);
                        }
                    }
                }
            }
        });
    }

    private void checkIfChatExist() {
        mChatsProvider.getChatByUser1AndUser2(mExtraIdUser1, mExtraIdUser2).get().addOnSuccessListener(queryDocumentSnapshots -> {
            int size = queryDocumentSnapshots.size();
            if (size == 0) {
                createChat();
            }
            else {
                mExtraIdChat = queryDocumentSnapshots.getDocuments().get(0).getId();
                mIdNotificationChat = queryDocumentSnapshots.getDocuments().get(0).getLong("idNotification");
                getMessageChat();
                updateViewed();
            }
        });
    }

    private void updateViewed() {
        String idSender;

        if (mAuthProvider.getUid().equals(mExtraIdUser1)) {
            idSender = mExtraIdUser2;
        }
        else {
            idSender = mExtraIdUser1;
        }

        mMessagesProvider.getMessagesByChatAndSender(mExtraIdChat, idSender).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                mMessagesProvider.updateViewed(document.getId(), true);
            }
        });

    }

    private void createChat() {
        Chat chat = new Chat();
        chat.setIdUser1(mExtraIdUser1);
        chat.setIdUser2(mExtraIdUser2);
        chat.setWriting(false);
        chat.setTimestamp(new Date().getTime());
        chat.setId(mExtraIdUser1 + mExtraIdUser2);
        Random random = new Random();
        int n = random.nextInt(1000000);
        chat.setIdNotification(n);
        mIdNotificationChat = n;

        ArrayList<String> ids = new ArrayList<>();
        ids.add(mExtraIdUser1);
        ids.add(mExtraIdUser2);
        chat.setIds(ids);
        mChatsProvider.create(chat);
        mExtraIdChat = chat.getId();
        getMessageChat();
    }

    private void getToken(final Message message) {
        String idUser;
        if (mAuthProvider.getUid().equals(mExtraIdUser1)) {
            idUser = mExtraIdUser2;
        }
        else {
            idUser = mExtraIdUser1;
        }
        mTokenProvider.getToken(idUser).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("token")) {
                    String token = documentSnapshot.getString("token");
                    getLastThreeMessages(message, token);
                }
            }
            else {
                Toast.makeText(ChatActivity.this, "Kullanıcı talepleri belirteci mevcut değil", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLastThreeMessages(final Message message, final String token) {
        mMessagesProvider.getLastThreeMessagesByChatAndSender(mExtraIdChat, mAuthProvider.getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            ArrayList<Message> messageArrayList = new ArrayList<>();

            for (DocumentSnapshot d: queryDocumentSnapshots.getDocuments()) {
                if (d.exists()) {
                    Message message1 = d.toObject(Message.class);
                    messageArrayList.add(message1);
                }
            }

            if (messageArrayList.size() == 0) {
                messageArrayList.add(message);
            }

            Collections.reverse(messageArrayList);

            Gson gson = new Gson();
            String messages = gson.toJson(messageArrayList);

            sendNotification(token, messages, message);
        });
    }

    private void sendNotification(final String token, String messages, Message message) {
        final Map<String, String> data = new HashMap<>();
        data.put("title", "YENİ MESAJ");
        data.put("body", message.getMessage());
        data.put("idNotification", String.valueOf(mIdNotificationChat));
        data.put("messages", messages);
        data.put("usernameSender", mMyUsername.toUpperCase());
        data.put("usernameReceiver", mUsernameChat.toUpperCase());
        data.put("idSender", message.getIdSender());
        data.put("idReceiver", message.getIdReceiver());
        data.put("idChat", message.getIdChat());

        if (mImageSender.equals("")) {
            mImageSender = "IMAGEN_NO_VALIDA";
        }
        if (mImageReceiver.equals("")) {
            mImageReceiver = "IMAGEN_NO_VALIDA";
        }

        data.put("imageSender", mImageSender);
        data.put("imageReceiver", mImageReceiver);

        String idSender;
        if (mAuthProvider.getUid().equals(mExtraIdUser1)) {
            idSender = mExtraIdUser2;
        }
        else {
            idSender = mExtraIdUser1;
        }

        mMessagesProvider.getLastMessageSender(mExtraIdChat, idSender).get().addOnSuccessListener(queryDocumentSnapshots -> {
            int size = queryDocumentSnapshots.size();
            String lastMessage;
            if (size > 0) {
                lastMessage = queryDocumentSnapshots.getDocuments().get(0).getString("message");
                data.put("lastMessage", lastMessage);
            }
            FCMBody body = new FCMBody(token, "high", "4500s", data);
            mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                @Override
                public void onResponse(@NonNull Call<FCMResponse> call, Response<FCMResponse> response) {
                    if (response.body() != null) {
                        if (response.body().getSuccess() == 1) {
                            //Toast.makeText(ChatActivity.this, "Bildirim başarıyla gönderildi", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChatActivity.this, "Bildirim gönderilemedi", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(ChatActivity.this, "Bildirim gönderilemedi", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<FCMResponse> call, Throwable t) {

                }
            });
        });

    }


    private void getMyInfoUser() {
        mUsersProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("username")) {
                    mMyUsername = documentSnapshot.getString("username");
                }
                if (documentSnapshot.contains("image")) {
                    mImageSender = documentSnapshot.getString("image");
                }
            }
        });
    }

}
