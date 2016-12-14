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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * A placeholder fragment containing a simple view.
 */
public class MQTTPublishFragment extends Fragment {

    public String                       topicToPublish;
    public String                       textToPublish;
    public String                       qos;

    public EditText                     textPublish;
    public EditText                     textPublishTopic;
    public EditText                     textPublishQoS;

    public FloatingActionButton         fabPublishTotopics;
    public FloatingActionButton         fabConnect;
    public FloatingActionButton         fabSubscribe;

    public PublishDataPassListener      mCallback;

    public MQTTPublishFragment() {
        // Required empty public constructor
    }

    // Interface of the functions from the parent Activity that this Fragment will call
    public interface PublishDataPassListener{
        void launchSubscribeFragment(String data);
        void launchConnectFragment(String data);
        void publishMQTTmessage(String messageParams[]);
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
                mCallback = (PublishDataPassListener) activity;

            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement PublishDataPassListener");
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

            btnCancel.setOnClickListener(new OnClickListener() {
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
        View rootView = inflater.inflate(R.layout.fragment_publish, container, false);

        // Initialise all necessary Views, their values and onClickListener's
        fabPublishTotopics  = (FloatingActionButton) rootView.findViewById(R.id.fabPublishToTopic);
        fabConnect          = (FloatingActionButton) rootView.findViewById(R.id.fabConnect);
        fabSubscribe        = (FloatingActionButton) rootView.findViewById(R.id.fabSubscribe);

        textPublishTopic    = (EditText)             rootView.findViewById(R.id.editTextPublishTopic);
        textPublish         = (EditText)             rootView.findViewById(R.id.editTextPublish);
        textPublishQoS      = (EditText)             rootView.findViewById(R.id.editTextQoS);

        fabPublishTotopics  .setOnClickListener(onClickListenerMQTT);
        fabConnect          .setOnClickListener(onClickListenerMQTT);
        fabSubscribe        .setOnClickListener(onClickListenerMQTT);

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

            textPublishTopic.setText(args.getString("topic"));
            textPublish     .setText(args.getString("text"));
            textPublishQoS  .setText(Integer.toString(args.getInt("qos")));
        }
    }

    // onClickListener for all Views. The action if filtered by the name of the View
    private OnClickListener onClickListenerMQTT = new OnClickListener() {
        @Override
        public void onClick(final View v) {

            switch(v.getId()){

                case R.id.fabPublishToTopic:
                    //Handle the Button to publish the message
                    textToPublish   = textPublish       .getText().toString();
                    topicToPublish  = textPublishTopic  .getText().toString();
                    qos             = textPublishQoS    .getText().toString();

                    // Bundle the parameters, and call the parent Activity method to start the connection
                    String connectParams[] = {"publish", textToPublish,topicToPublish,qos};
                    mCallback.publishMQTTmessage(connectParams);

                    break;

                case R.id.fabConnect:
                    // Change to the Connect fragment, through the parent Activity interface
                    mCallback.launchConnectFragment("");
                    break;

                case R.id.fabSubscribe:
                    // Change to the Subscribe fragment, through the parent Activity interface
                    mCallback.launchSubscribeFragment("");
                    break;
            }
        }
    };
}
