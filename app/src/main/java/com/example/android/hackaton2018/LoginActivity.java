package com.example.android.hackaton2018;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.TextInputLayout;

import com.nexmo.sdk.conversation.client.Conversation;
import com.nexmo.sdk.conversation.client.ConversationClient;
import com.nexmo.sdk.conversation.client.Event;
import com.nexmo.sdk.conversation.client.Member;
import com.nexmo.sdk.conversation.client.User;
import com.nexmo.sdk.conversation.client.event.NexmoAPIError;
import com.nexmo.sdk.conversation.client.event.RequestHandler;
import com.nexmo.sdk.conversation.client.event.ResultListener;
import com.nexmo.sdk.conversation.client.event.container.Invitation;
import com.nexmo.sdk.conversation.client.event.container.SynchronisingState;
import com.nexmo.sdk.conversation.client.event.network.NetworkState;
import com.nexmo.sdk.conversation.client.event.network.NetworkingStateListener;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = LoginActivity.class.getSimpleName();
    private String CONVERSATION_ID = "CON-76f7d1da-d99c-41d2-b77b-2ace6230e20f";
    private String USER_JWT_FIRST_USER = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE1Mjg4ODM3MTIsImp0aSI6ImRkYTE4N2IwLTZlZWYtMTFlOC05MTNjLTRiM2IzYzVlYTNmYiIsInN1YiI6InN2ZXRsYW5hIiwiZXhwIjoiMTUyODk3MDExMiIsImFjbCI6eyJwYXRocyI6eyIvdjEvc2Vzc2lvbnMvKioiOnt9LCIvdjEvdXNlcnMvKioiOnt9LCIvdjEvY29udmVyc2F0aW9ucy8qKiI6e319fSwiYXBwbGljYXRpb25faWQiOiJmMjQxOGJjMi1iMzNhLTRmNDItODhhMi04ODU5ODU3NjhhMTAifQ.oo5PS1GbGVDhnwLnh1NqeAT4fecFIKrRF_6eC3GMooBe65fCrtTJxw8B-suSogV0VnVTlbRDlCn4QVYaK0ac_a4h1J2lq5LyLH2Lm7iC-CSwqKoIr0Rlm7AUGVYVYbztp8-6jz7P5EC1vlScbRNgD6IO-yrFoEdGkuNLRU3zcU2uI4AF5KC2ZX4MOHIvWT4KBTec2yr8u6IkzFi1kXYObhf9JyNnyAbIQ1APP86ikI2a8p0q4rkVS9A9zyUkTozmf_aSu-cjEGKQlBcPxmkZdo7e2ocuwAgjcbGrP427tMFXE0YvAvJVbZSluQ0mX54AnMd4qS8L72jl6sDzCaIhkw";
    private String USER_JWT_SECOND_USER = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE1Mjg4ODM3MTAsImp0aSI6ImRiZTU1OGMwLTZlZWYtMTFlOC05MGViLTg3YzY0NjNmZDg5OCIsInN1YiI6ImphbWllIiwiZXhwIjoiMTUyODk3MDEwOSIsImFjbCI6eyJwYXRocyI6eyIvdjEvc2Vzc2lvbnMvKioiOnt9LCIvdjEvdXNlcnMvKioiOnt9LCIvdjEvY29udmVyc2F0aW9ucy8qKiI6e319fSwiYXBwbGljYXRpb25faWQiOiJmMjQxOGJjMi1iMzNhLTRmNDItODhhMi04ODU5ODU3NjhhMTAifQ.H7ZjcNh9KGHMn4TQptSjZERLTDVzpUdFoOzWAtLJIvwWUs03_Vvqci5hbP8v2AlHhtFNapLi3-VItFwKdvo_ocr_xYg3BqDbTT-8Bjif_rcLXqQpgdl8k01KUX0luGKJA3fCgIrT2YlhM_YmxhFuqyZcchDU_svvgQA36xRHVtg1brI7DdfGVI1yXLVF4DtGWvMWxtc17woxLdl7U5NsbGsDOItlgNR-VoEITIIR5aswHKPZijZJPTF_s_mUtDBNG9CuQnZTAEZx_Voz4OLyE9m60JsFCLp0rvCY-WfEiUgv6i2ww1s4TEYkoNIk8toTNL7xG-T9NnRX9T9-nQ1Z5g";

    private ConversationClient conversationClient;
    private String username;
    private TextView loginTxt;
    private Button loginBtn;
    private ProgressDialog progressDialog;
    //ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        actionBar = getSupportActionBar();
//        actionBar.hide();

        ConversationClientApplication application = (ConversationClientApplication) getApplication();
        conversationClient = application.getConversationClient();

        final TextInputLayout usernameWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);
        final TextInputLayout passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);

        usernameWrapper.setHint("Username");
        passwordWrapper.setHint("Password");

       // loginTxt = (TextView) findViewById(R.id.login_text);
        loginBtn = (Button) findViewById(R.id.login);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private String authenticate(String username) {
        System.out.println("username inserted: " + username);
        return username.toLowerCase().equals("svetlana") ? USER_JWT_FIRST_USER : USER_JWT_SECOND_USER;
    }

    private void login() {
        TextInputLayout usernameWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);
        username = usernameWrapper.getEditText().getText().toString();
        String userToken = authenticate(username);
        System.out.println("userToken: " + userToken);

        progressDialog = new ProgressDialog(new ContextThemeWrapper(LoginActivity.this, android.R.style.Theme_Holo_Light_Dialog));
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        System.out.println("authentificating");
        progressDialog.getWindow().setGravity(Gravity.CENTER);
        progressDialog.show();
        System.out.println("loginAsUser starting...");
        loginAsUser(userToken);

    }

    private void loginAsUser(String token) {
        System.out.println("loginAsUser started...");
        System.out.println("token: " + token);
        System.out.println("login starting...");

        conversationClient.synchronisationEvent().add(new ResultListener<SynchronisingState.STATE>() {
            @Override
            public void onSuccess(SynchronisingState.STATE result) {
                System.out.println("onSuccess state when retrieving conversation...");
                if (result == SynchronisingState.STATE.MEMBERS) {
                    System.out.println("result == SynchronisingState.STATE.MEMBERS");

                    List<Conversation> conversationList = conversationClient.getConversationList();
                    System.out.println("conversationList: " + conversationList.toString());
                    if (conversationList.size() > 0) {
                        progressDialog.dismiss();
                        System.out.println("conv list size: " + conversationList.size());
                        showConversationList(conversationList);
                    } else {
                        logAndShow("You are not a member of any conversations");
                    }
                }
            }
        });

        conversationClient.login(token, new RequestHandler<User>() {
            @Override
            public void onSuccess(User user) {
                System.out.println("login success...");
                showLoginSuccessAndAddInvitationListener(user);
                System.out.println("retrieving the conversation");
                retrieveConversations();
            }

            @Override
            public void onError(NexmoAPIError apiError) {
                logAndShow("Login Error: " + apiError.getMessage());
            }
        });
    }


    private void retrieveConversations() {
        System.out.println("retrieve conversations starting...");

        conversationClient.listenToConnectionEvents(new NetworkingStateListener() {
            @Override
            public void onNetworkingState(NetworkState networkingState) {
                System.out.println("network state " + networkingState.toString());
            }
        });
    }



    private void showConversationList(final List<Conversation> conversationList) {
        List<String> conversationNames = new ArrayList<>(conversationList.size());
        for (Conversation convo : conversationList) {
            conversationNames.add(convo.getDisplayName());
        }

        final AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this)
                .setTitle("Choose a conversation")
                .setItems(conversationNames.toArray(new CharSequence[conversationNames.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goToConversation(conversationList.get(which));

                        dialog.dismiss();
                    }
                });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });
    }

    private void goToConversation(final Conversation conversation) {
        System.out.println("conversation " + conversation.getEvents().toString());
        conversation.sendText("Hey there! You can chat now.", new RequestHandler<Event>() {
            @Override
            public void onError(NexmoAPIError apiError) {
                logAndShow("Error going to the Conversation: " + apiError.getMessage());
                System.out.println("Error going to the Conversation: " + apiError.toString());
            }

            @Override
            public void onSuccess(Event result) {
                conversation.updateEvents(null, null, new RequestHandler<Conversation>() {
                    @Override
                    public void onError(NexmoAPIError apiError) {
                        logAndShow("Error Updating Conversation: " + apiError.getMessage());
                        System.out.println("Error Updating Conversation: " + apiError.toString());
                    }

                    @Override
                    public void onSuccess(final Conversation result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
                                intent.putExtra("CONVERSATION-ID", conversation.getConversationId());
                                intent.putExtra("USERNAME", username);
                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        });

    }

    private void showLoginSuccessAndAddInvitationListener(final User user) {
        conversationClient.invitedEvent().add(new ResultListener<Invitation>() {
            @Override
            public void onSuccess(Invitation result) {
                logAndShow(result.getInvitedBy() + " invited you to their chat");
                result.getConversation().join(new RequestHandler<Member>() {
                    @Override
                    public void onSuccess(Member result) {
                        goToConversation(result.getConversation());
                    }

                    @Override
                    public void onError(NexmoAPIError apiError) {
                        logAndShow("Error joining conversation: " + apiError.getMessage());
                    }
                });
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //loginTxt.setText("Logged in as " + user.getName() + "\nStart chatting!");
            }
        });
    }

    private void logAndShow(final String message) {
       // Log.d(TAG, message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
