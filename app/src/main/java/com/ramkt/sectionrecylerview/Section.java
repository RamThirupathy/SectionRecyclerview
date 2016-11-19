/**
 * Created by Ram_Thirupathy on 10/19/2016.
 */
package com.ramkt.sectionrecylerview;

/**
 * Section class should be used by adapter to be set to Recylerview
 * refer {@link com.altice.labox.guide.channelguide.presentation.adapter.ChannelSectionRowListAdapter}
 */
public class Section<T> {//(this class getters will change depends on the section view)
    protected boolean isHeader;
    protected int mPosition;

    public boolean isHeader() {
        return this.isHeader;
    }


    public int getSectionPosition() {
        return this.mPosition;
    }

    public T getItem() {
        return null;
    }


    public String getDay() {
        return null;
    }

    public String getSeeAll() {
        return null;
    }

    public boolean isSeeAllVisible() {
        return false;
    }
}
