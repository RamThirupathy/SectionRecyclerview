# Section RecyclerView
Custom recylerview to view show list of sections

# Features
1. Click and move the section to top
2. Drag the section to bottom/top

# Demo
![alt tag](https://github.com/RamThirupathy/SectionRecyclerview/blob/master/sectionview.gif)

### Step 1
Add the following layouts
```java
1. layout_section_view.xml//list item design
2. layout_section_header.xml//header design
3. layout_section_footer.xml//footer design
4. layout_section_clone.xml//design of the list while dragging
```
### Step 2
Position the above layouts in main layout file
```java
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <android.support.v7.widget.RecyclerView
        android:id="@+id/rv_list"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
    </android.support.v7.widget.RecyclerView>
    <include
        layout="@layout/layout_section_header"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="top"></include>
    <FrameLayout
        android:id="@+id/fl_clone_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:visibility="invisible">
        <android.support.v7.widget.RecyclerView
            android:id="@+id/rv_clone_list"
            android:layout_width="match_parent"
            android:layout_height="match_parent">

        </android.support.v7.widget.RecyclerView>
        <include
            layout="@layout/layout_section_clone"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="top"></include>
    </FrameLayout>
    <include
        layout="@layout/layout_section_footer"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom"></include>
</FrameLayout>
```
### Step 3
Please add view adapter having list of Section.java

```java
public class ChannelAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Section<Channel>> mChannels;
```

### Step 4
Main container should implements SectionListener.onSectionChangeListener to get call back while section changes
when user scrolls or drags
```java
public void onSectionChange(SectionListener.SectionType type, Section section) ;
public void onDrag(Section section);
```
### Step 5
Intialize SectionRecylcerView and load it with TreeMap<Integer, ArrayList<Section<T>>>.

```java
  SectionRecyclerView mSectionRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSectionRecyclerView = new SectionRecyclerView(this, (RecyclerView) findViewById(R.id.rv_list), this,     findViewById(R.id.rl_header_layout), findViewById(R.id.rl_footer_layout), findViewById(R.id.fl_clone_view), (RecyclerView) findViewById(R.id.rv_clone_list));
        mSectionRecyclerView.loadRecyclerView(loadChannels());
    }

public void loadRecyclerView(TreeMap<Integer, ArrayList<T>> sectionMap);
```

Contributions
-------

Any contributions are welcome!

Thanks
-------
