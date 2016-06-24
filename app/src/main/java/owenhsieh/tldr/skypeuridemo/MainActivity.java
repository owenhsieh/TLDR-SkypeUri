package owenhsieh.tldr.skypeuridemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView topicTextView;
    private TextView accountTextView;

    private enum UriType{
        Chat,
        Call,
        Video
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topicTextView = (TextView) findViewById(R.id.topic);
        accountTextView = (TextView) findViewById(R.id.account);

        View sendMessage = findViewById(R.id.send_message);

        if (sendMessage != null) {
            sendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String skypeUri = makeSkypeUri(UriType.Chat, accountTextView.getText().toString(), getTopic());
                    initiateSkypeUri(getApplicationContext(), skypeUri);
                }
            });
        }
        View voiceChat = findViewById(R.id.voice_chat);

        if (voiceChat != null) {
            voiceChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String skypeUri = makeSkypeUri(UriType.Call, accountTextView.getText().toString(), getTopic());
                    initiateSkypeUri(getApplicationContext(), skypeUri);
                }
            });
        }

        View videoChat = findViewById(R.id.video_chat);
        if (videoChat != null) {
            videoChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String skypeUri = makeSkypeUri(UriType.Video, accountTextView.getText().toString(), getTopic());
                    initiateSkypeUri(getApplicationContext(), skypeUri);
                }
            });
        }
    }

    /**
     * Initiate the actions encoded in the specified URI.
     */
    public void initiateSkypeUri(Context myContext, String mySkypeUri) {
        // Make sure the Skype for Android client is installed.
        if (!isSkypeClientInstalled(myContext)) {
            goToMarket(myContext);
            return;
        }

        // Create the Intent from our Skype URI.
        Uri skypeUri = Uri.parse(mySkypeUri);
        Intent myIntent = new Intent(Intent.ACTION_VIEW, skypeUri);

        // Restrict the Intent to being handled by the Skype for Android client only.
        myIntent.setComponent(new ComponentName("com.skype.raider", "com.skype.raider.Main"));
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Initiate the Intent. It should never fail because you've already established the
        // presence of its handler (although there is an extremely minute window where that
        // handler can go away).
        myContext.startActivity(myIntent);
    }

    /**
     * Determine whether the Skype for Android client is installed on this device.
     */
    public boolean isSkypeClientInstalled(Context myContext) {
        PackageManager myPackageMgr = myContext.getPackageManager();
        try {
            myPackageMgr.getPackageInfo("com.skype.raider", PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * Install the Skype client through the market: URI scheme.
     */
    public void goToMarket(Context myContext) {
        Uri marketUri = Uri.parse("market://details?id=com.skype.raider");
        Intent myIntent = new Intent(Intent.ACTION_VIEW, marketUri);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myContext.startActivity(myIntent);
    }

    private String getTopic(){
        /** should use
         *
         * return URLEncoder.encode({topic}, "UTF-8").replaceAll("\\+", "%20")
         *
         * But right now skype automatically change ' ' into '_'
         * So so return {topic} would work as fine.
         * */
        return topicTextView.getText().toString();
    }

    private String makeSkypeUri(UriType uriType, String accountNames, String topic){
        String type = "";
        if( uriType == UriType.Chat){
            type = "chat";
        }else if( uriType == UriType.Call){
            type = "call";
        }else if( uriType == UriType.Video){
            type = "call&video=true";
        }

        String skypeUri = String.format("skype:%1s?%2s", accountNames, type);
        if(topic.length() > 0){
            skypeUri = skypeUri + "&topic=" + topic;
        }
        return skypeUri;
    }
}