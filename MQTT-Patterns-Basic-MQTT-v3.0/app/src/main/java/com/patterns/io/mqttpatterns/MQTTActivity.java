package com.patterns.io.mqttpatterns;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.patterns.io.mqttpatterns.MQTTConnectFragment.ConnectDataPassListener;
import com.patterns.io.mqttpatterns.MQTTPublishFragment.PublishDataPassListener;
import com.patterns.io.mqttpatterns.MQTTSubscribeFragment.SubscribeDataPassListener;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.InputStream;

public class MQTTActivity extends AppCompatActivity implements ConnectDataPassListener, PublishDataPassListener, MqttCallback, SubscribeDataPassListener  {

    private static final int PICKFILE_RESULT_CODE = 1;

    public String                   topicToPublish;
    public String                   topicToSubscribe;
    public String                   content;
    public String                   MQTTmessage;
    public String                   broker;
    public String                   port;
    public String                   clientId;
    public String                   messages[] = {"","","","",""};
    public String                   brokerURI;

    public int                      qos = 0;

    public FragmentManager          fragmentManager;
    public FragmentTransaction      fragmentTransaction;

    public MqttClient               client;
    public MqttConnectOptions       options;

    public MQTTConnectFragment      connectFragment;
    public MQTTSubscribeFragment    subscribeFragment;
    public MQTTPublishFragment      publishFragment;

    public Uri                      pathUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState );

        // Set the layout and the toolbar
        setContentView(R.layout.detail_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Fragment manager later used to work with the changing fragments
        fragmentManager     = getSupportFragmentManager();

        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first fragment
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            //Set the title to display on the toolbar
            setTitle(getString(R.string.mqtt_connect));

            // Create an instance of MQTTConnectFragment
            MQTTConnectFragment connectFragment = new MQTTConnectFragment();

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            connectFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, connectFragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // Interface to launch an MQTTPublishFragment fragment
    public void launchPublishFragment(String data) {

        // Set the title in the toolbar
        setTitle(getString(R.string.mqtt_publish));

        // Create instance of MQTTPublishFragment
        publishFragment  = new MQTTPublishFragment();

        Bundle args = new Bundle();

        // Pass information fromt the variables in the activity to the fragment
        args.putString("topic",topicToPublish);
        args.putString("text", content);
        args.putInt   ("qos",qos);
        publishFragment.setArguments(args);

        // Start transaction and commit
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, publishFragment);
        fragmentTransaction.commit();
    }

    // Interface to launch an MQTTConnectFragment fragment
    public void launchConnectFragment(String data) {

        // Set the title in the toolbar
        setTitle(getString(R.string.mqtt_connect));

        // Create instance of MQTTConnectFragment
        connectFragment  = new MQTTConnectFragment();

        Bundle args = new Bundle();

        // Pass information from the variables in the activity to the fragment
        args.putString("broker", broker);
        args.putString("port",   port);
        args.putString("client", clientId);
        connectFragment.setArguments(args);

        // Start transaction and commit
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, connectFragment);
        fragmentTransaction.commit();
    }

    // Interface to launch an MQTTSubscribeFragment fragment
    public void launchSubscribeFragment(String data) {

        // Set the title in the toolbar
        setTitle(getString(R.string.mqtt_subscribe));

        // Create instance of MQTTSubscribeFragment
        subscribeFragment  = new MQTTSubscribeFragment();

        Bundle args = new Bundle();

        // Pass information from the variables in the activity to the fragment
        args.putString("topic", topicToSubscribe);
        args.putStringArray("messages", messages);
        subscribeFragment.setArguments(args);

        // Start transaction and commit
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, subscribeFragment);
        fragmentTransaction.commit();
    }


    // Interface to call the construction of MQTT client from fragments.
    public void createMQTTClient(String connectParams[]){

        // This method is called from  MQTTConnectFragment and it passes an array of
        // strings with the information gathered from the GUI to create an MQQT client
        MQTTClientClass mqttClient = new MQTTClientClass();
        mqttClient.execute(connectParams);
    }

    // Interface to publish an MQTT message to topicToPublish.
    public void publishMQTTmessage(String publishParams[]) {

        // This method is called from  MQTTPublishFragment and it passes an array of
        // strings with the information gathered from the GUI to create an MQQT message
        MQTTClientClass mqttClient = new MQTTClientClass();
        mqttClient.execute(publishParams);
    }

    // Interface to subscribe the MQTT client to a given Topic
    public void subscribeMQTTtopic(String subscribeParams[]){

        try {
            // Declare callback function to be called when a reception event occurs
            client.setCallback(this);
            // Subscribe to the topic given by the MQTTSubscribeFragment
            client.subscribe(subscribeParams[1]);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not subscribe ", Toast.LENGTH_SHORT).show();

        }
        Toast.makeText(getApplicationContext(), "Subscribed to Topic " + subscribeParams[1], Toast.LENGTH_SHORT).show();
    }


    // Method to start a default file browser specific to each device.
    public void findFile(){

        // Create intent
        Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
        fileintent.setType("*/*");

        try {
            // Start the activity that returns to onActivityResult
            startActivityForResult(fileintent, PICKFILE_RESULT_CODE);
        } catch (ActivityNotFoundException e) {
            Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
        }
    }


    // This method is called after the startActivityForResult method is invoked.
     @Override
     public  void onActivityResult(int requestCode, int resultCode, Intent data) {
         // TODO Fix no activity available
         if (data == null)
             return;
         switch (requestCode) {
             case PICKFILE_RESULT_CODE:
                 if (resultCode == RESULT_OK) {
                     // The data intent returns an URI with getData
                     pathUri = data.getData();
                 }
         }
     }


    // This has to be implemented in order for the subscribe callback to be declared
    @Override
    public void connectionLost(Throwable cause) {
        // TODO Auto-generated method stub
        Toast.makeText(getApplicationContext(), "Lost connection", Toast.LENGTH_SHORT).show();

    }

    // This has to be implemented in order for the subscribe callback to be declared
    // When the message arrives, the GUI is updated, and the data is stored, 5 items max.
    @Override
    public void messageArrived(String topic, MqttMessage message)
            throws Exception {

        // Update local variable for topic and received message
        MQTTmessage     = message.toString();
        topicToSubscribe = topic;

        // Shift the data in the array containing the received messages
        for(int i = 4; i >= 1; i--){
            messages[i] =  messages[i-1];
        }
        messages[0] = topic + "/" + message;

        // Updating the GUI has to be done in the main Thread, since here we are in a side thread,
        // it is necessary to call runOnUiThread to do so
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    // Update the GUI only if we are in the Subscribe Fragment, otherwise we cannot reach the Views
                    if(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof MQTTSubscribeFragment ) {
                        // Call updateList from the MQTTSubscribeFragment to be able to see the Views and variable to update
                        // TODO: check if the the instance of MQTTSubscribeFragment that was previously created can be used here...
                        MQTTSubscribeFragment fragment_obj = (MQTTSubscribeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                        fragment_obj.updateList(messages);
                    }
                } catch (Exception e) {
                    Log.d("Error", "" + e);
                }
            }
        });
    }

    // This has to be implemented in order for the subscribe callback to be declared
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // TODO Auto-generated method stub
    }

    /*
    The AsyncTask is called with <Params, Progress, Result>
    This class contains all the Paho MQTT functionality
    */
    public class MQTTClientClass extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... paramString) {

            // The same Async function will be called from different fragments, keeping the unity
            // of the implementation. For this, the first string of the passed parameters is checked
            // for matches with the cases to process
            switch (paramString[0]){

                // If called from MQTTConnectFragment
                case "connect":

                    // Retrieve the information from the arguments into the local variables
                    broker                        = paramString[1];
                    port                          = paramString[2];
                    brokerURI                     = paramString[3];
                    clientId                      = paramString[4];

                    // Add memory persistence to the client
                    MemoryPersistence persistence = new MemoryPersistence();

                    try {
                        // Create client with the given URI, ID and persistence, add options and session type
                        client  = new MqttClient(brokerURI, clientId, persistence);
                        options = new MqttConnectOptions();
                        options.setCleanSession(true);
                        options.setConnectionTimeout(60);
                        options.setKeepAliveInterval(60);


                        if(paramString[5] == "tcp"){
                            // Do nothing if security is not selected
                        }else {
                            // If secure connection enabled create the keys and SocketFactory and add
                            // it to the connection options
                            InputStream caCert = getContentResolver().openInputStream(pathUri);
                            //InputStream clientCert  = getResources().openRawResource(R.raw.certificate);
                            //InputStream privateKey  = getResources().openRawResource(R.raw.privatekey);
                            options.setSocketFactory(SslUtil.getSocketFactory("1234", paramString[1], paramString[2], caCert, null, null));
                        }

                        // TODO: Rid these debugging prints
                        // Connect to the server
                        System.out.println("Connecting to broker: " + broker);
                        client.connect(options);
                        System.out.println("Connected");

                        return paramString;

                    } catch(MqttException me) {
                        // TODO: Rid these debugging prints
                        System.out.println("reason "+me.getReasonCode());
                        System.out.println("msg "   +me.getMessage());
                        System.out.println("loc "   +me.getLocalizedMessage());
                        System.out.println("cause " +me.getCause());
                        System.out.println("excep " + me);
                        me.printStackTrace();
                    } catch (Exception e) {
                        Log.d("Things Flow I/O", "Error " + e);
                        e.printStackTrace();
                    }
                    break;

                // If called from MQTTPublishFragment
                case "publish":

                    // Retrieve the information from the arguments into the local variables
                    content         = paramString[1];
                    topicToPublish  = paramString[2];
                    qos             = Integer.parseInt(paramString[3]);

                    // Create the message to send and set the Quality of Service
                    // TODO: Rid these debugging prints
                    System.out.println("Publishing message: " + content);
                    MqttMessage message = new MqttMessage(content.getBytes());
                    message.setQos(qos);

                    try {
                        // Publish the msessage
                        client.publish(topicToPublish, message);
                        System.out.println("Message published");
                        // TODO: Rid these debugging prints
                    } catch (MqttException e) {
                        e.printStackTrace();
                        return null;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                    return paramString;

                // If called from MQTTSubscribeFragment
                case "subscribe":
                    //TODO: Subscription extra actions, nothing so far
                    break;
            }
            return null;
        }

        // To do after the Async task has finished
        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);

            // If execution of the task was successful, the result will not be null
            if(result != null){
                switch (result[0]){
                    case "connect":
                        Log.d("Connect", "just connected");
                        Toast.makeText(getApplicationContext(), "Connected to " + broker + " on Port " + port, Toast.LENGTH_SHORT).show();
                        break;

                    case "publish":
                        Log.d("Publish", "just published");
                        Toast.makeText(getApplicationContext(), "Published " + content + " on Topic " + topicToPublish, Toast.LENGTH_SHORT).show();
                        break;

                    case "subscribe":
                        Toast.makeText(getApplicationContext(), "Subscribed " + content+ " to Topic " + topicToPublish, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Could not perform action, check Connectivity ", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
