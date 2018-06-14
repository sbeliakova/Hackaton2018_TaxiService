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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = LoginActivity.class.getSimpleName();
    private String CONVERSATION_ID;

    private String USER_JWT_FIRST_USER ;
    private String USER_JWT_SECOND_USER;
    private String SUPPORT_PSTN;

    private ConversationClient conversationClient;
    private String username;
    private TextView loginTxt;
    private Button loginBtn;
    private ProgressDialog progressDialog;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        actionBar = getSupportActionBar();
        actionBar.hide();

        ConversationClientApplication application = (ConversationClientApplication) getApplication();
        conversationClient = application.getConversationClient();

        final TextInputLayout usernameWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);

        usernameWrapper.setHint("Username");

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
        progressDialog.setMessage("Connecting...");
        System.out.println("connecting");
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
                        System.out.println("conv list size: " + conversationList.size());
                        //showConversationList(conversationList);
                        goToConversation(conversationList.get(0));
                        progressDialog.dismiss();
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
                .setTitle("Do you want to contact the driver?")

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
        conversation.sendText("logged in", new RequestHandler<Event>() {
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
                                intent.putExtra("SUPPORT_PSTN", SUPPORT_PSTN);
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
