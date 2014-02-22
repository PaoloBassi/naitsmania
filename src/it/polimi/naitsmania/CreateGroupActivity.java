package it.polimi.naitsmania;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreateGroupActivity extends Activity{
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.creategroup);
			
		    final EditText groupName = (EditText) findViewById(R.id.groupName);
			final EditText password = (EditText) findViewById(R.id.password);
			final EditText elements = (EditText) findViewById(R.id.number);
			Button btnCreate = (Button) findViewById(R.id.btnCreate);
			final TextView tw = (TextView) findViewById(R.id.groupInfoId);
			// access the info stored in the phone 
			final SharedPreferences sharedPref = getSharedPreferences(getString(R.string.groupInfo), Context.MODE_PRIVATE);
			// Choose what to write in the textfield
			String value = sharedPref.getString(getString(R.string.groupInfo), "No groups are registered");
			tw.setText(value);
			
			btnCreate.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(!isEmpty(groupName) && !isEmpty(password) && !isEmpty(elements)){
						Toast.makeText(getBaseContext(), "Group Created!", Toast.LENGTH_SHORT).show();
						SharedPreferences.Editor editor = sharedPref.edit();
						editor.putString(getString(R.string.groupInfo), groupName.getText().toString() + "(max number of members: " + elements.getText().toString() + ")" );
						editor.commit();
						tw.setText(sharedPref.getString(getString(R.string.groupInfo), "Error"));
					}
					else{
						Toast.makeText(getBaseContext(), "Missing fields", Toast.LENGTH_SHORT).show();
					}
				}
			});
			
			
		}
		
		private boolean isEmpty(EditText et){
			return et.getText().toString().trim().length() == 0;
		}
	
}
