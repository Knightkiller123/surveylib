package easygov.saral.harlabh.activity;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.models.responsemodels.kycmodel.Kyc;
import easygov.saral.harlabh.models.responsemodels.kycmodel.Poa;
import easygov.saral.harlabh.models.responsemodels.kycmodel.Poi;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.Prefs;
import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;

/**
 * Created by apoorv on 06-02-2017.
 */
public class QrReadActivity extends AppCompatActivity {
    private TextView text;

    // QREader
    private SurfaceView mySurfaceView;
    private QREader qrEader;
    DocumentBuilderFactory factory;
    DocumentBuilder builder;
    InputStream is;
    Document dom;
    private ImageView ivQrBack;
    private TextView tvNoScan;
    private Prefs mPrefs;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrreader);
        text = findViewById(R.id.code_info);
        tvNoScan = findViewById(R.id.tvNoScan);
        ivQrBack= findViewById(R.id.ivQrBack);
        mPrefs=Prefs.with(this);
        ivQrBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvNoScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mySurfaceView = findViewById(R.id.camera_view);



        qrEader = new QREader.Builder(this, mySurfaceView, new QRDataListener() {
            @Override
            public void onDetected(final String data) {
                //Log.d("QREader", "Value : " + data);
                text.post(new Runnable() {
                    @Override
                    public void run() {

                        Kyc obj=new Kyc();
                        Poi poi=new Poi();
                        Poa poa=new Poa();
                        String s1="<empty>",s2="<empty>",s3="<empty>",s4="<empty>",s5="<empty>",s6="<empty>",s7="<empty>"
                                ,s8="<empty>",s9="<empty>",s10="<empty>",s11="<empty>",s12="<empty>",s13="<empty>",s14="<empty>";
                        String s15="";
                        try {
                            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,100);
                            factory = DocumentBuilderFactory.newInstance();
                            InputStream is = new ByteArrayInputStream(data.getBytes("UTF-8"));
                            builder = factory.newDocumentBuilder();
                            dom = builder.parse(is);
                            NodeList nList = dom.getElementsByTagName("PrintLetterBarcodeData");

                            for (int i=0; i<nList.getLength(); i++) {

                                Node node = nList.item(i);
                                if (node.getNodeType() == Node.ELEMENT_NODE) {
                                    Element element2 = (Element) node;
                                    List<String> items=new ArrayList<String>();


                                    int chk=0;
                                    //s1=nList.item(0).getAttributes();
                                    int len=nList.item(0).getAttributes().getLength();


                                    try {
                                       s1= nList.item(0).getAttributes().getNamedItem("uid").getNodeValue();
                                        obj.aadhaar_id=s1;

                                    }
                                    catch (Exception e)
                                    {
                                        s1="";
                                        obj.aadhaar_id=s1;

                                    }

                                    try {
                                        s2= nList.item(0).getAttributes().getNamedItem("name").getNodeValue();
                                        poi.name=s2;
                                    }
                                    catch (Exception e)
                                    {
                                        s2="";
                                       // obj.poi.name=s2;
                                    }
                                    try {
                                        s3= nList.item(0).getAttributes().getNamedItem("gender").getNodeValue();
                                        poi.gender=s3;
                                    }
                                    catch (Exception e)
                                    {
                                        s3="";
                                       // obj.poi.gender=s3;
                                    }
                                    try {
                                        s4= nList.item(0).getAttributes().getNamedItem("yob").getNodeValue();
                                        poi.yob=s4;
                                    }
                                    catch (Exception e)
                                    {
                                        s4="";
                                       // obj.poi.dob=s4;
                                    }

                                    try {
                                        s5= nList.item(0).getAttributes().getNamedItem("co").getNodeValue();
                                        poa.co=s5;
                                    }
                                    catch (Exception e)
                                    {
                                        s5=" ";
                                       // obj.poa.co=s5;
                                    }

                                    try {
                                        s5= nList.item(0).getAttributes().getNamedItem("gname").getNodeValue();
                                        poa.gname=s5;
                                    }
                                    catch (Exception e)
                                    {
                                        s5=" ";
                                        // obj.poa.co=s5;
                                    }



                                    try {
                                        s6= nList.item(0).getAttributes().getNamedItem("house").getNodeValue();
                                        poa.lc=s6;
                                    }
                                    catch (Exception e)
                                    {
                                        s6=" ";
                                      //  obj.poa.lc=s6;
                                    }
                                    try {
                                        s7= nList.item(0).getAttributes().getNamedItem("street").getNodeValue();
                                        poa.lm=s7;

                                    }
                                    catch (Exception e)
                                    {
                                        s7="";
                                      //  obj.poa.lm=s7;
                                    }
                                    try {
                                        s8= nList.item(0).getAttributes().getNamedItem("vtc").getNodeValue();
                                        poa.vtc=s8;
                                    }
                                    catch (Exception e)
                                    {
                                        s8="";
                                      //  obj.poa.vtc=s8;
                                    }
                                    try {
                                        s9= nList.item(0).getAttributes().getNamedItem("po").getNodeValue();
                                        poa.po=s9;
                                    }
                                    catch (Exception e)
                                    {
                                        s9="";
                                       // obj.poa.po=s9;
                                    }
                                    try {
                                        s10= nList.item(0).getAttributes().getNamedItem("dist").getNodeValue();
                                        poa.dist=s10;
                                    }
                                    catch (Exception e)
                                    {
                                        s10="";
                                      //  obj.poa.dist=s10;
                                    }
                                    try {
                                        s11= nList.item(0).getAttributes().getNamedItem("subdist").getNodeValue();
                                        poa.subdist=s11;
                                    }
                                    catch (Exception e)
                                    {
                                        s11="";
                                       // obj.poa.subdist=s11;
                                    }
                                    try {
                                        s12= nList.item(0).getAttributes().getNamedItem("state").getNodeValue();
                                        poa.state=s12;
                                    }
                                    catch (Exception e)
                                    {
                                        s12="";
                                     //   obj.poa.state=s12;
                                    }
                                    try {
                                        s13= nList.item(0).getAttributes().getNamedItem("pc").getNodeValue();
                                        poa.pc=s13;
                                    }
                                    catch (Exception e)
                                    {
                                        s13="";
                                     //   obj.poa.pc=s13;
                                    }
                                    try {
                                        s14= nList.item(0).getAttributes().getNamedItem("dob").getNodeValue();
                                        poi.dob=s14;
                                    }
                                    catch (Exception e)
                                    {
                                        s14="";
                                     //   obj.poi.dob=s14;
                                    }
                                    try {
                                        s15= nList.item(0).getAttributes().getNamedItem("loc").getNodeValue();
                                        poa.lc=s15;
                                    }
                                    catch (Exception e)
                                    {
                                        s15="";
                                     //   obj.poa.lc=s15;
                                    }




                                }
                            }

                        } catch (ParserConfigurationException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (SAXException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        /*QrData obj=new QrData(s1,s2,s3,s4,s5,s6,s7,s8,s9,s10,s11,s12,s13,s14,s15);
                        Intent intent=new Intent(QrReadActivity.this,KycDetailActivity.class);
                        intent.putExtra("qrdata",new Gson().toJson(obj));*/
                        //startActivity(intent);

                        //finish();
                        obj.poa=poa;
                        obj.poi=poi;
                        mPrefs.save(Constants.qrKyc,"YES");
                        mPrefs.save(Constants.qrData,new Gson().toJson(obj));

                      /*  text.setText(s1+" :"+s2+" :"+s3+" :"+s4+" :"+s5+" :"+s6+" :"+s7+" :"+s8
                                +" :"+s9+" :"+s10+" :"+s11+" :"+s12+" :"+s13+" :"+s14+" :"+s15);*/

                        finish();
                    }
                });
            }
        }).facing(QREader.BACK_CAM)
                .enableAutofocus(true)
                .height(mySurfaceView.getHeight())
                .width(mySurfaceView.getWidth())
                .build();




    }






    /*private static String getValue(String tag, Element element) {
        NodeList n = element.getElementsByTagName(tag);
        return getElementValue(n.item(0));
    }*/
    /*public static final String getElementValue(Node elem) {
        Node child;
        if( elem != null){
            if (elem.hasChildNodes()){
                for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
                    if( child.getNodeType() == Node.TEXT_NODE  ){
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }*/
    @Override
    protected void onResume() {
        super.onResume();

        // Init and Start with SurfaceView
        // -------------------------------
        qrEader.initAndStart(mySurfaceView);
        //qrReader1.initAndStart(mySurfaceView1);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Cleanup in onPause()
        // --------------------
        qrEader.releaseAndCleanup();
      //  qrReader1.releaseAndCleanup();
    }
}
