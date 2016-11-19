package com.ramkt.sectionrecylerview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ramkt.sectionrecylerview.model.Channel;
import com.ramkt.sectionrecylerview.R;
import com.ramkt.sectionrecylerview.Section;
import com.ramkt.sectionrecylerview.SectionListener;

import java.util.List;

/**
 * Created by Ram_Thirupathy on 10/17/2016.
 */
public class ChannelAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Section<Channel>> mChannels;
    private SectionListener mHelper;
    private static final int VIEW_HEADER = 0;
    private static final int VIEW_NORMAL = 1;

    public ChannelAdapter(Context context, List<Section<Channel>> channels, SectionListener helper) {
        this.mContext = context;
        this.mChannels = channels;
        this.mHelper = helper;
    }


    @Override
    public int getItemViewType(int position) {
        return mChannels.get(position).isHeader() ? VIEW_HEADER : VIEW_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_section_header, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == VIEW_NORMAL) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_section_view, parent, false);
            return new SectionViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ChannelAdapter.HeaderViewHolder) {
            final String name = mChannels.get(position).getDay();
            HeaderViewHolder vh = (HeaderViewHolder) holder;
            vh.tvChannelName.setText(name);
            vh.rlHeaderLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mHelper.moveSectionToTop(position,0);
                }
            });
        }else{
            final String name = mChannels.get(position).getItem().getName();
            SectionViewHolder vh = (SectionViewHolder) holder;
            vh.tvProgramName.setText(name);
        }
    }

    @Override
    public int getItemCount() {
        return mChannels.size();
    }

    class SectionViewHolder extends RecyclerView.ViewHolder {
        TextView tvProgramName;
        public SectionViewHolder(View itemView) {
            super(itemView);
            tvProgramName = (TextView) itemView.findViewById(R.id.tv_program_name);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvChannelName;
        RelativeLayout rlHeaderLayout;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            tvChannelName = (TextView) itemView.findViewById(R.id.tv_header_section_name);
            rlHeaderLayout = (RelativeLayout) itemView.findViewById(R.id.rl_header_layout);
        }
    }
}
