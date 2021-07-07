package com.example.shortlink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shortlink.model.Url;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRActivity extends AppCompatActivity {

    private Intent intent;
    private FloatingActionButton toLink;
    private ImageView ivQR;
    private TextView twSiteName;
    private TextView twShortUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        intent = new Intent(this, MainActivity.class);

        toLink = findViewById(R.id.link_button);
        ivQR = findViewById(R.id.QR_code);
        twSiteName = findViewById(R.id.site_name);
        twShortUrl = findViewById(R.id.short_url);

        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(Url.getUrl(), BarcodeFormat.QR_CODE, 350, 350);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            ivQR.setImageBitmap(bitmap);
        } catch (NullPointerException | WriterException e) {
            e.printStackTrace();
        }

        twSiteName.setText(formatString(Url.getOriginalUrl()));
        twShortUrl.setText(Url.getUrl());

        twShortUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Ссылка скопирована!", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        toLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public String formatString(String s) {
        String newS = "";
        if (URLUtil.isHttpUrl(s)) {
            newS = s.replaceAll("http://", "");
        }
        else if (URLUtil.isHttpsUrl(s)) {
            newS = s.replaceAll("https://", "");
        }
        newS = newS.substring(0, newS.indexOf("/"));
        if (newS.substring(0,4).equals("www.") && newS.length() > 4) {
            newS = newS.substring(4, newS.length() - 1);
        }
        newS = newS.substring(0, newS.indexOf("."));
        return newS;
    }
}