package easygov.saral.harlabh.fragments;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;


import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Random;

import easygov.saral.harlabh.R;

/**
 * Created by apoorv on 16/05/18.
 */




public class PdfFragment extends Fragment implements OnPageChangeListener,OnLoadCompleteListener {
    PDFView pdfView;
    Integer pageNumber = 0;
    ImageView iVData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pdf, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pdfView=view.findViewById(R.id.pdfView);
        iVData=view.findViewById(R.id.iVData);
        view.findViewById(R.id.ivVerifyOtpBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        //hitApiGetPdf(view);
        FileOutputStream fos = null;
        try {




            File file;
            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            File myDir = new File(root + "/myGov");
            myDir.mkdirs();
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String fname = "MyGov-" + n + "."+getArguments().getString("fileExtension");
            file = new File(myDir, fname);
            if (file.exists())
                file.delete();

            fos = new FileOutputStream(file);
            fos.write(Base64.decode(getArguments().getString("pdfUrl"), Base64.NO_WRAP));
            fos.close();
            displayFromAsset(file);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //    new DownloadFile().execute(getArguments().getString("pdfUrl"), "maven.png");

    }

    private void displayFromAsset(File assetFileName) {
        if(getArguments().getString("fileExtension").equalsIgnoreCase("pdf")) {
            pdfView.setVisibility(View.VISIBLE);
            iVData.setVisibility(View.GONE);
            pdfView.fromFile(assetFileName)
                    .defaultPage(pageNumber)
                    .enableSwipe(true)

                    .swipeHorizontal(false)
                    .onPageChange(this)
                    .enableAnnotationRendering(true)
                    .onLoad(this)
                    .scrollHandle(new DefaultScrollHandle(getActivity()))
                    .load();
        }
        else{
            pdfView.setVisibility(View.GONE);
            iVData.setVisibility(View.VISIBLE);
            Glide.with(getActivity()).load(Uri.fromFile(assetFileName)).into(iVData);
        }
    }




    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
    }


    @Override    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        printBookmarksTree(pdfView.getTableOfContents(), "-");

    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e("kldfjkds", String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

}

