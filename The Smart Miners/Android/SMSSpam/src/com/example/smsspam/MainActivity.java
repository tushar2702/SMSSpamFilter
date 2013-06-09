package com.example.smsspam;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	EditText mEdit;
	BayesianFilter bayes = new BayesianFilter();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		bayes.train(getBaseContext());
		
		
		Button btn1 = (Button)findViewById(R.id.button1);
		
		btn1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mEdit   = (EditText)findViewById(R.id.editText1);
				//Toast.makeText(getBaseContext(), "button clicked", Toast.LENGTH_SHORT).show();
				
				String predictedLabel = bayes.predictLabel(mEdit.getText().toString());
				
				Toast.makeText(getBaseContext(), predictedLabel, Toast.LENGTH_SHORT).show();
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	

}
