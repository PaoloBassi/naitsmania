package it.polimi.naitsmania;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class PreparingToConnection extends Activity{
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preparing_connection);
		
		Button btn = (Button) findViewById(R.id.buttonConnection);
		
		btn.setOnClickListener(new View.OnClickListener() {
			
			// first step, choose the leader
			@Override
			public void onClick(View v) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(PreparingToConnection.this);
				final AlertDialog alert = builder.create();
				alert.setCanceledOnTouchOutside(false);
				alert.setTitle("Choose the leader");
				alert.setMessage("Now you have to choose if you want to be the group leader or not. \nThe group leader" +
						" is the one that will interact directly with the screen.");
				alert.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
					
					// second step, choose the group
					@Override
					public void onClick(DialogInterface dialog, int which) {
						 alert.dismiss();
						 // get the name group, if it has been created 
						 final SharedPreferences sharedPref = getSharedPreferences(getString(R.string.groupInfo), Context.MODE_PRIVATE);
						 String value = sharedPref.getString(getString(R.string.groupInfo), "No groups are registered");
						 
						 final AlertDialog alert2 = builder.create();
						 alert2.setCanceledOnTouchOutside(false);
						 alert2.setTitle("Choose the Group");
						 // here we shall put a control flow with different options in case of group not declared
						 alert2.setMessage("Do you want to use the group you defined or do you want to create another one (" +
						 		"You will have to restart from the beginning)?\n\n" + "Current group registered: " + value);
						 alert2.setButton(DialogInterface.BUTTON_POSITIVE, "Use my group", new DialogInterface.OnClickListener() {
							
							// use the registered group
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// go to the master's listener screen of NFC
								Intent i = new Intent(getBaseContext(), Read.class);
								startActivity(i);
							}
						});
						 // go to the creating group page
						 alert2.setButton(DialogInterface.BUTTON_NEGATIVE, "Create new one", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent i = new Intent(getBaseContext(), CreateGroupActivity.class);
								startActivity(i);
								// close the current activity
								PreparingToConnection.this.finish();
							}
						});
						alert2.show();
					}
				});
				
				// Here the answer of the servants
				alert.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// go to listening NFC page
						Intent i = new Intent(getBaseContext(), Message.class);
						startActivity(i);
						PreparingToConnection.this.finish();
					}
				});
				alert.show();
			}
		});
	}
}
