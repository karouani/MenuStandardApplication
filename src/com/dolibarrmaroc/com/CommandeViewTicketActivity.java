package com.dolibarrmaroc.com;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.RT_Printer.BluetoothPrinter.BLUETOOTH.BluetoothPrintDriver;
import com.dolibarrmaroc.com.models.Commandeview;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.MyTicketWitouhtProduct;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.models.PromoTicket;
import com.dolibarrmaroc.com.offline.Offlineimpl;
import com.dolibarrmaroc.com.offline.ioffline;
import com.dolibarrmaroc.com.utils.MyTicket;
import com.dolibarrmaroc.com.utils.ProduitTicket;

public class CommandeViewTicketActivity extends Activity {

	
	private List<PromoTicket> remises = new ArrayList<>();
	private List<PromoTicket> reduction = new ArrayList<>();
	private List<ProduitTicket> prd = new ArrayList<ProduitTicket>();
	
	private ioffline myoffline;
	
	
	/** Called when the activity is first created. */
	public static BluetoothAdapter myBluetoothAdapter;
	public String SelectedBDAddress;

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;

	private static final String tab00c0 = "AAAAAAACEEEEIIII" +
			"DNOOOOO\u00d7\u00d8UUUUYI\u00df" +
			"aaaaaaaceeeeiiii" +
			"\u00f0nooooo\u00f7\u00f8uuuuy\u00fey" +
			"AaAaAaCcCcCcCcDd" +
			"DdEeEeEeEeEeGgGg" +
			"GgGgHhHhIiIiIiIi" +
			"IiJjJjKkkLlLlLlL" +
			"lLlNnNnNnnNnOoOo" +
			"OoOoRrRrRrSsSsSs" +
			"SsTtTtTtUuUuUuUu" +
			"UuUuWwYyYZzZzZzF";

	/************************************
	 * SERIALZABLE DATA
	 */
	private HashMap<String, ArrayList<Produit>> produits;
	private Commandeview vx;
	private Compte compte;
	private MyTicketWitouhtProduct societe;
	private MyTicket ticket;
	private Button autre,quitter;
	private int okey = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commande_view_ticket);
		setTitle(R.string.bluetooth_unconnected);

		try {
			Bundle objetbunble  = this.getIntent().getExtras();

			ticket = new MyTicket();
			myoffline = new Offlineimpl(getApplicationContext());
			if (objetbunble != null) {
				compte =  (Compte) getIntent().getSerializableExtra("compte");
				produits = (HashMap<String, ArrayList<Produit>>) getIntent().getSerializableExtra("prod");
				vx = (Commandeview) getIntent().getSerializableExtra("cmd");
				
				
			}
			
			societe = new MyTicketWitouhtProduct();
			societe = myoffline.LoadSociete("");
			
			SelectedBDAddress = "";
			if((myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter())==null)
			{
				Toast.makeText(this,getResources().getString(R.string.none_paired), Toast.LENGTH_LONG).show();
			}
			if(!myBluetoothAdapter.isEnabled())
			{
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);    
				startActivityForResult(enableBtIntent, 2);
			}


			/******************* REMPLIR TICKET ************************************/
			ticket.setAddresse(removeDiacritic(societe.getAddresse()));
			ticket.setClient(removeDiacritic(vx.getClt().getName()).toUpperCase());
			ticket.setDejaRegler(0D);
			ticket.setDescription(removeDiacritic(societe.getDescription()));
			ticket.setFax(societe.getFax());
			ticket.setIF(societe.getIF());
			ticket.setMsg(getResources().getString(R.string.promotion));
			ticket.setNameSte(removeDiacritic(societe.getNameSte()).toUpperCase());
			ticket.setPatente(societe.getPatente());
			ticket.setSiteWeb(societe.getSiteWeb());
			ticket.setTel(societe.getTel());
			ticket.setNumFacture(vx.getRef());
			

			
			
			List<Produit> dem = produits.get("prod");
			List<Produit> prm = produits.get("promo");
			
			for (int i = 0; i < dem.size(); i++) {
				Produit p = dem.get(i);
				
				Double prix = p.getPrixttc();

				int tva = Integer.parseInt(p.getTva_tx());
				
				ProduitTicket prod = new ProduitTicket(p.getQteDispo(), removeDiacritic(p.getDesig()),prix , tva );
				prd.add(prod);
			}

			for (int i = 0; i < prm.size(); i++) {

				Produit p = prm.get(i);
				
				if(Double.parseDouble(p.getTva_tx()) == 100){
					PromoTicket promo = new PromoTicket(p.getId(), p.getDesig(), p.getQteDispo(), 1, p.getQteDispo()+"");
					remises.add(promo);
				}else{
					double a = p.getPrixttc() * p.getQteDispo();
					double t = (a *  Double.parseDouble(p.getTva_tx())) / 100D;
					double h = a - t;
					PromoTicket promo = new PromoTicket(p.getId(), p.getRef(), p.getQteDispo(), 0, h+" DH");
					promo.setPorcentage(p.getTva_tx());
					reduction.add(promo);
				}
			}

			
			
			ticket.setPrds(prd);
			Log.e("insid ",ticket.getPrds().size()+"");
			
			
			
			autre = (Button) findViewById(R.id.geocom);
			autre.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					SelectedBDAddress = "";
					if((myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter())==null)
					{
						Toast.makeText(CommandeViewTicketActivity.this,getResources().getString(R.string.none_paired), Toast.LENGTH_LONG).show();
					}else{
						if(!"".equals(myBluetoothAdapter.getAddress()) && myBluetoothAdapter.getAddress() != null){
							if(okey == 1)
							{
								Intent serverIntent = new Intent(CommandeViewTicketActivity.this, DeviceListActivity.class);
								startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);

							}else{
								createTicket(ticket);
							}
						}
					}
					if(!myBluetoothAdapter.isEnabled())
					{
						Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);    
						startActivityForResult(enableBtIntent, 2);
					}

				}
			});

			quitter = (Button) findViewById(R.id.btn_quit);
			quitter.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(CommandeViewTicketActivity.this, ConnexionActivity.class);
					intent.putExtra("user", compte);
					startActivity(intent);
					CommandeViewTicketActivity.this.finish();
				}
			});
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onStart() {
		// Launch the DeviceListActivity to see devices and do scan
		Intent serverIntent = new Intent(this, DeviceListActivity.class);
		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		//�˳�ǰ�ر�����
		BluetoothPrintDriver.close();
		super.onDestroy();
	}


	public void InitUIControl(){

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
		Log.e("request code",requestCode+"");
		if(data != null){
			switch (requestCode) {
			case REQUEST_CONNECT_DEVICE_SECURE:
				// When DeviceListActivity returns with a device to connect
				if (resultCode == Activity.RESULT_OK) 
				{
					// ������һ���豸֮ǰ�ȹر��������������豸֮���л�ʱ�����
					BluetoothPrintDriver.close();  
					// ��ȡ�豸 MAC address
					SelectedBDAddress = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
					// ��������
					if(!BluetoothPrintDriver.OpenPrinter(SelectedBDAddress)) 	
					{
						BluetoothPrintDriver.close();
						setTitle(R.string.bluetooth_connect_fail);
						//	            		mTitle.setText("����ʧ��");

						Intent serverIntent = new Intent(CommandeViewTicketActivity.this, DeviceListActivity.class);
						startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);

						okey = 1;
						return;
					}
					else
					{
						//	            		mTitle.setText(SelectedBDAddress);
						String bluetoothConnectSucess = getResources().getString(R.string.bluetooth_connect_sucess);
						this.setTitle(bluetoothConnectSucess+SelectedBDAddress);

						remplire();
						createTicket(ticket);
						
					}
				}
				break;
			case REQUEST_CONNECT_DEVICE_INSECURE:
				// When DeviceListActivity returns with a device to connect
				if (resultCode == Activity.RESULT_OK) {
					remplire();
					createTicket(ticket);
				}
				break;
			}
		}else{
			onStart();
		}
		
	}

	/************************************ CREATION TICKET *********************************************/
	@SuppressLint("NewApi")
	private void createTicket(MyTicket ticket) {
		if(!BluetoothPrintDriver.IsNoConnection()){
			BluetoothPrintDriver.Begin();

			/******************* ENVOYER TICKET ************************************/
			/******************** LOGO NAME SOCIETE ******************************/
			BluetoothPrintDriver.AddAlignMode((byte)1);//����
			BluetoothPrintDriver.SetLineSpace((byte)50);	
			BluetoothPrintDriver.SetZoom((byte)0x11);//���ߣ�����		
			BluetoothPrintDriver.ImportData(ticket.getNameSte().toUpperCase());
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();
			/************************ADDRESS SOCIETE********************************/
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();
			BluetoothPrintDriver.SetZoom((byte)0x00);//Ĭ�Ͽ�ȡ�Ĭ�ϸ߶�
			BluetoothPrintDriver.AddBold((byte)0x01);//����

			BluetoothPrintDriver.ImportData(removeDiacritic(ticket.getAddresse())); 
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();

			/*
			String nm = null;
			try {
				nm = new String(ticket.getAddresse().getBytes("ISO-8859-1"), Charset.forName("ISO-8859-1"));
				BluetoothPrintDriver.ImportData(nm);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			byte[] byteImage = EncodingUtils.getBytes(ticket.getAddresse(), "UTF-8");
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.ImportData(byteImage, byteImage.length);
			 */

			/************************VENDEUR********************************/
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.ImportData(ticket.getLine());
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();
			BluetoothPrintDriver.SetZoom((byte)0x00);//Ĭ�Ͽ�ȡ�Ĭ�ϸ߶�
			BluetoothPrintDriver.AddBold((byte)0x01);//����


			BluetoothPrintDriver.ImportData(removeDiacritic(compte.getLogin())); 
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();

			/************************ Ligne pointier ****************************/
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.ImportData(ticket.getLine());
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();

			/************************ INFOS SUR TICKET ***************************/
			BluetoothPrintDriver.SetCharacterFont((byte) 0x031);
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();
			BluetoothPrintDriver.AddBold((byte)0);//����
			BluetoothPrintDriver.AddAlignMode((byte) 0);//����
			BluetoothPrintDriver.ImportData(getResources().getString(R.string.commande_num)+" : "+ticket.getNumFacture());
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();
			BluetoothPrintDriver.ImportData(getResources().getString(R.string.client)+" "+removeDiacritic(ticket.getClient()).toUpperCase());
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();
			BluetoothPrintDriver.ImportData(getResources().getString(R.string.le)+vx.getDt());
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();
			BluetoothPrintDriver.SetCharacterFont((byte) 0x00);
			/************************ Ligne pointier ****************************/
			BluetoothPrintDriver.AddBold((byte)0x01);//����
			BluetoothPrintDriver.AddAlignMode((byte) 1);//����
			BluetoothPrintDriver.ImportData(ticket.getLine());
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();

			/************************* LES tables Headers ***********************/
			String tmpString2 = this.getResources().getString(R.string.print_table_content2);
			BluetoothPrintDriver.AddBold((byte)0x00);//����
			BluetoothPrintDriver.SetZoom((byte)0x01);//Ĭ�Ͽ�ȡ�Ĭ�ϸ߶�
			BluetoothPrintDriver.AddInverse((byte)0x01);
			//BluetoothPrintDriver.ImportData(new byte[]{0x1d,0x21,0x01}, 3);	//���ñ���
			BluetoothPrintDriver.ImportData(String.format("  "+tmpString2+" "),true);
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();

			DecimalFormat df = new DecimalFormat("0.00");
			/********************** Content Table *******************************/
			for (int i = 0; i < ticket.getPrds().size(); i++) {
				BluetoothPrintDriver.AddInverse((byte)0x00);
				BluetoothPrintDriver.SetZoom((byte)0x00);//���ߣ�����
				BluetoothPrintDriver.AddBold((byte)0x00);//����
				String data = "";
				if(ticket.getPrds().get(i).getRef().length() > 16){
					String t = ticket.getPrds().get(i).getRef().substring(0,16);
					String e = ticket.getPrds().get(i).getRef().substring(16,ticket.getPrds().get(i).getRef().length());
					data = t+"| "+ticket.getPrds().get(i).getQte()+"  |"+df.format(ticket.getPrds().get(i).getPrix());
					if(data.length()<32){
						int p = 32 - data.length();
						for (int j = 0; j < p; j++) {
							data = data + " ";
						}
					}
					String rest = main(e);
					data = data + rest;
				}else{
					String space = "";
					int k = 16 - ticket.getPrds().get(i).getRef().length();
					for (int j = 0; j <k; j++) {
						space = space + " ";
					}
					data = ticket.getPrds().get(i).getRef()+space+"| "+ticket.getPrds().get(i).getQte()+" |"+df.format(ticket.getPrds().get(i).getPrix());
				}

				BluetoothPrintDriver.SetZoom((byte)0x00);//Ĭ�Ͽ�ȡ�Ĭ�ϸ߶�
				BluetoothPrintDriver.AddInverse((byte)0x00);
				BluetoothPrintDriver.AddAlignMode((byte) 0);//����

				//BluetoothPrintDriver.ImportData(new byte[]{0x1d,0x21,0x01}, 3);	//���ñ���
				BluetoothPrintDriver.ImportData(String.format(data),true);

				BluetoothPrintDriver.excute();
				BluetoothPrintDriver.ClearData();

				if(i < ticket.getPrds().size() - 1){
					BluetoothPrintDriver.LF();
					BluetoothPrintDriver.ImportData(ticket.getLine());
					BluetoothPrintDriver.LF();
					BluetoothPrintDriver.excute();
					BluetoothPrintDriver.ClearData();
				}else{
					BluetoothPrintDriver.LF();
					BluetoothPrintDriver.excute();
					BluetoothPrintDriver.ClearData();
					BluetoothPrintDriver.LF();
					BluetoothPrintDriver.excute();
					BluetoothPrintDriver.ClearData();
				}

			}

			/************************** LEs tautaux ********************************************/
			/*
			 * 	TOTAL H.T 		2090.00 DH
			TOTAL TTC		2508.00 DH
			D�j� r�gl�		2508.00 DH
			Reste � payer	0.00 	DH
			Infos TVA (20%)	418.00 	DH	
			 */

			BluetoothPrintDriver.ImportData(ticket.getLine());
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();

			/*------------------------ HT---------------------------*/

			String data = getResources().getString(R.string.total_ht);
			double ht = vx.getHt();

			int espace = 32 - (df.format(ht).length() + data.length() + 3);

			for (int j = 0; j < espace; j++) {
				data = data + " ";
			}

			data = data + df.format(ht) + " DH";

			BluetoothPrintDriver.SetZoom((byte)0x00);//Ĭ�Ͽ�ȡ�Ĭ�ϸ߶�
			BluetoothPrintDriver.AddInverse((byte)0x00);
			BluetoothPrintDriver.AddAlignMode((byte) 0);//����
			BluetoothPrintDriver.ImportData(data,true);
			/*------------------------ TTC --------------------------*/
			String data2 = getResources().getString(R.string.total_ttc);
			double ttc = vx.getTtc();

			int espace2 = 32 - (df.format(ttc).length() + data2.length() + 3);

			for (int j = 0; j < espace2; j++) {
				data2 = data2 + " ";
			}
			data2 = data2 + df.format(ttc) + " DH";
			BluetoothPrintDriver.SetZoom((byte)0x00);//Ĭ�Ͽ�ȡ�Ĭ�ϸ߶�
			BluetoothPrintDriver.AddInverse((byte)0x01);
			BluetoothPrintDriver.AddAlignMode((byte) 0);//����
			BluetoothPrintDriver.ImportData(data2,true);
			
			
			/*------------------------ TVA---------------------------*/

			String data3 = "TVA (20%)";
			double tva = vx.getTva();

			int espace3 = 32 - (df.format(tva).length() + data3.length() + 3);

			for (int j = 0; j < espace3; j++) {
				data3 = data3 + " ";
			}

			data3 = data3 + df.format(tva) + " DH";

			BluetoothPrintDriver.SetZoom((byte)0x00);//Ĭ�Ͽ�ȡ�Ĭ�ϸ߶�
			BluetoothPrintDriver.AddInverse((byte)0x00);
			BluetoothPrintDriver.AddAlignMode((byte) 0);//����
			BluetoothPrintDriver.ImportData(data3,true);

			/*------------------------ LIGNE --------------------------*/

			BluetoothPrintDriver.SetZoom((byte)0x00);//Ĭ�Ͽ�ȡ�Ĭ�ϸ߶�
			BluetoothPrintDriver.AddInverse((byte)0x00);
			BluetoothPrintDriver.ImportData(ticket.getLine());
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();


			/*------------------------ LIGNE --------------------------*/
			BluetoothPrintDriver.SetZoom((byte)0x00);//Ĭ�Ͽ�ȡ�Ĭ�ϸ߶�
			BluetoothPrintDriver.AddInverse((byte)0x00);
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();

			if (remises.size()>0) {

				/*************************** List Promotion *****************************************/
				/*-------------------------Header Table --------------------------------------------*/

				BluetoothPrintDriver.SetZoom((byte)0x00);//Ĭ�Ͽ�ȡ�Ĭ�ϸ߶�
				BluetoothPrintDriver.AddInverse((byte)0x01);
				BluetoothPrintDriver.AddAlignMode((byte) 0);//����
				String data6 = getResources().getString(R.string.promotion_list);
				int espace6 = 32 - data6.length();
				for (int j = 0; j < espace6; j++) {
					data6 = data6 + " ";
				}
				BluetoothPrintDriver.ImportData(data6,true);

				String dataX = "R�duction sur les produits :";
				int espaceX = 33 - dataX.length();
				for (int j = 0; j < espaceX; j++) {
					dataX = dataX + " ";
				}
				int X = 0;
				
				/********************** Content Table *******************************/
				for (int i = 0; i < remises.size(); i++) {
					BluetoothPrintDriver.AddInverse((byte)0x00);
					BluetoothPrintDriver.SetZoom((byte)0x00);//���ߣ�����
					BluetoothPrintDriver.AddBold((byte)0x00);//����
					BluetoothPrintDriver.AddAlignMode((byte) 0x01);//����

					PromoTicket prom = new PromoTicket();
					prom = remises.get(i);

					if (prom.getType() == 1) {
						String data7 = getResources().getString(R.string.produit_offert);
						int espace7 = 33 - data7.length();
						for (int j = 0; j < espace7; j++) {
							data7 = data7 + " ";
						}
						BluetoothPrintDriver.ImportData(data7,true);
						String data8 = prom.getValue()+"->"+prom.getDesig();
						BluetoothPrintDriver.ImportData(removeDiacritic(data8),true);
					}


					BluetoothPrintDriver.excute();
					BluetoothPrintDriver.ClearData();

					if(i == (remises.size() - 1)){
						BluetoothPrintDriver.LF();
						BluetoothPrintDriver.ImportData(ticket.getLine());
						BluetoothPrintDriver.LF();
						BluetoothPrintDriver.excute();
						BluetoothPrintDriver.ClearData();
					}else{
						BluetoothPrintDriver.LF();
						BluetoothPrintDriver.excute();
						BluetoothPrintDriver.ClearData();
						BluetoothPrintDriver.LF();
						BluetoothPrintDriver.excute();
						BluetoothPrintDriver.ClearData();
					}

				}
				/*------------------------ LIGNE --------------------------*/
				/*
				BluetoothPrintDriver.LF();
				BluetoothPrintDriver.excute();
				BluetoothPrintDriver.ClearData();
			
				 */
				BluetoothPrintDriver.LF();
				BluetoothPrintDriver.excute();
				BluetoothPrintDriver.ClearData();
				BluetoothPrintDriver.LF();
				BluetoothPrintDriver.excute();
				BluetoothPrintDriver.ClearData();
				}
			
			
			/*-------------------------- les des reductions ----------------------------*/
			if (reduction.size()>0) {

				/*************************** List Promotion *****************************************/
				/*-------------------------Header Table --------------------------------------------*/

				BluetoothPrintDriver.SetZoom((byte)0x00);//Ĭ�Ͽ�ȡ�Ĭ�ϸ߶�
				BluetoothPrintDriver.AddInverse((byte)0x01);
				BluetoothPrintDriver.AddAlignMode((byte) 0);//����
				String data6 = "Remise sur les produits";
				int espace6 = 32 - data6.length();
				for (int j = 0; j < espace6; j++) {
					data6 = data6 + " ";
				}
				BluetoothPrintDriver.ImportData(data6,true);

				char c= '\u0025';
				/********************** Content Table *******************************/
				for (int i = 0; i < reduction.size(); i++) {
					BluetoothPrintDriver.AddInverse((byte)0x00);
					BluetoothPrintDriver.SetZoom((byte)0x00);//���ߣ�����
					BluetoothPrintDriver.AddBold((byte)0x00);//����
					BluetoothPrintDriver.AddAlignMode((byte) 0x01);//����

					PromoTicket prom = new PromoTicket();
					prom = reduction.get(i);

					String n = prom.getValue();
					
					String data8 = prom.getQte()+"->"+prom.getDesig()+" ("+prom.getPorcentage()+c+") ==> "+n.substring(0, n.indexOf(".")+3);
					BluetoothPrintDriver.ImportData(removeDiacritic(data8),true);


					BluetoothPrintDriver.excute();
					BluetoothPrintDriver.ClearData();

					if(i == (reduction.size() - 1)){
						BluetoothPrintDriver.LF();
						BluetoothPrintDriver.ImportData(ticket.getLine());
						BluetoothPrintDriver.LF();
						BluetoothPrintDriver.excute();
						BluetoothPrintDriver.ClearData();
					}else{
						BluetoothPrintDriver.LF();
						BluetoothPrintDriver.excute();
						BluetoothPrintDriver.ClearData();
						BluetoothPrintDriver.LF();
						BluetoothPrintDriver.excute();
						BluetoothPrintDriver.ClearData();
					}

				}
				/*------------------------ LIGNE --------------------------*/
				/*
				BluetoothPrintDriver.LF();
				BluetoothPrintDriver.excute();
				BluetoothPrintDriver.ClearData();
			
				 */
				BluetoothPrintDriver.LF();
				BluetoothPrintDriver.excute();
				BluetoothPrintDriver.ClearData();
				BluetoothPrintDriver.LF();
				BluetoothPrintDriver.excute();
				BluetoothPrintDriver.ClearData();
				}


			/************************ BAR CODE **************************************************/
			BluetoothPrintDriver.SetCharacterFont((byte) 0x00);
			String print1DBarcodeStr = "";
			String tmp ="";
			if (ticket.getNumFacture().substring(2).length() > 7) {
				int p = ticket.getNumFacture().indexOf("-");
				tmp = ticket.getNumFacture().substring(p);
				if(tmp.length() >  6 && tmp.length() == 11){
					print1DBarcodeStr = "-"+tmp.substring(6);
					tmp = ticket.getNumFacture().substring(p+1);
				}else{
					tmp = ticket.getNumFacture().substring(p+1);
					print1DBarcodeStr = "-"+tmp;
				}
			}else{
				print1DBarcodeStr = ticket.getNumFacture().substring(2);
				tmp = print1DBarcodeStr;
			}

			//String print1DBarcodeStr = ticket.getNumFacture().substring(2);
			//int len = print1DBarcodeStr.length();

			//    	for(int i=0; i<len; i++){
			//    		if(print1DBarcodeStr.charAt(i)<'0' || print1DBarcodeStr.charAt(i)>'9'){
			//    			//Utils.ShowMessage(PrinterOptionActivity.this, "�����ַ�ֻ����0��9λ֮�������!");
			//    			String tmpString = PrinterOptionActivity.this.getResources().getString(R.string.barcode_input_hint);
			//    			Utils.ShowMessage(PrinterOptionActivity.this, tmpString);
			//        		return;
			//        	}
			//    	}
			//BluetoothPrintDriver.AddCodePrint(BluetoothPrintDriver.Code128_B, print1DBarcodeStr);
			BluetoothPrintDriver.AddAlignMode((byte) 0x01);//����
			BluetoothPrintDriver.Code128_B(tmp);//print1DBarcodeStr
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();


			String val= " ";
			for (int i = 0; i < print1DBarcodeStr.length(); i++) {
				if(i < print1DBarcodeStr.length() -1 )
					val = val + print1DBarcodeStr.charAt(i) + "    ";
				else  val = val + print1DBarcodeStr.charAt(i);
			}

			BluetoothPrintDriver.AddBold((byte)0x00);//����
			BluetoothPrintDriver.AddAlignMode((byte) 0x01);//����
			BluetoothPrintDriver.ImportData(val);
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();

			/*------------------------ LIGNE --------------------------*/
			BluetoothPrintDriver.SetZoom((byte)0x00);//Ĭ�Ͽ�ȡ�Ĭ�ϸ߶�
			BluetoothPrintDriver.AddInverse((byte)0x00);
			BluetoothPrintDriver.ImportData(ticket.getLine());
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();

			/************************ FOOTER *******************************/
			BluetoothPrintDriver.AddBold((byte)0x00);//����
			BluetoothPrintDriver.AddAlignMode((byte) 0x01);//����
			BluetoothPrintDriver.SetCharacterFont((byte) 0x031);
			BluetoothPrintDriver.ImportData(removeDiacritic(ticket.getDescription()));
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();

			BluetoothPrintDriver.AddBold((byte)0x00);//����
			BluetoothPrintDriver.AddAlignMode((byte) 0x01);//����
			BluetoothPrintDriver.ImportData("Tel : "+ticket.getTel());
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();

			BluetoothPrintDriver.AddBold((byte)0x00);//����
			BluetoothPrintDriver.AddAlignMode((byte) 0x01);//����
			BluetoothPrintDriver.ImportData("Fax : "+ticket.getFax());
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();

			BluetoothPrintDriver.AddBold((byte)0x00);//����
			BluetoothPrintDriver.AddAlignMode((byte) 0x01);//����
			BluetoothPrintDriver.ImportData("I.F:"+ticket.getIF()+"  Patente:"+ticket.getPatente());
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();

			BluetoothPrintDriver.AddBold((byte)0x00);//����
			BluetoothPrintDriver.AddAlignMode((byte) 0x01);//����
			BluetoothPrintDriver.ImportData(removeDiacritic(ticket.getMsg()));
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();

			BluetoothPrintDriver.AddBold((byte)0x00);//����
			BluetoothPrintDriver.AddAlignMode((byte) 0x01);//����
			BluetoothPrintDriver.ImportData(ticket.getSiteWeb());
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();

			/************************ Ligne pointier pour La Fin ****************************/
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();

			BluetoothPrintDriver.AddAlignMode((byte) 1);//����
			BluetoothPrintDriver.ImportData(ticket.getLine());
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();


			for (int i = 0; i < 23; i++) {
				BluetoothPrintDriver.LF();
				BluetoothPrintDriver.excute();
				BluetoothPrintDriver.ClearData();
			}

			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();

			/*
			String powered = "JobService By GEO.COM";
			BluetoothPrintDriver.AddAlignMode((byte)1);//����
			BluetoothPrintDriver.SetLineSpace((byte)20);	
			BluetoothPrintDriver.SetZoom((byte)0x11);//���ߣ�����	
			//BluetoothPrintDriver.ImportData(new byte[]{0x1d,0x21,0x01}, 3);	//���ñ���
			BluetoothPrintDriver.ImportData(powered,true);
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();

			String web = "www.marocgeo.ma";
			BluetoothPrintDriver.AddAlignMode((byte)1);//����
			BluetoothPrintDriver.SetLineSpace((byte)20);	
			BluetoothPrintDriver.SetZoom((byte)0x11);//���ߣ�����	
			//BluetoothPrintDriver.ImportData(new byte[]{0x1d,0x21,0x01}, 3);	//���ñ���
			BluetoothPrintDriver.ImportData(web,true);
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();
			 */

			/*
			BluetoothPrintDriver.AddBold((byte)0x00);//����
			BluetoothPrintDriver.SetZoom((byte)0x00);//Ĭ�Ͽ�ȡ�Ĭ�ϸ߶�
			BluetoothPrintDriver.AddAlignMode((byte) 1);//����
			BluetoothPrintDriver.ImportData(ticket.getLine());
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.LF();
			BluetoothPrintDriver.excute();
			BluetoothPrintDriver.ClearData();
			
			*/
			
			
			//Log.e("tickets ",myoffline.LoadBluetooth("").toString());
	
	}else{
			onStart();
		}
		
	}


	public String main(String data) {
		int n = data.length()/16;
		int nr =data.length() - (n*16);
		List<String> tb = new ArrayList<String>();

		int k = 0;
		int m = 16;
		//System.out.println("lenght "+data.length()+" et div "+n);
		for (int i = 0; i < n; i++) {
			tb.add(data.substring(k, m));
			//System.out.println(data.substring(k, m-1));
			k = m;
			m = m+16;
			//System.out.println("on i "+i+ " k :"+k+" & m :"+m);
		}
		if(nr != 0){
			tb.add(data.substring(k, data.length()-1));
		}
		int s=0;

		String mt = "";
		for(String st:tb){
			//System.out.println("voila res "+st);
			int kl = st.length();
			int dif = 32 - kl;
			String sb = "";
			for (int i = 0; i < dif; i++) {
				sb = sb + " ";
			}
			st = st + sb;
			System.out.println(st);
			mt = mt + st;
		}
		return mt;
	}

	private String stringtoascci(String mString) {
		StringBuilder result = new StringBuilder();
		for (byte c : mString.getBytes())
			result.append(String.valueOf(c));
		String test = result.toString();
		return test;
	}

	public static String removeDiacritic(String source) {
		char[] vysl = new char[source.length()];
		char one;
		for (int i = 0; i < source.length(); i++) {
			one = source.charAt(i);
			if (one >= '\u00c0' && one <= '\u017f') {
				one = tab00c0.charAt((int) one - '\u00c0');
			}
			vysl[i] = one;
		}
		return new String(vysl);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.e("data keyup","is in");
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			/*
			Intent intent4 = new Intent(TicketActivity.this, VendeurActivity.class);
			intent4.putExtra("user", compte);
			startActivity(intent4);
			
			*/
			Intent intent1 = new Intent(CommandeViewTicketActivity.this, ConnexionActivity.class);
			intent1.putExtra("user", compte);
			intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent1);
			return true;
		}
		return false;
	}
	
	public void remplire(){
		if(produits != null){
			remises = new ArrayList<>();
			reduction = new ArrayList<>();
			prd = new ArrayList<>();
			
			List<Produit> dem = produits.get("prod");
			List<Produit> prm = produits.get("promo");
			
			ticket.setPrds(prd);
			
			
			
				for (int i = 0; i < dem.size(); i++) {
					Produit p = dem.get(i);
					
					Double prix = p.getPrixttc();

					int tva = Integer.parseInt(p.getTva_tx());
					
					ProduitTicket prod = new ProduitTicket(p.getQteDispo(), removeDiacritic(p.getDesig()),prix , tva );
					prd.add(prod);
				}

				for (int i = 0; i < prm.size(); i++) {

					Produit p = prm.get(i);
					
					if(Double.parseDouble(p.getTva_tx()) == 100){
						PromoTicket promo = new PromoTicket(p.getId(), p.getDesig(), p.getQteDispo(), 1, p.getQteDispo()+"");
						remises.add(promo);
					}else{
						double a = p.getPrixttc() * p.getQteDispo();
						double t = (a *  Double.parseDouble(p.getTva_tx())) / 100D;
						double h = a - t;
						PromoTicket promo = new PromoTicket(p.getId(), p.getRef(), p.getQteDispo(), 0, h+" DH");
						promo.setPorcentage(p.getTva_tx());
						reduction.add(promo);
					}
				}


				ticket.setPrds(prd);
			
		}
		
	}
}
