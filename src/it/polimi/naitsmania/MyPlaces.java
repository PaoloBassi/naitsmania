package it.polimi.naitsmania;

import it.polimi.naitsmania.contentprovider.MyPlacesContentProvider;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MyPlaces extends Activity {

	private Spinner mCategory;
	private EditText mTitleText;
	private EditText mBodyText;

	private Uri placeUri;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.places_edit);

		mCategory = (Spinner) findViewById(R.id.category);
		mTitleText = (EditText) findViewById(R.id.place_edit_adress);
		mBodyText = (EditText) findViewById(R.id.place_edit_description);
		Button confirmButton = (Button) findViewById(R.id.place_edit_button);

		Bundle extras = getIntent().getExtras();

		// check from the saved Instance
		placeUri = (bundle == null) ? null : (Uri) bundle
				.getParcelable(MyPlacesContentProvider.CONTENT_ITEM_TYPE);

		// Or passed from the other activity
		if (extras != null) {
			placeUri = extras
					.getParcelable(MyPlacesContentProvider.CONTENT_ITEM_TYPE);

			fillData(placeUri);
		}

		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (TextUtils.isEmpty(mTitleText.getText().toString())) {
					makeToast();
				} else {
					setResult(RESULT_OK);
					finish();
				}
			}

		});
	}

	private void fillData(Uri uri) {
		String[] projection = { PlacesTable.COLUMN_ADRESS,
				PlacesTable.COLUMN_DESCRIPTION, PlacesTable.COLUMN_CATEGORY };
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		if (cursor != null) {
			cursor.moveToFirst();
			String category = cursor.getString(cursor
					.getColumnIndexOrThrow(PlacesTable.COLUMN_CATEGORY));

			for (int i = 0; i < mCategory.getCount(); i++) {

				String s = (String) mCategory.getItemAtPosition(i);
				if (s.equalsIgnoreCase(category)) {
					mCategory.setSelection(i);
				}
			}

			mTitleText.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(PlacesTable.COLUMN_ADRESS)));
			mBodyText.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(PlacesTable.COLUMN_DESCRIPTION)));

			// always close the cursor
			cursor.close();
		}
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putParcelable(MyPlacesContentProvider.CONTENT_ITEM_TYPE, placeUri);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	private void saveState() {
		String category = (String) mCategory.getSelectedItem();
		String summary = mTitleText.getText().toString();
		String description = mBodyText.getText().toString();

		// only save if either summary or description
		// is available

		if (description.length() == 0 && summary.length() == 0) {
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

	private void makeToast() {
		Toast.makeText(MyPlaces.this, "Please maintain a summary",
				Toast.LENGTH_LONG).show();
	}

}
