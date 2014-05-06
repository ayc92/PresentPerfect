package com.radishugrads.presentperfect;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Recognition implements Runnable{

	static String dir = "../audio/";
	String address = "http://www.google.com/speech-api/v1/recognize?lang=en-us&client=chromium";
	String agent = "Mozilla/5.0";
	String type = "audio/x-flac; rate=16000";

	String utt = "utterance";
	String first = "\":\"";
	String second = "\",\"";
	
	private Handler handler = null;
	private String filePath = null;
	
	public Recognition(Handler h, String f) {
		handler = h;
		filePath = f;
	}
	
	public void run() {
		String s = test("0", filePath);
		Message msg = new Message();
		msg.obj = s;
		handler.sendMessage(msg);
	}

	private String test (String num, String flacfile) {
		Log.d("asdf", "started Recognition.test");
		Log.d("asdf", "filepath is " + flacfile);
		try {
			URL url = new URL (address);
			URLConnection urlConnection = url.openConnection ();
            urlConnection.setUseCaches(false);
			HttpURLConnection link = (HttpURLConnection) urlConnection;
            link.setInstanceFollowRedirects (false);
			link.setRequestMethod ("POST");
			urlConnection.setDoOutput (true);
			link.setRequestProperty ("User-Agent", agent );
			link.setRequestProperty ("Content-Type", type);
			DataInputStream inStream = new DataInputStream (
				new FileInputStream (flacfile));
			DataOutputStream outStream = new DataOutputStream (
												link.getOutputStream());
			byte buffer [] = new byte[4096];
			int len;
			while ((len = inStream.read (buffer)) > 0) {
				outStream.write(buffer, 0, len);
			}
			outStream.close ();
			inStream.close ();
			Thread.sleep (150);

			int responseCode = link.getResponseCode ();
			Log.d("asdf", "responseCode is " + responseCode);
			if (responseCode == 200) {
				InputStream resultStream = link.getInputStream ();
				BufferedReader in = new BufferedReader (
					new InputStreamReader (resultStream));
				StringBuffer sb = new StringBuffer ();
				String line = null;
				while ((line = in.readLine ()) != null) {
					sb.append (line);
					sb.append ("\n");
				}
				in.close ();
				String result = new String (sb);
				int pos = result.indexOf (utt);
				if (pos == -1) {
					System.out.println (num+"\tNo utt result");
					return "error";
				}
				int qos = result.indexOf (first, pos+1);
				if (qos == -1) {
					System.out.println (num+"\tNo first result");
					return "error";
				}
				int ros = result.indexOf (second, qos+1);
				if (ros == -1) {
					System.out.println (num+"\tNo second result");
					return "error";
				}
				String recognized = result.substring (qos+3, ros);
				System.out.println (num+"\t"+recognized);
				return recognized;
			}
		}
		catch (Exception e) {
			e.printStackTrace ();
			Log.d("asdf", "exception in Recognition.test");
		}
		
		return "**ERROR**";
	}
}