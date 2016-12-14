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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class MQTTConnectFragment extends Fragment {

    public String                   brokerString;
    public String                   portString;
    public String                   clientString;
    public String                   protocol;
    public String                   filePath;

    public boolean                  TLS = false;

    public EditText                 textBroker;
    public EditText                 textPort;
    public EditText                 textClient;

    public FloatingActionButton     fabConnectToBroker;
    public FloatingActionButton     fabPublish;
    public FloatingActionButton     fabSubscribe;

    public RadioButton              radioNormal;
    public RadioButton              radioTLS;

    public ConnectDataPassListener mCallback;

    public MQTTConnectFragment() {
        // Required empty public constructor
    }

    // Interface of the functions from the parent Activity that this Fragment will call
    public interface ConnectDataPassListener {
        void launchPublishFragment(String data);
        void launchSubscribeFragment(String data);
        void createMQTTClient(String connectParams[]);
        void findFile();
    }

    //
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity;

        // An Activity is needed to create the interface callback, so it is cast from the context
        // This is due to the onAttach method with Activity instead of context has ben deprecated
        if (context instanceof Activity) {
            activity = (Activity) context;

            // This makes sure that the container activity has implemented
            // the callback interface. If not, it throws an exception
            try {
                mCallback = (ConnectDataPassListener) activity;

            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement ConnectDataPassListener");
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
        View rootView = inflater.inflate(R.layout.fragment_connect, container, false);

        // Initialise all necessary Views, their values and onClickListener's
        fabConnectToBroker  = (FloatingActionButton) rootView.findViewById(R.id.fabConnectToBroker);
        fabPublish          = (FloatingActionButton) rootView.findViewById(R.id.fabPublish);
        fabSubscribe        = (FloatingActionButton) rootView.findViewById(R.id.fabSubscribe);

        textBroker          = (EditText) rootView.findViewById(R.id.editTextBroker);
        textPort            = (EditText) rootView.findViewById(R.id.editTextPort);
        textClient          = (EditText) rootView.findViewById(R.id.editTextClient);

        radioNormal         = (RadioButton) rootView.findViewById(R.id.radioNormal);
        radioTLS            = (RadioButton) rootView.findViewById(R.id.radioTLS);

        fabConnectToBroker  .setOnClickListener(onClickListenerMQTT);
        fabPublish          .setOnClickListener(onClickListenerMQTT);
        fabSubscribe        .setOnClickListener(onClickListenerMQTT);
        radioNormal         .setOnClickListener(onClickListenerMQTT);
        radioTLS            .setOnClickListener(onClickListenerMQTT);

        textBroker  .setText("192.168.0.11");
        textPort    .setText("1883");
        textClient  .setText("SampleClient");

        radioNormal .setChecked(true);

        return rootView;
    }

    /*
     This is called when landing here from another fragment (through the parent Activity)
     Therefore, the values are extracted of the arguments that have been passed onto here
     to have consistency in the UI values and update them as needed
     */
    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();

        //
        if (args != null) {
            // Get the arguments from the transaction
            textBroker  .setText(args.getString("broker"));
            textPort    .setText(args.getString("port"));
            textClient  .setText(args.getString("client"));

            // Type of connection, one disables the other
            if (TLS) {
                radioNormal.setChecked(false);
                radioTLS.setChecked(true);

            } else {
                radioNormal.setChecked(true);
                radioTLS.setChecked(false);
            }
        }
    }

    // onClickListener for all Views. The action if filtered by the name of the View
    private View.OnClickListener onClickListenerMQTT = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {

            switch (v.getId()) {

                case R.id.fabConnectToBroker:
                    //Handle Main action button to perform a connection

                    // Get text from text fields
                    brokerString    = textBroker.getText().toString();
                    portString      = textPort  .getText().toString();
                    clientString    = textClient.getText().toString();

                    // This String is built depending on the type of connection and data from the
                    // UI
                    String URIbroker;
                    if (!TLS) {
                        URIbroker = "tcp://" + brokerString + ":" + portString;
                        protocol = "tcp";
                    } else {
                        URIbroker = "ssl://" + brokerString + ":" + portString;
                        protocol = "ssl";
                    }

                    // Bundle the parameters, and call the parent Activity method to start the connection
                    String connectParams[] = {"connect", brokerString, portString,
                            URIbroker, clientString, protocol, filePath};
                    mCallback.createMQTTClient(connectParams);

                    break;

                case R.id.fabPublish:
                    // Change to the Publish fragment, through the parent Activity interface
                    mCallback.launchPublishFragment("");
                    break;

                case R.id.fabSubscribe:
                    // Change to the Subscribe fragment, through the parent Activity interface
                    mCallback.launchSubscribeFragment("");
                    break;

                case R.id.radioNormal:
                    // Set the type of connection. One type disables the other
                    radioTLS.setChecked(false);
                    TLS = false;
                    break;

                case R.id.radioTLS:
                    // Set the type of connection. One type disables the other
                    radioNormal.setChecked(false);
                    textPort.setText("8883");
                    TLS = true;

                    // this also starts the parent Activity interface to look for the Certificate Files
                    mCallback.findFile();

                    break;
            }
        }
    };
}



