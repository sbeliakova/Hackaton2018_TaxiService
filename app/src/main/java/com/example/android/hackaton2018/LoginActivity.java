package com.example.android.hackaton2018;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.TextInputLayout;

import com.nexmo.sdk.conversation.client.Conversation;
import com.nexmo.sdk.conversation.client.ConversationClient;
import com.nexmo.sdk.conversation.client.Member;
import com.nexmo.sdk.conversation.client.User;
import com.nexmo.sdk.conversation.client.event.NexmoAPIError;
import com.nexmo.sdk.conversation.client.event.RequestHandler;
import com.nexmo.sdk.conversation.client.event.ResultListener;
import com.nexmo.sdk.conversation.client.event.container.Invitation;
import com.nexmo.sdk.conversation.client.event.container.SynchronisingState;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = LoginActivity.class.getSimpleName();
    private String CONVERSATION_ID = "CON-76f7d1da-d99c-41d2-b77b-2ace6230e20f";
    private String USER_JWT_FIRST_USER = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE1Mjg3OTcyMDYsImp0aSI6IjczZmIxZWQwLTZlMjYtMTFlOC05NjFhLTYzNDkxOGMyZGE1MyIsInN1YiI6InN2ZXRsYW5hIiwiZXhwIjoiMTUyODg4MzYwNiIsImFjbCI6eyJwYXRocyI6eyIvdjEvc2Vzc2lvbnMvKioiOnt9LCIvdjEvdXNlcnMvKioiOnt9LCIvdjEvY29udmVyc2F0aW9ucy8qKiI6e319fSwiYXBwbGljYXRpb25faWQiOiJmMjQxOGJjMi1iMzNhLTRmNDItODhhMi04ODU5ODU3NjhhMTAifQ.t9kcgM910cCNPo8qjUbbDL2NMTFNb1M8PgOwvksmSPsVrbQu0SyrdSD-kKJRddg9zRIl_ghHUa9VKFsrsI_dpkRaPScUHQEXbVkhi1huUD-4AIK8qenno6TPTxV_2YnArZSThFdPH5475WCnUnYYX3aBeOETiMgjSlw8rDNmAQC9A0NAjg6-tFYLvjQMJe5D4zRM6nOwBtmOi4O1Pa_b57H_1IRLPejC5OMiBNWoDTVwyMCv2XEiJV7RsgR7qaYpnRd0KaZnydV9ZXxNGaj1NgVQ0xGLiviE9qDn30OhplAG2yod1UpR4EvoEC9T-y9358ACPXdrpGFQ1Del9X4iog";
    private String USER_JWT_SECOND_USER = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE1Mjg3OTcxODksImp0aSI6IjY5OTlmNDcwLTZlMjYtMTFlOC05NjkyLWQxYTM2YjY5OGFhMCIsInN1YiI6ImphbWllIiwiZXhwIjoiMTUyODg4MzU4OCIsImFjbCI6eyJwYXRocyI6eyIvdjEvc2Vzc2lvbnMvKioiOnt9LCIvdjEvdXNlcnMvKioiOnt9LCIvdjEvY29udmVyc2F0aW9ucy8qKiI6e319fSwiYXBwbGljYXRpb25faWQiOiJmMjQxOGJjMi1iMzNhLTRmNDItODhhMi04ODU5ODU3NjhhMTAifQ.d1INXg8q_4MhNR8GMhwo_d_5HoBNuO7fL4THTQWmAFoUrB6_eNzj8jlju3LTM1AUkrxYyOQCLTz9a1v4MKmgvRPiEWYtwEIlFaYyktMTZfiXap6Hhf0WR3OjYJT1_DI1U9DAaIfwe5oXqHq_IB-CDrhy_Qkob-MdCQaS0LJaMzSFlDY0Kw2iX8u2e74QWXKAEf0kCyRCNtrRidaUk-fqKaO1DEajQDeRCmo-T75zBNttzlyMRryDdfv3fLIChAepM9I1fSp8n9fQL2jpY6YrECfHVMCYy_H1_aaVhiISULpXBxlKW13S3gVC__Y92i4piiZfksD76WjMrqy6u746bQ";

    private ConversationClient conversationClient;
    private String username;
    private TextView loginTxt;
    private Button loginBtn;
 //   private Button chatBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ConversationClientApplication application = (ConversationClientApplication) getApplication();
        conversationClient = application.getConversationClient();

        final TextInputLayout usernameWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);
        final TextInputLayout passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);

        usernameWrapper.setHint("Username");
        passwordWrapper.setHint("Password");

        loginTxt = (TextView) findViewById(R.id.login_text);
        loginBtn = (Button) findViewById(R.id.login);
   //     chatBtn = (Button) findViewById(R.id.chat);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
//        chatBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                retrieveConversations();
//            }
//        });
    }

    private String authenticate(String username) {
        return username.toLowerCase().equals("svetlana") ? USER_JWT_FIRST_USER : USER_JWT_SECOND_USER;
    }

//    private void login() {
//        loginTxt.setText("Logging in...");
//
//        String userToken = authenticate();
//        conversationClient.login(userToken, new RequestHandler<User>() {
//            @Override
//            public void onSuccess(User user) {
//                showLoginSuccess(user);
//
//                conversationClient.synchronisationEvent().add(new ResultListener<SynchronisingState.STATE>() {
//                    @Override
//                    public void onSuccess(SynchronisingState.STATE result) {
//                      if ( result == SynchronisingState.STATE.MEMBERS) {
//                          logAndShow("Synchronization done");
//                          runOnUiThread(new Runnable() {
//                              @Override
//                              public void run() {
//                                  chatBtn.setVisibility(View.VISIBLE);
//                              }
//                          });
//                      }
//                    }
//                });
//            }
//
//            @Override
//            public void onError(NexmoAPIError apiError) {
//                logAndShow("Login Error: " + apiError.getMessage());
//            }
//        });
//    }

    private void login() {
//        final EditText input = new EditText(LoginActivity.this);
//        final AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this)
//                .setTitle("Enter your username")
//                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String userToken = authenticate(input.getText().toString());
//                        System.out.println("conversationList size in the login function: " +
//                                conversationClient.getConversationList().size());
//                        loginAsUser(userToken);
//                    }
//                });
        TextInputLayout usernameWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);
        username = usernameWrapper.getEditText().getText().toString();
        String userToken = authenticate(username);
        loginAsUser(userToken);

//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//        input.setLayoutParams(lp);
//        dialog.setView(input);
//        dialog.show();
    }

    private void loginAsUser(String token) {
        conversationClient.login(token, new RequestHandler<User>() {
            @Override
            public void onSuccess(User user) {
                showLoginSuccessAndAddInvitationListener(user);
                retrieveConversations();
            }

            @Override
            public void onError(NexmoAPIError apiError) {
                logAndShow("Login Error: " + apiError.getMessage());
            }
        });
    }

    private void retrieveConversations() {
        conversationClient.synchronisationEvent().add(new ResultListener<SynchronisingState.STATE>() {
            @Override
            public void onSuccess(SynchronisingState.STATE result) {
                if (result == SynchronisingState.STATE.MEMBERS) {
                    System.out.println(SynchronisingState.STATE.MEMBERS.toString());
                    conversationClient.getConversations(new RequestHandler<List<Conversation>>() {
                        @Override
                        public void onSuccess(List<Conversation> conversationList) {
                            if (conversationList.size() > 0) {
                                System.out.println("conv list size: " + conversationList.size());
                                showConversationList(conversationList);
                            } else {
                                logAndShow("You are not a member of any conversations");
                            }
                        }

                        @Override
                        public void onError(NexmoAPIError apiError) {
                            logAndShow("Error listing conversations: " + apiError.getMessage());
                        }
                    });
                }
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
                    }
                });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });
    }

//    private void goToConversation(final Conversation conversation) {
//        Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
//        intent.putExtra("CONVERSATION-ID", conversation.getConversationId());
//        intent.putExtra("USERNAME", username);
//        startActivity(intent);
//    }

    private void goToConversation(final Conversation conversation) {
        conversation.updateEvents(null, null, new RequestHandler<Conversation>() {
            @Override
            public void onError(NexmoAPIError apiError) {
                logAndShow("Error Updating Conversation: " + apiError.getMessage());
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
                loginTxt.setText("Logged in as " + user.getName() + "\nStart chatting!");
            }
        });
    }

    private void logAndShow(final String message) {
        Log.d(TAG, message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
