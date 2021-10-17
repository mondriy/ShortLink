package com.example.shortlink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shortlink.db.UrlDB;
import com.example.shortlink.model.Url;
import com.example.shortlink.network.Get;
import com.example.shortlink.network.NetworkService;
import com.example.shortlink.network.Post;
import com.example.shortlink.recycleView.RecycleView;
import com.example.shortlink.recycleView.SimpleItemTouchHelperCallback;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements RecycleView.OnNoteListener {

    private Intent intent;
    private Intent shareIntent;
    private Post post;

    private Context context;

    private UrlDB urlDB;
    private SQLiteDatabase database;
    private ContentValues contentValues;

    private Button toShort;
    private FloatingActionButton toQr;
    private TextInputLayout editUrlLayout;
    private TextInputEditText editUrl;

    private ArrayList<String> arrOriginalUrl = new ArrayList<>();
    private ArrayList<String> arrShortUrl = new ArrayList<>();

    private RelativeLayout relativeLayout;
    private RecyclerView recyclerView;
    private TextView emptyView;

    private RecycleView rw;

    private ImageView relativeLayoutBG;

    private FrameLayout blackout;

    private AnimatorSet animationSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        intent = new Intent(this, QRActivity.class);

        toShort = (Button) findViewById(R.id.containedButton);
        toQr = (FloatingActionButton) findViewById(R.id.qr_button);
        editUrlLayout = (TextInputLayout) findViewById(R.id.outlinedTextField);
        editUrl = (TextInputEditText) findViewById(R.id.textField);

        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        emptyView = (TextView) findViewById(R.id.empty_view);

        relativeLayout = (RelativeLayout) findViewById(R.id.bottomHistory);
        relativeLayoutBG = (ImageView) findViewById(R.id.relativeLayoutBG);
        blackout = (FrameLayout) findViewById(R.id.blackout);

        post = new Post();

        urlDB = new UrlDB(this);
        database = urlDB.getWritableDatabase();
        contentValues = new ContentValues();

        readDB();

        rw = new RecycleView(this, arrOriginalUrl, arrShortUrl, this);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(rw);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(rw);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) relativeLayout);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (BottomSheetBehavior.STATE_DRAGGING == newState) {
                    if (rw.getItemCount() == 0) {
                        recyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    else {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }
                    animationSet = new AnimatorSet();
                    blackout.setVisibility(View.VISIBLE);
                    ObjectAnimator animBG = ObjectAnimator.ofFloat(relativeLayoutBG,"alpha",1f);
                    ObjectAnimator anim = ObjectAnimator.ofFloat(blackout,"alpha",0.5f);
                    animationSet.playTogether(animBG, anim);
                    animationSet.start();
                }
                if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                    animationSet = new AnimatorSet();
                    ObjectAnimator animBG = ObjectAnimator.ofFloat(relativeLayoutBG,"alpha",0f);
                    ObjectAnimator anim = ObjectAnimator.ofFloat(blackout,"alpha",0f);
                    animationSet.playTogether(animBG, anim);
                    animationSet.start();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        editUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editUrlLayout.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        toShort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlToShort = editUrlLayout.getEditText().getText().toString();
                Url.setOriginalUrl(urlToShort);
                if (URLUtil.isHttpUrl(urlToShort) || URLUtil.isHttpsUrl(urlToShort)) {
                    if (!searchDB(urlToShort)) {
                        post.setUrl(urlToShort);
                        openConnection(post);
                    }
                    else {
                        editUrlLayout.setError("URL существует!");
                    }
                }
                else {
                    editUrlLayout.setError("Неверный URL!");
                }
            }
        });

        toQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openQRActivity();
            }
        });

        shareIntent = getIntent();
        String action = shareIntent.getAction();
        String type = shareIntent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = shareIntent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    editUrl.setText(sharedText);
                    toShort.performClick();
                }
            }
        }

    }

    public void openQRActivity() {
        startActivity(intent);
    }
 
    public void openConnection(Post post) {
        NetworkService.getInstance()
                .getSHApi()
                .postData(post)
                .enqueue(new Callback<Get>() {
                    @Override
                    public void onResponse(@NonNull Call<Get> call, @NonNull Response<Get> response) {
                        Get get = response.body();
                        Url.setUrl("slik.cf/" + get.getShortLink());

                        ClipboardManager clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("", Url.getUrl());
                        clipboard.setPrimaryClip(clip);

                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Ссылка скопирована!", Toast.LENGTH_SHORT);
                        toast.show();
                        editUrlLayout.setError("");
                        writeDB();
                        readDB();
                        toQr.setVisibility(View.VISIBLE);
                        Animation anim = new ScaleAnimation(
                                0.01f, 1f,
                                0.01f, 1f,
                                Animation.RELATIVE_TO_SELF, 0.5f,
                                Animation.RELATIVE_TO_SELF, 0.5f);
                        anim.setFillAfter(true);
                        anim.setInterpolator(new LinearOutSlowInInterpolator());
                        anim.setDuration(300);
                        toQr.startAnimation(anim);
                        rw.notifyDataSetChanged();
                        openQRActivity();
                    }
                    @Override
                    public void onFailure(@NonNull Call<Get> call, @NonNull Throwable t) {
                        editUrlLayout.setError("Проверьте подключение к сети!");
                    }
                });
    }

    public void writeDB() {
        contentValues.put(urlDB.KEY_ORIGINAL_URL, Url.getOriginalUrl());
        contentValues.put(urlDB.KEY_SHORT_URL, Url.getUrl());

        database.insert(urlDB.DATABASE_TABLE, null, contentValues);
    }

    public void readDB() {
        Cursor cursor = database.query(urlDB.DATABASE_TABLE, null, null, null, null, null, null);
        arrShortUrl.clear();
        arrOriginalUrl.clear();
        if (cursor.moveToFirst()) {
            int originalUrl = cursor.getColumnIndex(urlDB.KEY_ORIGINAL_URL);
            int shortUrl = cursor.getColumnIndex(urlDB.KEY_SHORT_URL);
            do {
                arrOriginalUrl.add(cursor.getString(originalUrl));
                arrShortUrl.add(cursor.getString(shortUrl));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public boolean searchDB(String parameter) {
        Cursor cursor = database.query(urlDB.DATABASE_TABLE, null, null, null, null, null, null);
        boolean result = false;
        if (cursor.moveToFirst()) {
            int originalUrl = cursor.getColumnIndex(urlDB.KEY_ORIGINAL_URL);
            do {
                if (cursor.getString(originalUrl).equals(parameter)) {
                    result = true;
                }
            } while (cursor.moveToNext());
        }
        return result;
    }

    @Override
    public void onNoteClick(String urlFromHistory, String urlOriginalFromHistory) {

        ClipboardManager clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", urlFromHistory);
        clipboard.setPrimaryClip(clip);

        Url.setUrl(urlFromHistory);
        Url.setOriginalUrl(urlOriginalFromHistory);
        openQRActivity();
    }

}