package easygov.saral.harlabh.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.adapters.StatusAdapter;
import easygov.saral.harlabh.models.responsemodels.statusmodel.StatusResponse;

/**
 * Created by apoorv on 11/12/17.
 */

public class StatusReportActivity extends AppCompatActivity {
   private RecyclerView rvStatus;

   private StatusResponse status;
    private ImageView ivStatusBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_status);
        init();
    }

    private void init() {
        rvStatus= findViewById(R.id.rvStatus);
        rvStatus.setLayoutManager(new LinearLayoutManager(this));
        ivStatusBack= findViewById(R.id.ivStatusBack);
        Bundle bundle=new Bundle();
        bundle=getIntent().getExtras();
        if(bundle!=null)
        {
           status= new Gson().fromJson(bundle.getString("statuslist"),new TypeToken<StatusResponse>() {}.getType());
            rvStatus.setAdapter(new StatusAdapter(this,status.data.objects));
        }

        ivStatusBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



}
