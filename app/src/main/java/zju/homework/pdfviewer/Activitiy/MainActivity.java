package zju.homework.pdfviewer.Activitiy;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pspdfkit.configuration.PSPDFConfiguration;
import com.pspdfkit.configuration.page.PageScrollDirection;
import com.pspdfkit.ui.PSPDFFragment;

import zju.homework.pdfviewer.BuildConfig;
import zju.homework.pdfviewer.R;
import zju.homework.pdfviewer.Utils.Util;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Util.showOpenFileDialog(MainActivity.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == Util.REQUEST_OPEN_DOCUMENT ){
            if( resultCode == Activity.RESULT_OK && data != null){
                final Uri uri = data.getData();
                Intent intent = new Intent(MainActivity.this, PDFViewActivity.class);
                intent.putExtra(PDFViewActivity.EXTRA_URI, uri);
                this.startActivity(intent);
            }
        }

    }


}
