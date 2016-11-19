/**
 * Created by Ram_Thirupathy on 10/19/2016.
 */
package com.ramkt.sectionrecylerview;

/**
 * StickySectionItem class is subclass of {@link Section} and it should be changed based
 * on the adapter view and data
 */
public class StickySectionItem extends Section {//(this class attribute will change depends on the section view)
    private String mDay;

    public StickySectionItem(String day, int position) {
        this.mDay = day;
        super.mPosition = position;//this is the order of section from top
        super.isHeader = true;
    }

    public String getDay() {
        return mDay;
    }

}
