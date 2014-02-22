package it.polimi.naitsmania;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		TextView firstLink = (TextView) findViewById(R.id.firstLink);
		TextView secondLink = (TextView) findViewById(R.id.secondLink);
		// Make the link appears clickable
		firstLink.setMovementMethod(LinkMovementMethod.getInstance());
		secondLink.setMovementMethod(LinkMovementMethod.getInstance());
		// Click send to creation of groups
		firstLink.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), CreateGroupActivity.class);
				startActivity(intent);
			}
		});
		
		secondLink.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), ConnectScreen.class);
				startActivity(intent);
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
