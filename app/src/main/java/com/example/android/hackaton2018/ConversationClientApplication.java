package com.example.android.hackaton2018;

/**
 * Created by sbeliakova on 11/06/2018.
 */


import android.app.Application;
import com.nexmo.sdk.conversation.client.ConversationClient;


public class ConversationClientApplication extends Application {
    private ConversationClient conversationClient;

    @Override
    public void onCreate() {
        super.onCreate();
        this.conversationClient = new ConversationClient.ConversationClientBuilder().context(this).build();
    }

    public ConversationClient getConversationClientApp() {
        return this.conversationClient;
    }
}