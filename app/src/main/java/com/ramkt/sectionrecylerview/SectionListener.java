/**
 * Created by Ram_Thirupathy on 10/18/2016.
 */
package com.ramkt.sectionrecylerview;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;



/**
 * Main driving class to achieve section dragging and sticky section
 */
public abstract class SectionListener {
    protected Context mContext;
    protected RecyclerView mRecyclerView;
    protected View mHeaderView;
    protected View mFooterView;
    protected Section mCurrentHeaderSection;
    protected Section mCurrentFooterSection;
    protected View mCloneView;
    private boolean isSectionDragging;
    private int mSectionDropDuration = 500;//milli seconds

    public SectionListener(Context context, RecyclerView recyclerView, View headerView, View footerView, View cloneView) {
        this.mContext = context;
        this.mHeaderView = headerView;
        this.mFooterView = footerView;
        this.mRecyclerView = recyclerView;
        this.mCloneView = cloneView;
        initHeaderSticker();
        initFooterSticker();
    }

    /**************************************************************************
     * Abstract methods to implement by recylerview holder..refer{@link SectionRecyclerView}
     ***************************************************************************/

    public abstract void moveSectionToTop(int index, int offset);

    public abstract void moveSectionToTopBySectionPosition(int index);

    public abstract Section getLastSection();

    public abstract void updateCloneSection(SectionType type, Section section);

    public abstract void resetDragging(SectionType type);

    public abstract void finishDragging(SectionType type, Section section);

    public abstract void resetCloneView();


    /**************************************************************************
     * public methods to access Header and Footer view and to set the x,y location of
     * the same
     ***************************************************************************/

    public void setSection(SectionType type, Section section) {
        if (type == SectionType.Header) {
            if (isDragging())
                return;
            mCurrentHeaderSection = section;
        } else {
            mCurrentFooterSection = section;
        }
    }

    public void showSection(SectionType type, int position) {
        if (type == SectionType.Header)
            mHeaderView.setTranslationY(position);
        else {
            if (isDragging()) {
                return;
            }
            mFooterView.setTranslationY(position);
        }
    }

    public void hideSection(SectionType type, int position) {
        if (type == SectionType.Header)
            mHeaderView.setTranslationY(position);
        else {
            mCurrentFooterSection = null;
            mFooterView.setTranslationY(position);
        }
    }

    public void onScrollSectionChange(SectionType type, int position) {
        if (type == SectionType.Header)
            mHeaderView.setTranslationY(position);
        else {
            if (isDragging())
                return;
            mFooterView.setTranslationY(position);
        }
    }

    protected boolean isDragging() {
        return isSectionDragging;
    }

    /**************************************************************************
     * private methods to set up the dragging portion
     ***************************************************************************/

    private void setCloneTop() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mCloneView.getHeight());
        lp.setMargins(0, 0, 0, 0);
        lp.gravity = Gravity.TOP;
        mCloneView.setLayoutParams(lp);
        updateCloneSection(SectionType.Header, mCurrentHeaderSection);
        moveSectionToTopBySectionPosition(mCurrentHeaderSection.getSectionPosition() - 1);
        mHeaderView.setTranslationY(-mHeaderView.getHeight());
    }

    private void setCloneBottom() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mCloneView.getHeight());
        lp.setMargins(0, mRecyclerView.getHeight() - mFooterView.getHeight(), 0, 0);
        mCloneView.setLayoutParams(lp);
        updateCloneSection(SectionType.Footer, mCurrentFooterSection);
        mFooterView.setTranslationY(mFooterView.getHeight());
    }

    private void initHeaderSticker() {
        mHeaderView.setOnTouchListener(new View.OnTouchListener() {
            float dY;
            float newY = 0;
            boolean firstHold = true;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int recyclerViewHt = mRecyclerView.getHeight() - view.getHeight();
                try {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_MOVE:
                            newY = -(dY - event.getRawY());
                            if (mCurrentHeaderSection.getSectionPosition() == 0) {
                                return false;
                            }
                            if (firstHold) {
                                firstHold = false;
                                setCloneTop();
                            }
                            if (newY > 0 && newY <= recyclerViewHt) {
                                mCloneView.setTranslationY(newY);
                                if (newY > (recyclerViewHt - view.getHeight())) {
                                    mFooterView.setTranslationY((int) (newY - (recyclerViewHt - view.getHeight())));
                                } else if (newY <= (view.getHeight())) {
                                    onScrollSectionChange(SectionType.Header, (int) (newY - view.getHeight()));
                                } else {
                                    onScrollSectionChange(SectionType.Header, 0);
                                }
                            } else {
                                return false;
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            if (mCurrentHeaderSection.getSectionPosition() == 0) {
                                isSectionDragging = false;
                                return false;
                            }
                            if (newY >= (recyclerViewHt / 2)) {
                                mCloneView.animate().y(recyclerViewHt).setListener(new SectionAnimationListener(SectionType.Header, 1)).setDuration(mSectionDropDuration).start();
                            } else {
                                mCloneView.animate().y(0).setListener(new SectionAnimationListener(SectionType.Header, 0)).setDuration(mSectionDropDuration).start();
                            }
                            break;
                        case MotionEvent.ACTION_DOWN:
                            if (mCurrentFooterSection != null)
                                mFooterView.setTranslationY(0);
                            dY = event.getRawY();
                            firstHold = true;
                            isSectionDragging = true;
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
    }

    private void initFooterSticker() {
        mFooterView.setOnTouchListener(new View.OnTouchListener() {
            float dY;
            float newY = 0;
            boolean firstHold = true;
            boolean isTouch = true;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int recyclerViewHt = -(mRecyclerView.getHeight() - view.getHeight());
                try {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_MOVE:
                            newY = event.getRawY() - dY;

                            if (mCurrentFooterSection.getSectionPosition() == getLastSection().getSectionPosition()) {
                                return false;
                            }
                            if (firstHold) {
                                firstHold = false;
                                setCloneBottom();
                            }

                            if (newY < 0 && newY >= (recyclerViewHt)) {
                                isTouch = false;
                                mCloneView.setTranslationY(newY);
                                if (newY < (recyclerViewHt + view.getHeight())) {
                                    onScrollSectionChange(SectionType.Header, (int) (-(recyclerViewHt + view.getHeight() - (newY))));
                                } else if (mCurrentFooterSection.getSectionPosition() < getLastSection().getSectionPosition()) {
                                    if (newY >= (-view.getHeight())) {
                                        mFooterView.setTranslationY((int) (view.getHeight() + newY));
                                    } else {
                                        mFooterView.setTranslationY(0);
                                    }
                                }
                            } else {
                                return false;
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            if (isTouch) {
                                isSectionDragging = false;
                                moveSectionToTopBySectionPosition(mCurrentFooterSection.getSectionPosition());
                                resetCloneView();
                                return false;
                            }
                            if (mCurrentFooterSection.getSectionPosition() == getLastSection().getSectionPosition()) {
                                isSectionDragging = false;
                                return false;
                            }

                            if (newY <= (recyclerViewHt / 2)) {
                                mCloneView.animate().y(0).setListener(new SectionAnimationListener(SectionType.Footer, 0)).setDuration(mSectionDropDuration).start();
                            } else {
                                mCloneView.animate().y(-(recyclerViewHt)).setListener(new SectionAnimationListener(SectionType.Footer, 1)).setDuration(mSectionDropDuration).start();
                            }
                            break;
                        case MotionEvent.ACTION_DOWN:
                            if (!isSectionDragging) {
                                isTouch = true;
                                mHeaderView.setTranslationY(0);
                                dY = event.getRawY();
                                firstHold = true;
                                isSectionDragging = true;
                            }
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
    }

    /**************************************************************************
     * Support Inner class and interface
     ***************************************************************************/

    public interface onSectionChangeListener {
        void onSectionChange(SectionType type, Section section);

        void onDrag(Section section);
    }


    public enum SectionType {
        Header,
        Footer
    }

    public class SectionAnimationListener implements Animator.AnimatorListener {
        SectionType mType;
        int mMovement;

        public SectionAnimationListener(SectionType type, int movement) {
            this.mType = type;
            this.mMovement = movement;
        }

        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            isSectionDragging = false;
            if (mType == SectionType.Header) {
                if (mMovement == 0)
                    resetDragging(SectionType.Header);
                else
                    finishDragging(SectionType.Header, mCurrentHeaderSection);
            } else {
                if (mMovement == 0)
                    resetDragging(SectionType.Footer);
                else
                    finishDragging(SectionType.Footer, mCurrentFooterSection);
            }
            resetCloneView();

        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }
}