/**
 * Created by Ram_Thirupathy on 10/19/2016.
 */
package com.ramkt.sectionrecylerview;

import java.util.ArrayList;
import java.util.List;

/**
 * SectionItem class is subclass of {@link Section} and it is the representation of
 * items in the section
 */
public class SectionItem<T> extends Section<T> {
    T mItem;


    public T getItem() {
        return this.mItem;
    }


    public ArrayList<Section<T>> getSectionItems(List<T> items, int sectionPosition) {
        ArrayList<Section<T>> sections = new ArrayList<Section<T>>();
        for (T t : items) {
            SectionItem<T> sectionItem = new SectionItem<T>();
            sectionItem.mPosition = sectionPosition;//this is the order of section in which this item is present from top
            sectionItem.mItem = t;
            sections.add(sectionItem);
        }
        return sections;
    }
}
