package com.dolibarrmaroc.com;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.utils.Base64;
import com.dolibarrmaroc.com.utils.URL;


public class SignatureActivity extends Activity {

	private GestureOverlayView gestureView;
	private String path;
	private File file;
	private Bitmap bitmap;
	public boolean gestureTouch=false;

	private Button donebutton;
	private Button clearButton;


	private  ProgressDialog dialog = null;

	private String chemin;
	private Compte compte = new Compte();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signature);

		try {
			Bundle objetbunble  = this.getIntent().getExtras();

			if (objetbunble != null) {

				chemin = this.getIntent().getStringExtra("path");
				compte =  (Compte) getIntent().getSerializableExtra("compte");
			}

			donebutton = (Button) findViewById(R.id.DoneButton);
			clearButton = (Button) findViewById(R.id.ClearButton);

			path=Environment.getExternalStorageDirectory()+"/signature.png";
			file = new File(path);
			file.delete();
			gestureView = (GestureOverlayView) findViewById(R.id.signaturePad);
			gestureView.setDrawingCacheEnabled(true);

			gestureView.setAlwaysDrawnWithCacheEnabled(true);
			gestureView.setHapticFeedbackEnabled(false);
			gestureView.cancelLongPress();
			gestureView.cancelClearAnimation();
			gestureView.addOnGestureListener(new OnGestureListener() {

				@Override
				public void onGesture(GestureOverlayView arg0, MotionEvent arg1) {

				}

				@Override
				public void onGestureCancelled(GestureOverlayView arg0,
						MotionEvent arg1) {

				}

				@Override
				public void onGestureEnded(GestureOverlayView arg0, MotionEvent arg1) {

				}

				@Override
				public void onGestureStarted(GestureOverlayView arg0,
						MotionEvent arg1) {
					if (arg1.getAction()==MotionEvent.ACTION_MOVE){
						gestureTouch=false;                     
					}
					else 
					{
						gestureTouch=true;
					}
				}

			});

			donebutton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog = ProgressDialog.show(SignatureActivity.this, "Uploading",
							"Attendez SVP...", true);
					new ImageGalleryTask().execute();
				}
			});

			clearButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					gestureView.invalidate();
					gestureView.clear(true);
					gestureView.clearAnimation();
					gestureView.cancelClearAnimation();
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class ImageGalleryTask extends AsyncTask<Void, Void, String> {
		@SuppressWarnings("unused")
		@Override
		protected String doInBackground(Void... unsued) {

			try {
				bitmap = Bitmap.createBitmap(gestureView.getDrawingCache());
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				fos = new FileOutputStream(file);
				// compress to specified format (PNG), quality - which is
				// ignored for PNG, and out stream
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.close();


				InputStream is;
				BitmapFactory.Options bfo;
				Bitmap bitmapOrg;
				ByteArrayOutputStream bao ;

				bfo = new BitmapFactory.Options();
				bfo.inSampleSize = 2;
				//bitmapOrg = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/" + customImage, bfo);

				bao = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
				byte [] ba = bao.toByteArray();
				String ba1 = Base64.encodeBytes(ba);

				String ttt = System.currentTimeMillis()+".jpg";
				//facture.setImage(ba1);


				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("image",ba1));
				nameValuePairs.add(new BasicNameValuePair("path",chemin));
				nameValuePairs.add(new BasicNameValuePair("cmd",ttt));
				Log.v("log_tag", System.currentTimeMillis()+".jpg");	
				try{
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(URL.URL+"upload_photo.php");
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();
					is = entity.getContent();

					//vendeurManager.insertFacture(facture);

					Log.v("log_tag", "In the try Loop" );

					return "Success";
				}catch(Exception e){
					Log.v("log_tag", "Error in http connection "+e.toString());
					return "error";
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			if(gestureTouch==false)
			{
				setResult(0);
				finish();
			}
			else
			{
				setResult(1);
				finish();
			}

			return null;
		}
		@Override
		protected void onProgressUpdate(Void... unsued) {

		}

		@Override
		protected void onPostExecute(String sResponse) {
			try {
				if (dialog.isShowing()){
					dialog.dismiss();
					Toast.makeText(SignatureActivity.this, "Op�ration effectuer avec success !! ", Toast.LENGTH_LONG).show();
					Intent intent = new Intent(SignatureActivity.this,VendeurActivity.class);
					intent.putExtra("user", compte);
					startActivity(intent);
					SignatureActivity.this.finish();
				}

			} catch (Exception e) {
				Log.e(e.getClass().getName(), e.getMessage() +" << ", e);
				Toast.makeText(SignatureActivity.this, "Erreur survenue ressayer puls tard", Toast.LENGTH_LONG).show();
				SignatureActivity.this.finish(); 
			}
		}
	}
}
