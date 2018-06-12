package com.example.android.hackaton2018;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nexmo.sdk.conversation.client.Conversation;
import com.nexmo.sdk.conversation.client.ConversationClient;
import com.nexmo.sdk.conversation.client.User;
import com.nexmo.sdk.conversation.client.event.NexmoAPIError;
import com.nexmo.sdk.conversation.client.event.RequestHandler;
import com.nexmo.sdk.conversation.client.event.ResultListener;
import com.nexmo.sdk.conversation.client.event.container.SynchronisingState;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = LoginActivity.class.getSimpleName();
    private String CONVERSATION_ID = "CON-76f7d1da-d99c-41d2-b77b-2ace6230e20f";
    private String USER_JWT_SVETLANA = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE1Mjg3OTcyMDYsImp0aSI6IjczZmIxZWQwLTZlMjYtMTFlOC05NjFhLTYzNDkxOGMyZGE1MyIsInN1YiI6InN2ZXRsYW5hIiwiZXhwIjoiMTUyODg4MzYwNiIsImFjbCI6eyJwYXRocyI6eyIvdjEvc2Vzc2lvbnMvKioiOnt9LCIvdjEvdXNlcnMvKioiOnt9LCIvdjEvY29udmVyc2F0aW9ucy8qKiI6e319fSwiYXBwbGljYXRpb25faWQiOiJmMjQxOGJjMi1iMzNhLTRmNDItODhhMi04ODU5ODU3NjhhMTAifQ.t9kcgM910cCNPo8qjUbbDL2NMTFNb1M8PgOwvksmSPsVrbQu0SyrdSD-kKJRddg9zRIl_ghHUa9VKFsrsI_dpkRaPScUHQEXbVkhi1huUD-4AIK8qenno6TPTxV_2YnArZSThFdPH5475WCnUnYYX3aBeOETiMgjSlw8rDNmAQC9A0NAjg6-tFYLvjQMJe5D4zRM6nOwBtmOi4O1Pa_b57H_1IRLPejC5OMiBNWoDTVwyMCv2XEiJV7RsgR7qaYpnRd0KaZnydV9ZXxNGaj1NgVQ0xGLiviE9qDn30OhplAG2yod1UpR4EvoEC9T-y9358ACPXdrpGFQ1Del9X4iog";

    private ConversationClient conversationClient;
    private TextView loginTxt;
    private Button loginBtn;
    private Button chatBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ConversationClientApplication application = (ConversationClientApplication) getApplication();
        conversationClient = application.getConversationClient();

        loginTxt = (TextView) findViewById(R.id.login_text);
        loginBtn = (Button) findViewById(R.id.login);
        chatBtn = (Button) findViewById(R.id.chat);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChatActivity();
            }
        });
    }

    private String authenticate() {
        return USER_JWT_SVETLANA;
    }

    private void login() {
        loginTxt.setText("Logging in...");

        String userToken = authenticate();
        conversationClient.login(userToken, new RequestHandler<User>() {
            @Override
            public void onSuccess(User user) {
                showLoginSuccess(user);

                conversationClient.synchronisationEvent().add(new ResultListener<SynchronisingState.STATE>() {
                    @Override
                    public void onSuccess(SynchronisingState.STATE result) {
                      if ( result == SynchronisingState.STATE.MEMBERS) {
                          logAndShow("Synchronization done");
                          runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  chatBtn.setVisibility(View.VISIBLE);
                              }
                          });
                      }
                    }
                });
            }

            @Override
            public void onError(NexmoAPIError apiError) {
                logAndShow("Login Error: " + apiError.getMessage());
            }
        });
    }

    private void showLoginSuccess(final User user) {

    runOnUiThread(new Runnable() {
        @Override
        public void run() {
            loginTxt.setText("Logged in as " + user.getName() + "\nGo to a conversation!");
        }
    });
    }

    private void goToChatActivity() {
        logAndShow("going to Chat");
        final Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
        logAndShow("passed Intent");
        intent.putExtra("CONVERSATION-ID", CONVERSATION_ID);
        logAndShow("passed putExtra");
        startActivity(intent);
        logAndShow("Start activity");
    }

    private void logAndShow(final String message) {
        Log.d(TAG, message);
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}