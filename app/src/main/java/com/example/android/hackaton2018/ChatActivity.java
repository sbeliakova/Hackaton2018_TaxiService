package com.example.android.hackaton2018;

/**
 * Created by sbeliakova on 12/06/2018.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.nexmo.sdk.conversation.client.Call;
import com.nexmo.sdk.conversation.client.Image;
import com.nexmo.sdk.conversation.client.SeenReceipt;
import com.nexmo.sdk.conversation.client.event.container.Receipt;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

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
    private String supportPstn;
    private Call currentCall;
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

        supportPstn = intent.getStringExtra("SUPPORT_PSTN");

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
                sendMessage();
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

        conversation.typingEvent().add(new ResultListener<Member>() {
            @Override
            public void onSuccess(final Member member) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String typingMsg = member.getTypingIndicator().equals(Member.TYPING_INDICATOR.ON) ? member.getName() + " is typing" : null;
                        typingNotificationTxt.setText(typingMsg);
                    }
                });
            }
        }).addTo(subscriptions);

        conversation.seenEvent().add(new ResultListener<Receipt<SeenReceipt>>() {
            @Override
            public void onSuccess(Receipt<SeenReceipt> result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).addTo(subscriptions);
    }




    private void sendTypeIndicator(Member.TYPING_INDICATOR typingIndicator) {
        switch (typingIndicator){
            case ON: {
                conversation.startTyping(new RequestHandler<Member.TYPING_INDICATOR>() {
                    @Override
                    public void onSuccess(Member.TYPING_INDICATOR typingIndicator) {
                        //typingNotificationTxt.setVisibility(View.VISIBLE);


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
                 //       typingNotificationTxt.setVisibility(View.GONE);
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


    private void sendMessage() {
        System.out.println("starting sendMessage...");
        conversation.sendText(chatBox.getText().toString(), new RequestHandler<Event>() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.audio:
                //TODO: implement
                requestAudio();
                return true;
            case R.id.outbound_pstn:
                System.out.println("calling the PSTN...");
                callPhone();
                System.out.println("callPhone function called");
                return true;
            case R.id.image:
                System.out.println("sending Image");
               // sendImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
//
//    private void sendImage() {
//
//
//        conversation.sendImage("imagePath", new ImageSendListener() {
//            @Override
//            public void onSuccess(Event image) {
//            }
//
//            @Override
//            public void onError(NexmoAPIError error) {
//            }
//        });
//    }

    private void callPhone() {
        System.out.println("callPhone");
        System.out.println("supportPstn: " + supportPstn);
        conversationClient.callPhone(supportPstn, new RequestHandler<Call>() {
            @Override
            public void onSuccess(Call result) {
                System.out.println("callPhone OnSuccess");
                currentCall = result;
                System.out.println("currentCall: " + currentCall.toString());
                System.out.println("opening a dialog window");
                final AlertDialog dialogPSTN = new AlertDialog.Builder(ChatActivity.this).create();
                dialogPSTN.setTitle("Call with Support");
                dialogPSTN.setIcon(R.drawable.ic_twotone_call_end_24px);
//                dialogPSTN.setButton(AlertDialog.BUTTON_NEUTRAL, "Disconnect the call",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialogInterface, int which) {
//                                endCall();
//                                dialogPSTN.dismiss();
//                            }
//                        });
                dialogPSTN.show();
                System.out.println("switch");
                switch (result.getCallState()) {
                    case STARTED:
                        logAndShow("PSTN call started");
                    case RINGING:
                        logAndShow("PSTN call ringing");
                    case ANSWERED:
                        logAndShow("PSTN call answered");
                    default:
                        logAndShow("Error attaching call listener");
                }
            }

            @Override
            public void onError(NexmoAPIError apiError) {
                logAndShow("Cannot initiate PSTN call: " + apiError.getMessage());
            }
        });


    }

    private void endCall() {
        currentCall.hangup(new RequestHandler<Void>() {
            @Override
            public void onError(NexmoAPIError apiError) {
                logAndShow("Cannot hangup: " + apiError.toString());
            }

            @Override
            public void onSuccess(Void result) {
                logAndShow("Call completed.");
            }
        });
    }

    private void requestAudio() {
        if (ContextCompat.checkSelfPermission(ChatActivity.this, RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            toggleAudio();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, RECORD_AUDIO)) {
                logAndShow("Need permissions granted for Audio to work");
            } else {
                ActivityCompat.requestPermissions(ChatActivity.this, new String[]{RECORD_AUDIO}, PERMISSION_REQUEST_AUDIO);
            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_AUDIO: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //TODO: implement
                    toggleAudio();
                    break;
                } else {
                    logAndShow("Enable audio permissions to continue");
                    break;
                }
            }
            default: {
                logAndShow("Issue with onRequestPermissionsResult");
                break;
            }
        }
    }

    private void toggleAudio() {
        if(AUDIO_ENABLED) {
           // conversation.audio().disable(new RequestHandler<Void>()
            conversation.media(Conversation.MEDIA_TYPE.AUDIO).disable(new RequestHandler<Void>()
            {
                @Override
                public void onError(NexmoAPIError apiError) {
                    logAndShow(apiError.getMessage());
                }

                @Override
                public void onSuccess(Void result) {
                    AUDIO_ENABLED = false;
                    logAndShow("Audio is disabled");
                }
            });
        } else {
           // conversation.audio().enable(new AudioCallEventListener()
            conversation.media(Conversation.MEDIA_TYPE.AUDIO).enable(new AudioCallEventListener()
            {
                @Override
                public void onRinging() {
                    logAndShow("Ringing");
                }

                @Override
                public void onCallConnected() {
                    logAndShow("Connected");
                    AUDIO_ENABLED = true;
                    final AlertDialog dialog = new AlertDialog.Builder(ChatActivity.this).create();
                    dialog.setTitle("Call");
                    dialog.setIcon(R.drawable.ic_twotone_call_end_24px);
                    dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Disconnect the call",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                  //  onCallEnded();
                                    requestAudio();

                                    dialog.dismiss();
                                }
                            });
                    dialog.show();
                }

                @Override
                public void onCallEnded() {
                    logAndShow("Call Ended");
                    AUDIO_ENABLED = false;
                }

                @Override
                public void onGeneralCallError(NexmoAPIError apiError) {
                    logAndShow(apiError.getMessage());
                    AUDIO_ENABLED = false;
                }

                @Override
                public void onAudioRouteChange(AppRTCAudioManager.AudioDevice device) {
                    logAndShow("Audio Route changed");
                }
            });
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
