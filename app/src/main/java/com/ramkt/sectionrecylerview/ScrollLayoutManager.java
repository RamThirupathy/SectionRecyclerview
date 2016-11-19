/**
 * Created by Ram_Thirupathy on 10/18/2016.
 */
package com.ramkt.sectionrecylerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * ScrollLayoutManager class implements {@link android.support.v7.widget.RecyclerView.OnScrollListener}
 * listen to {@link RecyclerView} scrolls and sends data to {@link SectionListener}.
 * It used List and TreeMap to achieve fast dragging
 */
public class ScrollLayoutManager<T> extends RecyclerView.OnScrollListener {
    private SectionListener mHelper;
    private LinearLayoutManager mLayoutManger;
    private TreeMap<Integer, ArrayList<Section<T>>> mSectionMap;
    private ArrayList<Section<T>> mItems;
    private boolean mInitialLoad = true;

    public ScrollLayoutManager(SectionListener helper, ArrayList<Section<T>> channels, TreeMap<Integer, ArrayList<Section<T>>> sectionMap, LinearLayoutManager layoutManager) {
        this.mHelper = helper;
        this.mSectionMap = sectionMap;
        this.mItems = channels;
        this.mLayoutManger = layoutManager;
    }

    /**************************************************************************
     *
     * Abstract method implementation and other getter methods to get position and
     * view data
     *
     ***************************************************************************/
    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, final int dx, final int dy) {
        super.onScrolled(recyclerView, dx, dy);
        setHeader(recyclerView, dy);
        setFooter(recyclerView, dy);
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, final int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if(mLayoutManger.findFirstVisibleItemPosition()==-1)
            return;
        if (newState == 0) {
            Section firstItem = mItems.get(mLayoutManger.findFirstVisibleItemPosition());
            initializeHeaderSection(firstItem.getSectionPosition());
            initializeFooterSection(mLayoutManger.findLastVisibleItemPosition());
        }
    }

    public int getPositionInList(int sectionPosition) {//find total no of section item in all section before the section position
        int positionInList = 0;
        Map.Entry<Integer, ArrayList<Section<T>>> entry = mSectionMap.floorEntry(--sectionPosition);

        while (entry != null) {
            positionInList += entry.getValue().size();
            entry = mSectionMap.floorEntry(--sectionPosition);
        }
        return positionInList;
    }

    public void initializeStickySection(int position, int lastSection) {
        if (mSectionMap.floorEntry(position) != null) {
            int visibleItems = getVisibleItemCount() +1;
            if ((mItems.size() - position) > visibleItems) {
                mHelper.setSection(SectionListener.SectionType.Header, mSectionMap.floorEntry(mItems.get(position).getSectionPosition()).getValue().get(0));
            } else {
                int offset = visibleItems - (mItems.size() - position);
                int prevSection = position - offset;
                mHelper.setSection(SectionListener.SectionType.Header, mSectionMap.floorEntry(mItems.get(prevSection).getSectionPosition()).getValue().get(0));
            }
        }

        initializeFooterSection(lastSection);
    }

    public Section getFirstSession() {
        return mSectionMap.firstEntry().getValue().get(0);
    }

    public Section getLastSession() {
        return mSectionMap.lastEntry().getValue().get(0);
    }

    public Section getNextSession(int index) {
        Map.Entry<Integer, ArrayList<Section<T>>> entry = mSectionMap.ceilingEntry(index + 1);
        return entry == null ? null : entry.getValue().get(0);
    }

    public Section getPrevSession(int index) {
        return mSectionMap.floorEntry(index - 1).getValue().get(0);
    }

    public int findFirstVisibleItemPosition(){
        return mLayoutManger.findFirstVisibleItemPosition();
    }

    public ArrayList<Section<T>> getItemsInSection(int sectionIndex,int itemsNeeded){
        ArrayList<Section<T>> sections = new ArrayList<Section<T>>(mSectionMap.get(sectionIndex)) ;
        while(sections.size()<itemsNeeded){
            sectionIndex++;
            int offset = itemsNeeded - sections.size();
            ArrayList<Section<T>> section = mSectionMap.get(sectionIndex);
            if(section==null){
                break;
            }
            else if(offset>=section.size()){
                sections.addAll(section);
            }else{
                sections.addAll(section.subList(0,offset));
            }
        }
        return sections;
    }

    public List<Section<T>> getItemsInList(int listIndex, int itemsNeeded){
        int toIndex = itemsNeeded+listIndex;
        if(toIndex>mItems.size()){
            toIndex--;
        }
        return mItems.subList(listIndex,toIndex);
    }

    public void scrollToPositionWithOffset(int position, int offset) {
        mLayoutManger.scrollToPositionWithOffset(position, offset);
    }

    public int getVisibleItemCount() {
        return mLayoutManger.findLastVisibleItemPosition() - mLayoutManger.findFirstVisibleItemPosition();
    }

    public Section getLastVisisbleSession(){
        int position = mLayoutManger.findLastVisibleItemPosition();
        int sectionPosition = mItems.get(position).getSectionPosition();
        return mSectionMap.get(sectionPosition+1)!=null?mSectionMap.get(sectionPosition+1).get(0):null;
    }

    /**************************************************************************
     *
     * Private methods to set up Section header and Footer while user scrolls the
     * recycler view
     *
     ***************************************************************************/

    private void initializeHeaderSection(int position) {
        if (mSectionMap.floorEntry(position) != null) {
            if (mSectionMap.lastKey() == mItems.get(position).getSectionPosition()) {
                //find no of items in screen and if last section has items less than visible items then decrement position
                if (mSectionMap.lastEntry().getValue().size() < getVisibleItemCount()) {
                    position = mSectionMap.lastKey() - 1;
                }
            }
            mHelper.setSection(SectionListener.SectionType.Header, mSectionMap.floorEntry(position).getValue().get(0));
        }
    }

    private void initializeFooterSection(int position) {
        if (position >= mItems.size())
            return;
        Section lastItem = mItems.get(position);
        if (!validateFooter(lastItem)) {
            mHelper.setSection(SectionListener.SectionType.Footer, mSectionMap.ceilingEntry(lastItem.getSectionPosition() + 1).getValue().get(0));
        }
    }


    private boolean validateFooter(Section firstItem) {
        if (mSectionMap.ceilingEntry(firstItem.getSectionPosition() + 1) == null) {
            final int viewPosition = (mLayoutManger.findLastVisibleItemPosition()) - mLayoutManger.findFirstVisibleItemPosition();
            View view = mLayoutManger.getChildAt(viewPosition);
            mHelper.hideSection(SectionListener.SectionType.Footer, view.getHeight());
            return true;
        }
        return false;
    }

    private void setHeader(int position, Section section) {
        mHelper.setSection(SectionListener.SectionType.Header, section);
        mHelper.showSection(SectionListener.SectionType.Header, position);
    }

    private void setFooter(int position, Section section) {
        mHelper.setSection(SectionListener.SectionType.Footer, section);
        mHelper.showSection(SectionListener.SectionType.Footer, position);
    }

    private View getViewByPosition(int position) {
        final int viewPosition = (position) - mLayoutManger.findFirstVisibleItemPosition();
        return mLayoutManger.getChildAt(viewPosition);
    }

    private void setHeader(@NonNull final RecyclerView recyclerView, final int dy) {
        int firstPosition = mLayoutManger.findFirstVisibleItemPosition();
        if (firstPosition < 0)
            return;
        Section firstItem = mItems.get(firstPosition);
        Section secondItem = mItems.get(firstPosition + 1);
        if (dy >= 0) {//scroll down
            if (mInitialLoad) {
                initializeFooterSection(mLayoutManger.findLastVisibleItemPosition());
                mInitialLoad = false;
            }
            if (firstItem.isHeader()) {
                setHeader(0, firstItem);
            } else if (secondItem.isHeader()) {
                int offset = recyclerView.getChildAt(1).getHeight() - recyclerView.getChildAt(0).getHeight();
                int topPosition = recyclerView.getChildAt(0).getTop();
                if (topPosition < offset) {
                    mHelper.onScrollSectionChange(SectionListener.SectionType.Header, topPosition + recyclerView.getChildAt(1).getHeight());
                }
            } else {
                mHelper.onScrollSectionChange(SectionListener.SectionType.Header, 0);
            }
        } else {//scroll up
            if (firstItem.isHeader()) {
                if (recyclerView.getChildAt(0).getTop() == 0)
                    mHelper.hideSection(SectionListener.SectionType.Header, -recyclerView.getChildAt(0).getHeight());//apply negative mPosition to hide
            } else if (secondItem.isHeader()) {
                int bottomOffset = recyclerView.getChildAt(1).getTop() - recyclerView.getChildAt(1).getHeight();
                if (bottomOffset <= 0) {
                    setHeader(bottomOffset, mSectionMap.floorEntry(secondItem.getSectionPosition() - 1).getValue().get(0));
                }
            } else {
                mHelper.onScrollSectionChange(SectionListener.SectionType.Header, 0);
            }
        }
    }


    private void setFooter(@NonNull final RecyclerView recyclerView, final int dy) {
        int lastPosition = mLayoutManger.findLastVisibleItemPosition();
        if (lastPosition < 0)
            return;
        Section firstItem = mItems.get(lastPosition);
        if (validateFooter(firstItem)) {
            return;
        }
        Section secondItem = mItems.get(lastPosition - 1);
        if (dy >= 0) {//scroll down
            if (firstItem.isHeader()) {
                View view = getViewByPosition(lastPosition);
                int offset = view.getTop() - recyclerView.getHeight();
                if (offset <= (-view.getHeight()))
                    mHelper.hideSection(SectionListener.SectionType.Footer, view.getHeight());//positive value to push down in y axis
            } else if (secondItem.isHeader()) {
                View view = getViewByPosition(lastPosition - 1);//second from last
                int offset = view.getHeight() - (recyclerView.getHeight() - view.getBottom());
                if (offset >= 0) {
                    if (mSectionMap.ceilingEntry(secondItem.getSectionPosition() + 1) != null)
                        setFooter(offset, mSectionMap.ceilingEntry(secondItem.getSectionPosition() + 1).getValue().get(0));
                    else {
                        mHelper.hideSection(SectionListener.SectionType.Footer, view.getHeight());//hide footer if no section is after the current one
                    }
                }
            } else {
                mHelper.onScrollSectionChange(SectionListener.SectionType.Footer, 0);
            }
        } else {//scroll up
            if (firstItem.isHeader()) {
                setFooter(0, firstItem);
            } else if (secondItem.isHeader()) {
                View view = getViewByPosition(lastPosition - 1);//second from last
                int offset = (view.getBottom() + view.getHeight()) - recyclerView.getHeight();
                if (offset > 0) {
                    mHelper.onScrollSectionChange(SectionListener.SectionType.Footer, offset);
                }
            } else {
                mHelper.onScrollSectionChange(SectionListener.SectionType.Footer, 0);
            }
        }
    }

}
