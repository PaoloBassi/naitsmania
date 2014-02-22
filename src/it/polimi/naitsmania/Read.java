package it.polimi.naitsmania;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Read extends Activity {
	
	private TextView mTextView;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilters;
    private String[][] mNFCTechLists;
    private ArrayList<String> member = new ArrayList<String>();
    private FileOutputStream fOut = null;
	private OutputStreamWriter osw = null;

 
    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
 
        setContentView(R.layout.read);
        mTextView = (TextView)findViewById(R.id.mex);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Button read = (Button) findViewById(R.id.buttonr);
        Button ne = (Button) findViewById(R.id.buttonnew);
        final Context con = this;

    	try{
    		 
   
    
    	
    }
    	catch (Exception e) { 
    		e.printStackTrace();
    	}
    	
    	ne.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				
				mTextView.setText("waiting new memeber");
			}});
        
        read.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				
		AlertDialog.Builder adb = new AlertDialog.Builder(con);
		adb.setTitle("NFC read");

		adb.setMessage("group stored")
				.setCancelable(false)
				.setNeutralButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
							}
		});

		adb.show();
		write(member);
		
        }});
 
        // create an intent with tag data and deliver to this activity
        mPendingIntent = PendingIntent.getActivity(this, 0,
            new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
 
        // set an intent filter for all MIME data
        IntentFilter ndefIntent = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefIntent.addDataType("*/*");
            mIntentFilters = new IntentFilter[] { ndefIntent };
        } catch (Exception e) {
            Log.e("TagDispatch", e.toString());
        }
 
        mNFCTechLists = new String[][] { new String[] { NfcF.class.getName() } };
    }
 
    @Override
    public void onNewIntent(Intent intent) {
 
        String s="";
        
        // parse through all NDEF messages and their records and pick text type only
        Parcelable[] data = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (data != null) {
            try {
                for (int i = 0; i < data.length; i++) {
                    NdefRecord [] recs = ((NdefMessage)data[i]).getRecords();
                    for (int j = 0; j < recs.length; j++) {
                        if (recs[j].getTnf() == NdefRecord.TNF_WELL_KNOWN &&
                            Arrays.equals(recs[j].getType(), NdefRecord.RTD_TEXT)) {
                            byte[] payload = recs[j].getPayload();
                            String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
                            int langCodeLen = payload[0] & 0077;
 
                            s += (new String(payload, langCodeLen + 1, payload.length - langCodeLen - 1,
                                 textEncoding) + "\"");
                        }
                    }
                }
                
                member.add(s);
                
            } catch (Exception e) {
                Log.e("TagDispatch", e.toString());
            }
      
 
        mTextView.setText(s);
    }
    }
    @Override
    public void onResume() {
        super.onResume();
 
        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, mNFCTechLists);
    }
 
    @Override
    public void onPause() {
        super.onPause();
 
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    
    @SuppressWarnings("deprecation")
	public void write(ArrayList<String> input){
    	
    	int i;
    	
    	try{
    		fOut = openFileOutput("group.txt",MODE_WORLD_WRITEABLE); 
        	osw = new OutputStreamWriter(fOut);
        	for(i=0;i<input.size();i++){
        		osw.append(input.get(i)+";\n");
            	osw.flush();
        	}
        	
        	osw.close();
        	fOut.close();
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.getMessage());
    		
    	}
    	
    }
}
