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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.liuzh.readinghabit.R;
import com.liuzh.readinghabit.bean.one.OneDay;
import com.liuzh.readinghabit.bean.read.ReadData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by 刘晓彬.
 * @date on 2017/4/22.
 * <p>
 * 没有写不出来的bug,只有不努力的码农
 */

public class CollectDialog extends Dialog {

    private RecyclerView mRvOne;
    private RecyclerView mRvRead;

    private TextView mTvOneNoMsg;
    private TextView mTvReadNoMsg;

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

        initWindow();

        mTvOneNoMsg = findViewById(R.id.tv_oneNoMsg);
        mTvReadNoMsg = findViewById(R.id.tv_readNoMsg);

        mProgressBar =  findViewById(R.id.progressBar);
        mRvOne = findViewById(R.id.rv_one);
        mRvRead =  findViewById(R.id.rv_read);

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
                TextView tv = holder.itemView.findViewById(R.id.tv_title);
                tv.setText(mOneList.get(position).hp_content);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOneListener.onOneCLick(mOneList.get(holder.getLayoutPosition()));
                    }
                });
                holder.itemView.findViewById(R.id.tv_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOneListener.onOneDelete(mOneList.get(holder.getLayoutPosition()));
                        mOneList.remove(holder.getLayoutPosition());
                        mOneAdapter.notifyDataSetChanged();
                        if (mOneList.size() == 0) {
                            oneNoMsg(true);
                        }
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
                TextView tv = holder.itemView.findViewById(R.id.tv_title);
                tv.setText(mReadList.get(position).title);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mReadListener.onReadClick(mReadList.get(holder.getLayoutPosition()));
                    }
                });
                holder.itemView.findViewById(R.id.tv_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mReadListener.onReadDelete(mReadList.get(holder.getLayoutPosition()));
                        mReadList.remove(holder.getLayoutPosition());
                        mReadAdapter.notifyDataSetChanged();
                        if (mReadList.size() == 0) {
                            readNoMsg(true);
                        }
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

    private void initWindow() {
        Window window = getWindow();
        if (window == null) {
            return;
        }

        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(attributes);
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

        void onReadDelete(ReadData read);
    }

    public void setOnReadClickListener(OnReadClickListener listener) {
        mReadListener = listener;
    }


    public interface OnOneClickListener {
        void onOneCLick(OneDay one);

        void onOneDelete(OneDay one);
    }

    public void setOnOneClickListener(OnOneClickListener listener) {
        mOneListener = listener;
    }

    public void oneNoMsg(boolean isNoMsg) {
        if (isNoMsg) {
            mRvOne.setVisibility(View.GONE);
            mTvOneNoMsg.setVisibility(View.VISIBLE);
        } else {
            mTvOneNoMsg.setVisibility(View.GONE);
            mRvOne.setVisibility(View.VISIBLE);
        }
    }

    public void readNoMsg(boolean isNoMsg) {
        if (isNoMsg) {
            mRvRead.setVisibility(View.GONE);
            mTvReadNoMsg.setVisibility(View.VISIBLE);
        } else {
            mRvRead.setVisibility(View.VISIBLE);
            mTvReadNoMsg.setVisibility(View.GONE);
        }
    }

}
