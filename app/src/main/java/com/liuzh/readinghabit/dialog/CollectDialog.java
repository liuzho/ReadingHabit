package com.liuzh.readinghabit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.liuzh.readinghabit.R;
import com.liuzh.readinghabit.bean.one.OneDay;
import com.liuzh.readinghabit.bean.read.ReadData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 刘晓彬 on 2017/4/22.
 */

public class CollectDialog extends Dialog {

    private RecyclerView mRvOne;
    private RecyclerView mRvRead;

    private ProgressBar mProgressBar;

    private List<OneDay> mOneList = new ArrayList<>();
    private List<ReadData> mReadList = new ArrayList<>();

    private RecyclerView.Adapter mReadAdapter;
    private RecyclerView.Adapter mOneAdapter;

    private OnReadClickListener mReadListener;
    private OnOneClickListener mOneListener;


    public CollectDialog(@NonNull Context context) {
        super(context);
    }

    public void setData(List<OneDay> oneList, List<ReadData> readList) {
        mOneList = oneList;
        mReadList = readList;
        mReadAdapter.notifyDataSetChanged();
        mOneAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_collect);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mRvOne = (RecyclerView) findViewById(R.id.rv_one);
        mRvRead = (RecyclerView) findViewById(R.id.rv_read);

        mRvOne.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvRead.setLayoutManager(new LinearLayoutManager(getContext()));

        mOneAdapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                return new RecyclerView.ViewHolder(inflater.inflate(
                        R.layout.item_lv_like, parent, false)) {
                };
            }

            @Override
            public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
                TextView tv = (TextView) holder.itemView.findViewById(R.id.tv_title);
                tv.setText(mOneList.get(position).hp_content);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOneListener.onOneCLick(mOneList.get(holder.getLayoutPosition()));
                    }
                });
            }

            @Override
            public int getItemCount() {
                return mOneList.size();
            }
        };
        mRvOne.setAdapter(mOneAdapter);


        mReadAdapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                return new RecyclerView.ViewHolder(inflater.inflate(
                        R.layout.item_lv_like, parent, false)) {
                };
            }

            @Override
            public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
                TextView tv = (TextView) holder.itemView.findViewById(R.id.tv_title);
                tv.setText(mReadList.get(position).title);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mReadListener.onReadClick(mReadList.get(holder.getLayoutPosition()));
                    }
                });
            }

            @Override
            public int getItemCount() {
                return mReadList.size();
            }
        };
        mRvRead.setAdapter(mReadAdapter);

    }

    public void showProgress(boolean needShow) {
        if (needShow) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    public interface OnReadClickListener {
        void onReadClick(ReadData read);
    }

    public void setOnReadClickListener(OnReadClickListener listener) {
        mReadListener = listener;
    }


    public interface OnOneClickListener {
        void onOneCLick(OneDay one);
    }

    public void setOnOneClickListener(OnOneClickListener listener) {
        mOneListener = listener;
    }

}
