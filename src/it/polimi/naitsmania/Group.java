package it.polimi.naitsmania;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class Group extends Activity {

	final Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group);

		TextView tv = (TextView) findViewById(R.id.list);

		FileInputStream fIn = null;
		InputStreamReader isr = null;

		char[] inputBuffer = new char[255];
		String data = null;

		try {
			fIn = openFileInput("group.txt");
			isr = new InputStreamReader(fIn);
			isr.read(inputBuffer);
			data = new String(inputBuffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if(isr!=null){
			isr.close();
			fIn.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		if(data==null)
			data = "no group";
		
		
		tv.setText(data);
	}

}
