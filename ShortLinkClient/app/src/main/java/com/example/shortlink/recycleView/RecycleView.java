package com.example.shortlink.recycleView;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.shortlink.R;
import com.example.shortlink.db.UrlDB;

import java.util.ArrayList;

public class RecycleView extends RecyclerView.Adapter<RecycleView.ViewHolder> implements ItemTouchHelperAdapter {

    private ArrayList<String> originalUrl;
    private ArrayList<String> shortUrl;
    private OnNoteListener mOnNoteListener;
    private Context context;

    private UrlDB urlDB;
    private SQLiteDatabase database;



    public RecycleView(Context context, ArrayList<String> originalUrl, ArrayList<String> shortUrl, OnNoteListener onNoteListener) {
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.mOnNoteListener = onNoteListener;
        this.context = context;
    }

    @Override
    public RecycleView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_url, parent, false);
        return new ViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.originalUrl.setText(originalUrl.get(position));
        holder.shortUrl.setText(shortUrl.get(position));
    }

    @Override
    public int getItemCount() {
        return shortUrl.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView originalUrl;
        TextView shortUrl;

        OnNoteListener onNoteListener;

        public ViewHolder(View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            originalUrl = itemView.findViewById(R.id.originalUrl);
            shortUrl = itemView.findViewById(R.id.shortUrl);
            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(shortUrl.getText().toString(), originalUrl.getText().toString());
        }
    }

    public interface OnNoteListener {
        void onNoteClick(String urlFromHistory, String urlOriginalFromHistory);
    }

    @Override
    public void onItemDismiss(int position) {
        urlDB = new UrlDB(context);
        database = urlDB.getWritableDatabase();
        database.delete(urlDB.DATABASE_TABLE, "originalUrl =  '" + originalUrl.get(position) + "'", null);
        originalUrl.remove(position);
        shortUrl.remove(position);
        notifyItemRemoved(position);
    }

}