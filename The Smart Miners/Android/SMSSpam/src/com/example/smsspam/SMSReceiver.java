package com.example.smsspam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		
		Bundle extras = arg1.getExtras();
		String origin;
		String body= null;
		Object[] pdus = (Object[]) extras.get("pdus");
		for (Object pdu : pdus) {
		        SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdu);
		        //origin = msg.getOriginatingAddress();
		        body = msg.getMessageBody();
		        //Toast.makeText(arg0, "body is " + body, Toast.LENGTH_SHORT).show();
		}
		
		//applyBayesian(arg0,body);
		
		applySVM(arg0,body);
		
	}

	private void applySVM(Context arg0, String body) {
		
		
	}

	private void applyBayesian(Context arg0, String body) {
		BayesianFilter bayes = new BayesianFilter();
		bayes.train(arg0);
		String predictedLabel = bayes.predictLabel(body);
		Toast.makeText(arg0, "predicted label is " + predictedLabel, Toast.LENGTH_LONG).show();
		
		
	}

}
