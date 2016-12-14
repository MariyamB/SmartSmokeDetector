package com.patterns.io.mqttpatterns;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MQTTSubscribeFragment extends Fragment {

    public String                       topicToSubscribe;
    public String                       messages[] = {"","","","",""};

    private ArrayAdapter<String>        messagesAdapter;

    public EditText                     topicSubscribe;

    public FloatingActionButton         fabSubscribeTotopic;
    public FloatingActionButton         fabConnect;
    public FloatingActionButton         fabLaunchPublish;

    public ListView                     listViewMessages;

    public SubscribeDataPassListener    mCallback;

    public MQTTSubscribeFragment() {
        // Required empty public constructor
    }

    // Interface of the functions from the parent Activity that this Fragment will call
    public interface SubscribeDataPassListener {
        void launchPublishFragment(String data);
        void launchConnectFragment(String data);
        void subscribeMQTTtopic(String messageParams[]);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity;

        // An Activity is needed to create the interface callback, so it is cast from the context
        // This is due to the onAttach method with Activity instead of context has ben deprecated
        if (context instanceof Activity){
            activity=(Activity) context;

            // This makes sure that the container activity has implemented
            // the callback interface. If not, it throws an exception
            try {
                mCallback = (SubscribeDataPassListener) activity;

            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement SubscribeDataPassListener");
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        // Inflate the menu; this adds items to the action bar if it is present.
        //inflater.inflate(R.menu.devicefragment, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {

            // Create a dialog that pups up with information about the application
            final Dialog dialog = new Dialog(getActivity());

            dialog.setContentView(R.layout.about_layout);
            dialog.setTitle(R.string.aboutpatterns);

            Button btnCancel = (Button) dialog.findViewById(R.id.dismiss);
            dialog.show();

            btnCancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_mqttsubscribe, container, false);

        // Initialise all necessary Views, their values and onClickListener's
        fabSubscribeTotopic = (FloatingActionButton) rootView.findViewById(R.id.fabSubscribeToTopic);
        fabConnect          = (FloatingActionButton) rootView.findViewById(R.id.fabConnect);
        fabLaunchPublish    = (FloatingActionButton) rootView.findViewById(R.id.fabPublish);

        topicSubscribe      = (EditText)             rootView.findViewById(R.id.editTextSubscribeTopic);

        fabSubscribeTotopic .setOnClickListener(onClickListenerMQTT);
        fabConnect          .setOnClickListener(onClickListenerMQTT);
        fabLaunchPublish    .setOnClickListener(onClickListenerMQTT);

        // This list adapter is displayed in the ListView and it holds the incoming messages
        List<String> MQTTmessages = new ArrayList<String>(Arrays.asList(messages));

        messagesAdapter = new ArrayAdapter<String>(
                // The current context (this fragment's parent activity)
                getActivity(),
                // ID of list item layout xml
                R.layout.list_item,
                // ID of textView to populate
                R.id.device_item,
                // Data to populate with
                MQTTmessages);

        // Find the listView by its ID
        listViewMessages = (ListView) rootView.findViewById(R.id.listViewMessages);

        // Bind the adapter to the List View
        listViewMessages.setAdapter(messagesAdapter);

        return rootView;
    }

    /*
    This is called when landing here from another fragment (through the parent Activity)
    Therefore, the values are extracted of the arguments that have been passed onto here
    to have consistency in the UI values and update them as needed
    */
    @Override
    public void onStart(){

        super.onStart();
        Bundle args = getArguments();
        if (args != null) {

            //textBroker.setText(args.getString(puerto));
            topicSubscribe.setText(args.getString("topic"));

            messages = args.getStringArray("messages");
            if (messages != null) {
                messagesAdapter.clear();
                messagesAdapter.addAll(messages);
                // Bind the adapter to the List View
                listViewMessages.setAdapter(messagesAdapter);
            }
        }
    }

    // onClickListener for all Views. The action if filtered by the name of the View
    private View.OnClickListener onClickListenerMQTT = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {

            switch(v.getId()){

                case R.id.fabSubscribeToTopic:
                    // Handle the Button to subscribe to a topic
                    topicToSubscribe= topicSubscribe .getText().toString();

                    // Bundle the parameters, and call the parent Activity method to start the connection
                    String connectParams[] = {"subscribe", topicToSubscribe};
                    mCallback.subscribeMQTTtopic(connectParams);
                    break;

                case R.id.fabPublish:
                    //// Change to the Publish fragment, through the parent Activity interface
                    mCallback.launchPublishFragment("Text to pass FragmentB");
                    break;

                case R.id.fabConnect:
                    // Change to the Connect fragment, through the parent Activity interface
                    mCallback.launchConnectFragment("Text to pass FragmentB");
                    break;
            }
        }
    };

    // This method is called from the parent Activity and it has to run in the UI thread, to update
    // the itesm in the ListView
    public void updateList(String messages[]){

        messagesAdapter.clear();
        messagesAdapter.addAll(messages);

        // Bind the adapter to the List View
        listViewMessages.setAdapter(messagesAdapter);
    }
}
