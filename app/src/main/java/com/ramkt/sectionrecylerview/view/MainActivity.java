package com.ramkt.sectionrecylerview.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.ramkt.sectionrecylerview.R;
import com.ramkt.sectionrecylerview.Section;
import com.ramkt.sectionrecylerview.SectionItem;
import com.ramkt.sectionrecylerview.SectionListener;
import com.ramkt.sectionrecylerview.SectionRecyclerView;
import com.ramkt.sectionrecylerview.StickySectionItem;
import com.ramkt.sectionrecylerview.model.Channel;

import java.util.ArrayList;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity implements SectionListener.onSectionChangeListener {
    SectionRecyclerView mSectionRecyclerView;
    TextView mtvHeader;
    TextView mtvFooter;
    TextView mtvDrag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mtvHeader = (TextView)findViewById(R.id.tv_header_section_name);
        mtvFooter = (TextView)findViewById(R.id.tv_footer_section_name);
        mtvDrag = (TextView)findViewById(R.id.tv_drag_section_name);
        mSectionRecyclerView = new SectionRecyclerView(this, (RecyclerView) findViewById(R.id.rv_list), this, findViewById(R.id.rl_header_layout), findViewById(R.id.rl_footer_layout), findViewById(R.id.fl_clone_view), (RecyclerView) findViewById(R.id.rv_clone_list));
        mSectionRecyclerView.loadRecyclerView(loadChannels());
    }

    private TreeMap<Integer, ArrayList<Section<Channel>>> loadChannels() {
        TreeMap<Integer, ArrayList<Section<Channel>>> sectionMap = new TreeMap<Integer, ArrayList<Section<Channel>>>();
        for (int j = 0; j < 50; j++) {
            StickySectionItem stickySection = new StickySectionItem("Channel " + j, j);
            ArrayList<Section<Channel>> items = new ArrayList<Section<Channel>>();
            items.add(stickySection);
            ArrayList<Channel> ch = new ArrayList<Channel>();
            for (int i = 0; i <= 10; i++) {
                Channel c = new Channel();
                c.setName("Program" + j + " " + i);
                ch.add(c);
            }
            items.addAll(new SectionItem<Channel>().getSectionItems(ch, j));
            sectionMap.put(j, items);
        }
        return sectionMap;
    }


    @Override
    public void onSectionChange(SectionListener.SectionType type, Section section) {
        if (section != null) {
            if (type == SectionListener.SectionType.Header) {
                mtvHeader.setText(section.getDay());
            } else {
                mtvFooter.setText(section.getDay());
            }
        }
    }

    @Override
    public void onDrag(Section section) {
        mtvDrag.setText(section.getDay());
    }
}
