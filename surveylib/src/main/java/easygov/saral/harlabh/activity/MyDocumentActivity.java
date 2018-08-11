package easygov.saral.harlabh.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.fragments.MyDocumentFragment;

public class MyDocumentActivity extends AppCompatActivity {

    private RelativeLayout rlContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_document);
        rlContainer= findViewById(R.id.rlContainer);
        getSupportFragmentManager().beginTransaction().add(R.id.rlContainer, new MyDocumentFragment())
                .commit();


        /*rvDocuments=findViewById(R.id.rvDocuments);
        ivChangeViewType=findViewById(R.id.ivChangeViewType);
        ivDocumentBack=findViewById(R.id.ivDocumentBack);
        ivChangeViewType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isListView=!isListView;
                setRelativeLayout();
            }
        });
        ivDocumentBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        setRelativeLayout();
        hitApiGetDocument();
    */}

/*
    private void setRelativeLayout() {

        if(isListView)
        {
            rvDocuments.setLayoutManager(new LinearLayoutManager(this));
            ivChangeViewType.setImageResource(R.drawable.grid);
            rvDocuments.setAdapter(new  ListViewDocumentAdapter(this,adapterData));
        }
        else
        {
            rvDocuments.setLayoutManager(new GridLayoutManager(this,2));
            ivChangeViewType.setImageResource(R.drawable.list);
        }
    }

    private void hitApiGetDocument() {
        GeneralFunctions.showDialog(this);
        RestClient.get().readDocsForUserByFieldID().enqueue(new Callback<PojoDocument>() {
            @Override
            public void onResponse(Call<PojoDocument> call, Response<PojoDocument> response) {

                if(response.isSuccessful())
                {
                    if(response.body().code.equals("401"))
                    {
                        Intent intent = new Intent(MyDocumentActivity.this,
                                SplashActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        GeneralFunctions.clearPrefs(Prefs.with(MyDocumentActivity.this));
                        startActivity(intent);
                        Toast.makeText(MyDocumentActivity.this, "Session Expired! Please login again", Toast.LENGTH_SHORT).show();
                    }

                    if(response.body().success==1)
                    {
                        adapterData.addAll(response.body().data.user_service_docs);
                        rvDocuments.getAdapter().notifyDataSetChanged();
                        setRelativeLayout();
                    }

                    else {
                        Toast.makeText(MyDocumentActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(MyDocumentActivity.this, getResources().getString(R.string.serverIssue), Toast.LENGTH_SHORT).show();
                }
                GeneralFunctions.dismissDialog();
            }

            @Override
            public void onFailure(Call<PojoDocument> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rvDocuments,getResources().getString(R.string.netIssue));

            }
        });

    }
*/


}
