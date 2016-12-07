/**
 * Created by Ram_Thirupathy on 10/18/2016.
 */
package com.ramkt.sectionrecylerview;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.ramkt.sectionrecylerview.adapter.ChannelAdapter;
import com.ramkt.sectionrecylerview.adapter.Divider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * Class extends {@link SectionListener} to perform Sticky section
 */
public class SectionRecyclerView<T extends Section> extends SectionListener {
        private ScrollLayoutManager mScrollLayoutManager;
        private RecyclerView mCloneRecyclerView;
        private onSectionChangeListener mListener;
        private int mHeaderDragHolder;

        public SectionRecyclerView(Context context, RecyclerView recyclerView, onSectionChangeListener listener, View header, View footer, View cloneView, RecyclerView cloneRecyclerView) {
            super(context, recyclerView, header, footer, cloneView);
            this.mListener = listener;
            this.mCloneRecyclerView = cloneRecyclerView;
            setUpRecyclerView();
        }

        /**************************************************************************
         * Abstract methods implementation and other public API to load recycler view with
         * data
         ***************************************************************************/

        @Override
        public void setSection(SectionType type, Section section) {
            if (type == SectionType.Footer && isDragging()) {
                return;
            }
            super.setSection(type, section);
            mListener.onSectionChange(type, section);
        }

        @Override
        public void moveSectionToTop(int index, int offset) {
            mScrollLayoutManager.scrollToPositionWithOffset(index, offset);
            mScrollLayoutManager.initializeStickySection(index, index + mScrollLayoutManager.getVisibleItemCount());
        }

        @Override
        public void moveSectionToTopBySectionPosition(int index) {
            int fromPosition = mScrollLayoutManager.getPositionInList(index);
            moveSectionToTop(fromPosition, 0);
        }

        @Override
        public Section getLastSection() {
            return mScrollLayoutManager.getLastSession();
        }

        @Override
        public void updateCloneSection(SectionType type, Section section) {
            if (type == SectionType.Header) {
                mCloneView.setTranslationY(0);
                mListener.onDrag(section);
                mCloneView.setVisibility(View.VISIBLE);
                mHeaderDragHolder = mScrollLayoutManager.findFirstVisibleItemPosition();
                int topOffset = mRecyclerView.getChildAt(0).getTop();
                List<T> itemsInList = mScrollLayoutManager.getItemsInList(mHeaderDragHolder, mScrollLayoutManager.getVisibleItemCount() + 1);
                loadCloneRecyclerView(itemsInList, topOffset);
            } else {
                updateFooterCloneSection(section);
            }

        }

        public void updateFooterCloneSection(Section section) {
            ArrayList<T> itemsInSection = mScrollLayoutManager.getItemsInSection(section.getSectionPosition(), mScrollLayoutManager.getVisibleItemCount());
            loadCloneRecyclerView(itemsInSection, 0);
            mCloneView.setTranslationY(0);
            mListener.onDrag(section);
            mCloneView.setVisibility(View.VISIBLE);
            section = mScrollLayoutManager.getNextSession(section.getSectionPosition());
            if (section != null) {
                mListener.onSectionChange(SectionType.Footer, section);
            }

        }

        @Override
        public void resetDragging(SectionType type) {
            try {
                if (type == SectionType.Header) {
                    if (mCloneRecyclerView != null && mCloneRecyclerView.getChildAt(0)!=null) {
                        moveSectionToTop(mHeaderDragHolder, mCloneRecyclerView.getChildAt(0).getTop());
                    }
                } else
                    resetFooterDragging();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public void resetFooterDragging() {
            if (mCurrentFooterSection == null)
                return;
            moveSectionToTopBySectionPosition(mCurrentFooterSection.getSectionPosition());
        }

        @Override
        public void finishDragging(SectionType type, Section section) {
            if (type == SectionType.Header) {
                finishHeaderDragging(section);
            } else {
                onScrollSectionChange(SectionType.Footer, 0);
                setSection(SectionType.Footer, section);
            }
        }

        public void finishHeaderDragging(Section section) {
            setSection(SectionType.Header, mScrollLayoutManager.getPrevSession(section.getSectionPosition()));
            mFooterView.setTranslationY(0);
            Section footerSection = mScrollLayoutManager.getLastVisisbleSession();
            if (footerSection != null)
                setSection(SectionType.Footer, footerSection);
            else
                hideSection(SectionType.Footer, mFooterView.getHeight());
        }

        @Override
        public void resetCloneView() {
            mCloneView.setVisibility(View.INVISIBLE);
            mCloneRecyclerView.setAdapter(null);
        }


        public void loadRecyclerView(TreeMap<Integer, ArrayList<T>> sectionMap) {
            ArrayList<T> section = mapToList(sectionMap);
            mScrollLayoutManager = new ScrollLayoutManager(this, section, sectionMap, (LinearLayoutManager) mRecyclerView.getLayoutManager());
            mRecyclerView.addOnScrollListener(mScrollLayoutManager);
            ChannelAdapter adapter = new ChannelAdapter(mContext, section, this);
            mRecyclerView.setAdapter(adapter);

        }

        /**************************************************************************
         * private methods to create data for dragging portion
         ***************************************************************************/

        private void setUpRecyclerView() {
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.addItemDecoration(new Divider(mContext, Divider.VERTICAL_LIST));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            LinearLayoutManager cloneLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            mCloneRecyclerView.setLayoutManager(cloneLayoutManager);
            mCloneRecyclerView.addItemDecoration(new Divider(mContext, Divider.VERTICAL_LIST));
            mCloneRecyclerView.setItemAnimator(new DefaultItemAnimator());
        }

        private ArrayList<T> mapToList(TreeMap<Integer, ArrayList<T>> sectionMap) {
            ArrayList<T> sections = new ArrayList<T>();
            for (Map.Entry<Integer, ArrayList<T>> entry : sectionMap.entrySet()) {
                sections.addAll(entry.getValue());
            }
            return sections;
        }

        private void loadCloneRecyclerView(List<T> sections, final int offset) {
            ChannelAdapter adapter = new ChannelAdapter(mContext, sections, this);
            mCloneRecyclerView.setAdapter(adapter);
            if (offset < 0) {
                ((LinearLayoutManager) mCloneRecyclerView.getLayoutManager()).scrollToPositionWithOffset(0, offset);
            }
        }


    }