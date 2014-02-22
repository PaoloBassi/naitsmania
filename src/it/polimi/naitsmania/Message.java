package it.polimi.naitsmania;

import java.nio.charset.Charset;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Message extends Activity {
	
	NfcAdapter adapter;
	PendingIntent pendingIntent;
	IntentFilter writeTagFilters[];
	boolean writeMode;
	Tag mytag;
	final Context ctx = this;
	final Activity ac = this;
	private NdefMessage mNdefMessage;
	private NfcAdapter mNfcAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create);

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		
		Button send = (Button) findViewById(R.id.buttons);
		final EditText surname = (EditText) findViewById(R.id.surname);
		final EditText name = (EditText) findViewById(R.id.name);
		final EditText city = (EditText) findViewById(R.id.city);
		
		send.setOnClickListener(new View.OnClickListener() {

			 @SuppressLint("NewApi") @Override
			public void onClick(View view) {
				
		        // create an NDEF message with two records of plain text type
		        mNdefMessage = new NdefMessage(
		                       new NdefRecord[] {
		                       createNewTextRecord(surname.getText().toString(), Locale.ENGLISH, true),
		                       createNewTextRecord(name.getText().toString(), Locale.ENGLISH, true), 
		                       createNewTextRecord(city.getText().toString(), Locale.ENGLISH, true),});
		  
		        if (mNfcAdapter != null){
		        	if(mNfcAdapter.isEnabled()){
		        mNfcAdapter.setNdefPushMessage(mNdefMessage, ac);
		        
		    	AlertDialog.Builder adb = new AlertDialog.Builder(ctx);
				adb.setTitle("NFC message");

				adb.setMessage("message created")
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
			}
		        	else{
		        		
		        		AlertDialog.Builder adb = new AlertDialog.Builder(ctx);
						adb.setTitle("NFC Technology");

						adb.setMessage("Enable NFC and send the message!")
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
		        		
		        	}
		        }
		        
		        else{
		        	
		        	AlertDialog.Builder adb = new AlertDialog.Builder(ctx);
					adb.setTitle("NFC Technology");

					adb.setMessage("You are using Bluetooth to send message.")
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
		        	
					
					// bluetooth sendmessage function here
		        }
			 }
	});
		
	}
		 public static NdefRecord createNewTextRecord(String text, Locale locale, boolean encodeInUtf8) {
		        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
		 
		        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
		        byte[] textBytes = text.getBytes(utfEncoding);
		 
		        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
		        char status = (char)(utfBit + langBytes.length);
		 
		        byte[] data = new byte[1 + langBytes.length + textBytes.length];
		        data[0] = (byte)status;
		        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
		        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
		 
		        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
		    }

		@SuppressLint("NewApi") 
		@Override
			public void onResume() {
				super.onResume();

				if (mNfcAdapter != null)
					mNfcAdapter.setNdefPushMessage(mNdefMessage, ac);
			}

			@SuppressLint("NewApi")
			@Override
			public void onPause() {
				super.onPause();

				if (mNfcAdapter != null)
					mNfcAdapter.setNdefPushMessage(mNdefMessage, ac);
			}
}