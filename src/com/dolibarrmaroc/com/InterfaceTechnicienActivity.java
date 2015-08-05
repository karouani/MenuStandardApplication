package com.dolibarrmaroc.com;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dolibarrmaroc.com.models.Client;
import com.dolibarrmaroc.com.models.Compte;

public class InterfaceTechnicienActivity extends Activity implements OnClickListener{

	private Compte compte;
	private Client clt;
	private String objet;
	private String superviseur;

	//Recuperation Interface de l'utilisateur Par Service
	private String nmb;
	private String serviceName;
	private List<String> labels;
	private String date;
	private String timeD;
	private String timeF;
	private String mYear, mMonth, mDay, mHour, mMinute;
	//Declaration Interface
	private LinearLayout myLayout;

	private Button valider;

	private int fieldsSize;

	private List<EditText> maVue;

	public InterfaceTechnicienActivity() {
		compte = new Compte();
		labels = new ArrayList<>();
		maVue = new ArrayList<>();
		clt = new Client();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_interface_technicien);

		try {
			myLayout = (LinearLayout) findViewById(R.id.interfaceGenerated);
			myLayout.removeAllViews();
			myLayout.setPadding(15, 5, 15, 5);
			//myLayout.setPadding(left, top, right, bottom);
			Bundle objetbunble  = this.getIntent().getExtras();

			if (objetbunble != null) {
				compte = (Compte) getIntent().getSerializableExtra("user");
				nmb = this.getIntent().getStringExtra("nmbService");
				serviceName = this.getIntent().getStringExtra("service");
				date = this.getIntent().getStringExtra("date");
				timeD = this.getIntent().getStringExtra("timed");
				timeF = this.getIntent().getStringExtra("timef");
				
				mYear = this.getIntent().getStringExtra("year");
				mMonth = this.getIntent().getStringExtra("month");
				mDay= this.getIntent().getStringExtra("day");
				mHour= this.getIntent().getStringExtra("heurD");
				mMinute= this.getIntent().getStringExtra("minD");
				
				clt = (Client) this.getIntent().getSerializableExtra("client");
				objet =  this.getIntent().getStringExtra("objet");
				superviseur =  this.getIntent().getStringExtra("Superviseur");

				for (int i = 0; i < Integer.parseInt(nmb); i++) {
					labels.add(this.getIntent().getStringExtra("labels"+i));
				}
				Log.e(">> Recuperation depuis Next Step","date : "+date+
						" Heur D : "+timeD+
						" HF : "+timeF+
						" objet "+objet+
						" Client : "+clt.toString());
			}
			/*
			TextView txt = (TextView) findViewById(R.id.testtext);
			txt.setText("L'interface Comme Suite "+labels.toString()+"\n\r Time : "+date+" "+timeD+" la durÃ¯Â¿Â½e est : "+timeF);
			 */
			
			String lan = Locale.getDefault().getLanguage();
			
			
			
			String lb = "";
			String lb2 = "";
			
			fieldsSize = Integer.parseInt(nmb);
			
			for (int i = 0; i < fieldsSize; i++) {
				
				lb = labels.get(i);
				lb2 = labels.get(i);
				
				if(lan.toLowerCase().equals("ar")){
					if(lb.equals("Marque")){
						lb = getResources().getString(R.string.tecv18)+"  (Marque) ";
						lb2 = getResources().getString(R.string.tecv18);
					}else if(lb.equals("Model")){
						lb = getResources().getString(R.string.tecv19)+"  (Model) ";
						lb2 = getResources().getString(R.string.tecv19);
					}else if(lb.equals("Matricule")){
						lb = getResources().getString(R.string.tecv20)+"  (Matricule)  ";
						lb2 = getResources().getString(R.string.tecv20);
					}else if(lb.equals("Num PSN")){
						lb = getResources().getString(R.string.tecv46);
						lb2 = getResources().getString(R.string.tecv46);
					}else if(lb.equals("Num Sim")){
						lb = getResources().getString(R.string.tecv48);
						lb2 = getResources().getString(R.string.tecv48);
					}
				}
				
				TextView label = new TextView(this);
				EditText txtEdit = new EditText(this);

				label.setText(lb+" :");
				txtEdit.setHint(lb2);
				txtEdit.setInputType(InputType.TYPE_CLASS_TEXT);
				txtEdit.setId(3949+i);

				/*
				LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT
						);
				 */

				maVue.add(txtEdit);

				myLayout.addView(label);
				myLayout.addView(txtEdit);
			}
			valider = (Button) findViewById(R.id.getIntervention);
			valider.setOnClickListener(this);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.getIntervention){
			List<String> txtRec = new ArrayList<>();
			String msg = ">> "+objet;
			String error = ">> Il Faut Remplir les champs suivants : ";
			int m = 1;
			int i = 0;

			String st="";
			String lan = Locale.getDefault().getLanguage();
			
			for (i = 0; i < fieldsSize; i++) {
				if(!"".equals(maVue.get(i).getText().toString())){
					txtRec.add(maVue.get(i).getText().toString());
					Log.i(">> Valeur de EditText"+maVue.get(i).getHint(),maVue.get(i).getText().toString());
					
					if(lan.toLowerCase().equals("ar")){
						if(maVue.get(i).getHint().equals("علامة تجارية")){
							st ="Marque";
						}else if(maVue.get(i).getHint().equals("النموذج")){
							st = "Model";
						}else if(maVue.get(i).getHint().equals("رقم لوحة السيارة")){
							st ="Matricule";
						}else if(maVue.get(i).getHint().equals("PSN رقم")){
							st ="Num PSN";
						}else if(maVue.get(i).getHint().equals("SIM رقم")){
							st ="Num Sim";
						}
						msg += "\n\r"+ st + " : "+maVue.get(i).getText().toString();
					}else{
						msg += "\n\r"+ maVue.get(i).getHint() + " : "+maVue.get(i).getText().toString();
					}
					
				

					//maVue.get(i).setText("");
				}else{
					error += "\n\r"+ maVue.get(i).getHint();
					m++;
				}

			}

			if(m > 1 ){
				Toast.makeText(InterfaceTechnicienActivity.this, error, Toast.LENGTH_LONG).show();
				Log.i("Interface Data : ",msg);
			}else{

				Intent intent = new Intent(this, ObservationTechActivity.class);

				intent.putExtra("user", compte);
				intent.putExtra("objet", objet);
				intent.putExtra("client", clt);

				intent.putExtra("date", date);
				intent.putExtra("timed", timeD);
				intent.putExtra("timef", timeF);
				
				intent.putExtra("heurD",  mHour);
				intent.putExtra("minD",  mMinute);
				intent.putExtra("year", mYear);
				intent.putExtra("month",mMonth);
				intent.putExtra("day", mDay);
				intent.putExtra("Superviseur", superviseur);
				
				msg += "\n\r Le superviseur est : "+superviseur;
				intent.putExtra("description", msg);

				startActivity(intent);
				Log.e("Interface Data : ",msg);
			}

			

		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(this)
			.setTitle(getResources().getString(R.string.tecv36))
			.setMessage(getResources().getString(R.string.tecv47))
			.setNegativeButton(R.string.tecv16, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface ds, int arg1) {
					//VendeurActivity.super.onBackPressed();
					ds.dismiss();
					
				}

			})
			.setPositiveButton(R.string.tecv15, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface ds, int arg1) {
					//VendeurActivity.super.onBackPressed();
					Intent intent1 = new Intent(InterfaceTechnicienActivity.this, ConnexionActivity.class);
					intent1.putExtra("user", compte);
					intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(intent1);
					InterfaceTechnicienActivity.this.finish();
					
				}

			}).setCancelable(true)
			.create().show();
			return true;
		}
		return false;
	}
	
}
