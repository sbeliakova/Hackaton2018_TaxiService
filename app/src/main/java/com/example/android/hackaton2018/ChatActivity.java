package com.example.android.hackaton2018;

/**
 * Created by sbeliakova on 12/06/2018.
 */

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.nexmo.sdk.conversation.client.Conversation;
import com.nexmo.sdk.conversation.client.ConversationClient;
import com.nexmo.sdk.conversation.client.Event;
import com.nexmo.sdk.conversation.client.Member;
import com.nexmo.sdk.conversation.client.Text;
import com.nexmo.sdk.conversation.client.audio.AppRTCAudioManager;
import com.nexmo.sdk.conversation.client.audio.AudioCallEventListener;
import com.nexmo.sdk.conversation.client.event.EventType;
import com.nexmo.sdk.conversation.client.event.NexmoAPIError;
import com.nexmo.sdk.conversation.client.event.RequestHandler;
import com.nexmo.sdk.conversation.client.event.ResultListener;
import com.nexmo.sdk.conversation.core.SubscriptionList;
import static android.Manifest.permission.RECORD_AUDIO;


public class ChatActivity extends AppCompatActivity {
    private final String TAG = ChatActivity.this.getClass().getSimpleName();
    private static final int PERMISSION_REQUEST_AUDIO = 0;
    private boolean AUDIO_ENABLED = false;

    private EditText chatBox;
    private ImageButton sendBtn;
    private TextView typingNotificationTxt;
    private TextView chatTxt;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;

    private ConversationClient conversationClient;
    private Conversation conversation;
    private SubscriptionList subscriptions = new SubscriptionList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ConversationClientApplication application = (ConversationClientApplication) getApplication();
        conversationClient = application.getConversationClient();

        Intent intent = getIntent();
        String conversationId = intent.getStringExtra("CONVERSATION-ID");
        System.out.println("conversationId: " + conversationId);
        conversation = conversationClient.getConversation(conversationId);

        chatTxt = (TextView) findViewById(R.id.chat_txt);
        chatBox = (EditText) findViewById(R.id.chat_box);
        sendBtn = (ImageButton) findViewById(R.id.send_btn);
        typingNotificationTxt = (TextView) findViewById(R.id.typing_notification);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        chatAdapter = new ChatAdapter(conversation);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        final String username = intent.getStringExtra("USERNAME");

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("onClickListener reacts and sending message");
                System.out.println("username used: " + username);
                sendMessage(username);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        attachListeners();
       //addListener();
    }

    private void attachListeners() {
        conversation.messageEvent().add(new ResultListener<Event>() {
            @Override
            public void onSuccess(Event result) {
                chatAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(chatAdapter.getItemCount());
            }
        }).addTo(subscriptions);

        chatBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //intentionally left blank
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    sendTypeIndicator(Member.TYPING_INDICATOR.ON);
                } else {
                    sendTypeIndicator(Member.TYPING_INDICATOR.OFF);
                }
            }
        });
    }

    private void sendTypeIndicator(Member.TYPING_INDICATOR typingIndicator) {
        switch (typingIndicator){
            case ON: {
                conversation.startTyping(new RequestHandler<Member.TYPING_INDICATOR>() {
                    @Override
                    public void onSuccess(Member.TYPING_INDICATOR typingIndicator) {
                        //intentionally left blank
                    }

                    @Override
                    public void onError(NexmoAPIError apiError) {
                        logAndShow("Error start typing: " + apiError.getMessage());
                    }
                });
                break;
            }
            case OFF: {
                conversation.stopTyping(new RequestHandler<Member.TYPING_INDICATOR>() {
                    @Override
                    public void onSuccess(Member.TYPING_INDICATOR typingIndicator) {
                        //intentionally left blank
                    }

                    @Override
                    public void onError(NexmoAPIError apiError) {
                        logAndShow("Error stop typing: " + apiError.getMessage());
                    }
                });
                break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        subscriptions.unsubscribeAll();
    }
//
//    private void sendMessage(String username) {
//        conversation.sendText(username + ": " + chatBox.getText().toString(),
//                new RequestHandler<Event>() {
//            @Override
//            public void onSuccess(Event event) {
//                if (event.getType().equals(EventType.TEXT)) {
//                    Log.d(TAG, "onSent: " + ((Text) event).getText());
//                }
//            }
//
//            @Override
//            public void onError(NexmoAPIError apiError) {
//                logAndShow("Error sending message: " + apiError.getMessage());
//            }
//        });
//    }

    private void sendMessage(String username) {
        System.out.println("starting sendMessage...");
        conversation.sendText(username + ": " + chatBox.getText().toString(), new RequestHandler<Event>() {
            @Override
            public void onError(NexmoAPIError apiError) {
                logAndShow("Error sending message: " + apiError.getMessage());
            }

            @Override
            public void onSuccess(Event result) {
                System.out.println("onSuccess");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatBox.setText(null);
                    }
                });
            }
        });
    }


    private void addListener() {
        conversation.messageEvent().add(new ResultListener<Event>() {
            @Override
            public void onSuccess(Event message) {
                showMessage(message);
            }
        }).addTo(subscriptions);
    }

    private void showMessage(final Event message) {
        if (message.getType().equals(EventType.TEXT)) {
            Text text = (Text) message;
            chatBox.setText(null);
            final String prevText = chatTxt.getText().toString();
            chatTxt.setText(prevText + "\n" + text.getText());
        }
    }

    private void logAndShow(final String message) {
        Log.d(TAG, message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ChatActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
