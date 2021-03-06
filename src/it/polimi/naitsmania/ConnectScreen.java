package it.polimi.naitsmania;

import java.util.ArrayList;
import java.util.Arrays;

import it.polimi.naitsmania.contentprovider.MyPlacesContentProvider;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ConnectScreen extends Activity {
	
	// Debugging
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Layout Views
    private TextView mTitle;
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;
    private Button blueButton;
    private Button redButton;
    private Button greenButton;
    private Button yellowButton;
    private int numb = 0;
    
    //Buttons
    private String readMessage;
    private ArrayList<String> readMessageArray = new ArrayList<String>(Arrays.asList("Blue", "Red", "Green", "Yellow"));

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;
    
    //Database
    private Uri placeUri;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        
        // Set up the window layout
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.connectscreen);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

        // Set up the custom title
        mTitle = (TextView) findViewById(R.id.title_left_text);
        mTitle.setText(R.string.app_name);
        mTitle = (TextView) findViewById(R.id.title_right_text);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
            if (mChatService == null) {
            	//Setup connection between phones.
            	Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            	ensureDiscoverable();
            	
                //Setup chat window
            	setupChat();
            }
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
              // Start the Bluetooth chat services
              mChatService.start();
            }
        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        mConversationView = (ListView) findViewById(R.id.in);
        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton = (Button) findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                TextView view = (TextView) findViewById(R.id.edit_text_out);
                String message = view.getText().toString();
                sendMessage(message);
            }
        });
        
        //Initialize the buttons
        blueButton = (Button) findViewById(R.id.bluebutton);
        blueButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send button item to myplaces.
            	if (D) Log.e(TAG, "Blue button pushed.");
            	addPlaceToList(readMessageArray.get(0).toString());
            	Toast.makeText(getApplicationContext(), readMessageArray.get(0) + " has been added.", Toast.LENGTH_SHORT).show();
            	//reArrange(0);
            }
        });
        
        redButton = (Button) findViewById(R.id.redbutton);
        redButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send button item to myplaces.
            	addPlaceToList(readMessageArray.get(1).toString());
            	Toast.makeText(getApplicationContext(), readMessageArray.get(1) + " has been added.", Toast.LENGTH_SHORT).show();
                //reArrange(1);
            }
        });
        
        greenButton = (Button) findViewById(R.id.greenbutton);
        greenButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send button item to myplaces.
            	addPlaceToList(readMessageArray.get(2).toString());
            	Toast.makeText(getApplicationContext(), readMessageArray.get(2) + " has been added.", Toast.LENGTH_SHORT).show();
                //reArrange(2);
            }
        });
        
        yellowButton = (Button) findViewById(R.id.yellowbutton);
        yellowButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send button item to myplaces.
            	addPlaceToList(readMessageArray.get(3).toString());
            	Toast.makeText(getApplicationContext(), readMessageArray.get(3) + " has been added.", Toast.LENGTH_SHORT).show();
                //reArrange(3);
            }
        });

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }

    private void ensureDiscoverable() {
        if(D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }

    // The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener =
        new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            if(D) Log.i(TAG, "END onEditorAction");
            return true;
        }
    };

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothChatService.STATE_CONNECTED:
                    mTitle.setText(R.string.title_connected_to);
                    mTitle.append(mConnectedDeviceName);
                    mConversationArrayAdapter.clear();
                    break;
                case BluetoothChatService.STATE_CONNECTING:
                    mTitle.setText(R.string.title_connecting);
                    break;
                case BluetoothChatService.STATE_LISTEN:
                case BluetoothChatService.STATE_NONE:
                    mTitle.setText(R.string.title_not_connected);
                    break;
                }
                break;
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                mConversationArrayAdapter.add("Me:  " + writeMessage);
                break;
            case MESSAGE_READ:
            	if (numb>=4) {
            		return;
            	}
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                readMessage = new String(readBuf, 0, msg.arg1);
                mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                
                if (D) Log.e(TAG, "Is about to add element to array.");
                //Add to readMessageArray
                readMessageArray.add(numb, readMessage);
                setButtonText(numb, readMessage);
                if (D) Log.e(TAG, "Added element to array.");
                if (numb<=3) {
                	numb += 1;
                }
                
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                // Get the device MAC address
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                // Get the BLuetoothDevice object
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                // Attempt to connect to the device
                mChatService.connect(device);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupChat();
            } else {
                // User did not enable Bluetooth or an error occured
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.scan:
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            return true;
        case R.id.discoverable:
            // Ensure this device is discoverable by others
            ensureDiscoverable();
            return true;
        }
        return false;
    }
    
	private void addPlaceToList(String place) {
		String category = "Normal";
		String summary = place;
		String description = "Place";

		// only save if either summary or description
		// is available

		if (summary.length() == 0) {
			return;
		}

		ContentValues values = new ContentValues();
		values.put(PlacesTable.COLUMN_CATEGORY, category);
		values.put(PlacesTable.COLUMN_ADRESS, summary);
		values.put(PlacesTable.COLUMN_DESCRIPTION, description);

		if (placeUri == null) {
			// New place
			placeUri = getContentResolver().insert(
					MyPlacesContentProvider.CONTENT_URI, values);
		} else {
			// Update place
			getContentResolver().update(placeUri, values, null, null);
		}
	}
	private void setButtonText(int number, String theMessage) {
        if (number == 0) {
    		//Input to buttons
            if(D) Log.e(TAG, theMessage);
            blueButton = (Button) findViewById(R.id.bluebutton);
            blueButton.setText(theMessage);
        } else if (number == 1) {
            if(D) Log.e(TAG, theMessage);
            redButton = (Button) findViewById(R.id.redbutton);
            redButton.setText(theMessage);
        	
        } else if (number == 2) {
            if(D) Log.e(TAG, theMessage);
            greenButton = (Button) findViewById(R.id.greenbutton);
            greenButton.setText(theMessage);
        } else if (number == 3) {
            if(D) Log.e(TAG, theMessage);
            yellowButton = (Button) findViewById(R.id.yellowbutton);
            yellowButton.setText(theMessage);
        }
	}
	/*
	private void reArrange(int element) {
		ArrayList <String> tempList = new ArrayList<String>();
		tempList.addAll(readMessageArray);
		readMessageArray.clear();
		tempList.remove(0);
		readMessageArray.addAll(tempList);
		if (D) Log.e(TAG, "reArrange: " + element);
		if (element < 4) { 
			while (element != 3) {
				setButtonText(element, readMessageArray.get(element).toString());
				if (D) Log.e(TAG, "reArrange numb end: " + numb + " with name " + readMessageArray.get(element).toString());
				element++;
			}
		}
	}*/

}
	
