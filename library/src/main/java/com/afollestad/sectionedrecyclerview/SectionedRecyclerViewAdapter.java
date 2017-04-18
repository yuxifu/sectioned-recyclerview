package com.afollestad.sectionedrecyclerview;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

import java.util.List;

/** @author Aidan Follestad (afollestad) */
public abstract class SectionedRecyclerViewAdapter<VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<VH> {

  protected static final int VIEW_TYPE_HEADER = -2;
  protected static final int VIEW_TYPE_ITEM = -1;

  private final ArrayMap<Integer, Integer> headerLocationMap;
  private GridLayoutManager layoutManager;
  private boolean showHeadersForEmptySections;

  public SectionedRecyclerViewAdapter() {
    headerLocationMap = new ArrayMap<>();
  }

  public abstract int getSectionCount();

  public abstract int getItemCount(int section);

  public abstract void onBindHeaderViewHolder(VH holder, int section);

  public abstract void onBindViewHolder(
      VH holder, int section, int relativePosition, int absolutePosition);

  public final boolean isHeader(int position) {
    return headerLocationMap.get(position) != null;
  }

  /**
   * Instructs the list view adapter to whether show headers for empty sections or not.
   *
   * @param show flag indicating whether headers for empty sections ought to be shown.
   */
  public final void shouldShowHeadersForEmptySections(boolean show) {
    showHeadersForEmptySections = show;
  }

  public final void setLayoutManager(@Nullable GridLayoutManager lm) {
    layoutManager = lm;
    if (lm == null) return;
    lm.setSpanSizeLookup(
        new GridLayoutManager.SpanSizeLookup() {
          @Override
          public int getSpanSize(int position) {
            if (isHeader(position)) return layoutManager.getSpanCount();
            final int[] sectionAndPos = getSectionIndexAndRelativePosition(position);
            final int absPos = position - (sectionAndPos[0] + 1);
            return getRowSpan(
                layoutManager.getSpanCount(), sectionAndPos[0], sectionAndPos[1], absPos);
          }
        });
  }

  @SuppressWarnings("UnusedParameters")
  protected int getRowSpan(
      int fullSpanSize, int section, int relativePosition, int absolutePosition) {
    return 1;
  }

  // returns section along with offsetted position
  private int[] getSectionIndexAndRelativePosition(int itemPosition) {
    synchronized (headerLocationMap) {
      Integer lastSectionIndex = -1;
      for (final Integer sectionIndex : headerLocationMap.keySet()) {
        if (itemPosition > sectionIndex) {
          lastSectionIndex = sectionIndex;
        } else {
          break;
        }
      }
      return new int[] {
        headerLocationMap.get(lastSectionIndex), itemPosition - lastSectionIndex - 1
      };
    }
  }

  @Override
  public final int getItemCount() {
    int count = 0;
    headerLocationMap.clear();
    for (int s = 0; s < getSectionCount(); s++) {
      int itemCount = getItemCount(s);
      if (showHeadersForEmptySections || (itemCount > 0)) {
        headerLocationMap.put(count, s);
        count += itemCount + 1;
      }
    }
    return count;
  }

  /**
   * @hide
   * @deprecated
   */
  @Override
  @Deprecated
  public long getItemId(int position) {
    if (isHeader(position)) {
      int pos = headerLocationMap.get(position);
      return getHeaderId(pos);
    } else {
      int[] sectionAndPos = getSectionIndexAndRelativePosition(position);
      return getItemId(sectionAndPos[0], sectionAndPos[1]);
    }
  }

  public long getHeaderId(int section) {
    return super.getItemId(section);
  }

  public long getItemId(int section, int position) {
    return super.getItemId(position);
  }

  /**
   * @hide
   * @deprecated
   */
  @Override
  @Deprecated
  public final int getItemViewType(int position) {
    if (isHeader(position)) {
      return getHeaderViewType(headerLocationMap.get(position));
    } else {
      final int[] sectionAndPos = getSectionIndexAndRelativePosition(position);
      return getItemViewType(
          sectionAndPos[0],
          // offset section view positions
          sectionAndPos[1],
          position - (sectionAndPos[0] + 1));
    }
  }

  @SuppressWarnings("UnusedParameters")
  @IntRange(from = 0, to = Integer.MAX_VALUE)
  public int getHeaderViewType(int section) {
    //noinspection ResourceType
    return VIEW_TYPE_HEADER;
  }

  @SuppressWarnings("UnusedParameters")
  @IntRange(from = 0, to = Integer.MAX_VALUE)
  public int getItemViewType(int section, int relativePosition, int absolutePosition) {
    //noinspection ResourceType
    return VIEW_TYPE_ITEM;
  }

  /**
   * @hide
   * @deprecated
   */
  @Override
  @Deprecated
  public final void onBindViewHolder(VH holder, int position) {
    StaggeredGridLayoutManager.LayoutParams layoutParams = null;
    if (holder.itemView.getLayoutParams() instanceof GridLayoutManager.LayoutParams)
      layoutParams =
          new StaggeredGridLayoutManager.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    else if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams)
      layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
    if (isHeader(position)) {
      if (layoutParams != null) layoutParams.setFullSpan(true);
      onBindHeaderViewHolder(holder, headerLocationMap.get(position));
    } else {
      if (layoutParams != null) layoutParams.setFullSpan(false);
      final int[] sectionAndPos = getSectionIndexAndRelativePosition(position);
      final int absPos = position - (sectionAndPos[0] + 1);
      onBindViewHolder(
          holder,
          sectionAndPos[0],
          // offset section view positions
          sectionAndPos[1],
          absPos);
    }
    if (layoutParams != null) holder.itemView.setLayoutParams(layoutParams);
  }

  /**
   * @hide
   * @deprecated
   */
  @Deprecated
  @Override
  public final void onBindViewHolder(VH holder, int position, List<Object> payloads) {
    super.onBindViewHolder(holder, position, payloads);
  }
}
