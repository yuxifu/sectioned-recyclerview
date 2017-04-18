package com.afollestad.sectionedrecyclerview;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.ViewGroup;

import java.util.List;

/** @author Aidan Follestad (afollestad) */
public abstract class SectionedRecyclerViewAdapter<VH extends SectionedViewHolder>
    extends RecyclerView.Adapter<VH> {

  private static final String TAG = "SectionedRVAdapter";

  protected static final int VIEW_TYPE_HEADER = -2;
  protected static final int VIEW_TYPE_ITEM = -1;

  private final ArrayMap<Integer, Integer> headerLocationMap;
  private final ArrayMap<Integer, Boolean> collapsedSectionMap;
  private GridLayoutManager layoutManager;
  private boolean showHeadersForEmptySections;
  private SectionedViewHolder.PositionDelegate positionDelegate;

  public SectionedRecyclerViewAdapter() {
    headerLocationMap = new ArrayMap<>();
    collapsedSectionMap = new ArrayMap<>(0);
    positionDelegate =
        new SectionedViewHolder.PositionDelegate() {
          @Override
          public ItemCoord relativePosition(int absolutePosition) {
            return getRelativePosition(absolutePosition);
          }

          @Override
          public boolean isHeader(int absolutePosition) {
            return SectionedRecyclerViewAdapter.this.isHeader(absolutePosition);
          }
        };
  }

  public void notifySectionChanged(@IntRange(from = 0, to = Integer.MAX_VALUE) int section) {
    if (section < 0 || section > getSectionCount() - 1) {
      throw new IllegalArgumentException(
          "Section " + section + " is out of range of existing sections.");
    }
    Integer sectionHeaderIndex = headerLocationMap.get(section);
    if (sectionHeaderIndex == null) {
      throw new IllegalStateException("No header position mapped for section " + section);
    }
    int sectionItemCount = getItemCount(section);
    if (sectionItemCount == 0) {
      Log.d(TAG, "There are no items in section " + section + " to notify.");
      return;
    }
    int startPosition = sectionHeaderIndex + 1;
    Log.d(TAG, "Invalidating " + sectionItemCount + " items starting at index " + startPosition);
    notifyItemRangeChanged(startPosition, sectionItemCount);
  }

  public void expandSection(int section) {
    collapsedSectionMap.remove(section);
    notifyDataSetChanged();
  }

  public void collapseSection(int section) {
    collapsedSectionMap.put(section, true);
    notifyDataSetChanged();
  }

  public void toggleSectionExpanded(int section) {
    if (collapsedSectionMap.get(section) != null) {
      expandSection(section);
    } else {
      collapseSection(section);
    }
  }

  public abstract int getSectionCount();

  public abstract int getItemCount(int section);

  public abstract void onBindHeaderViewHolder(VH holder, int section, boolean expanded);

  public abstract void onBindViewHolder(
      VH holder, int section, int relativePosition, int absolutePosition);

  public final boolean isHeader(int position) {
    return headerLocationMap.get(position) != null;
  }

  public final boolean isSectionExpanded(int section) {
    return collapsedSectionMap.get(section) == null;
  }

  public final int getSectionHeaderIndex(int section) {
    Integer index = headerLocationMap.get(section);
    if (index == null) {
      return -1;
    }
    return index;
  }

  public final void shouldShowHeadersForEmptySections(boolean show) {
    showHeadersForEmptySections = show;
  }

  public final void setLayoutManager(@Nullable GridLayoutManager lm) {
    layoutManager = lm;
    if (lm == null) {
      return;
    }
    lm.setSpanSizeLookup(
        new GridLayoutManager.SpanSizeLookup() {
          @Override
          public int getSpanSize(int position) {
            if (isHeader(position)) {
              return layoutManager.getSpanCount();
            }
            ItemCoord sectionAndPos = getRelativePosition(position);
            int absPos = position - (sectionAndPos.section() + 1);
            return getRowSpan(
                layoutManager.getSpanCount(),
                sectionAndPos.section(),
                sectionAndPos.relativePos(),
                absPos);
          }
        });
  }

  protected int getRowSpan(
      int fullSpanSize, int section, int relativePosition, int absolutePosition) {
    return 1;
  }

  /** Converts an absolute position to a relative position and section. */
  public ItemCoord getRelativePosition(int absolutePosition) {
    Integer absHeaderLoc = headerLocationMap.get(absolutePosition);
    if (absHeaderLoc != null) {
      return new ItemCoord(absHeaderLoc, -1);
    }
    synchronized (headerLocationMap) {
      Integer lastSectionIndex = -1;
      for (Integer sectionIndex : headerLocationMap.keySet()) {
        if (absolutePosition > sectionIndex) {
          lastSectionIndex = sectionIndex;
        } else {
          break;
        }
      }
      return new ItemCoord(
          headerLocationMap.get(lastSectionIndex), absolutePosition - lastSectionIndex - 1);
    }
  }

  @Override
  public final int getItemCount() {
    int count = 0;
    headerLocationMap.clear();
    for (int s = 0; s < getSectionCount(); s++) {
      int itemCount = getItemCount(s);
      if (collapsedSectionMap.get(s) != null) {
        headerLocationMap.put(count, s);
        count += 1;
        continue;
      }
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
      ItemCoord sectionAndPos = getRelativePosition(position);
      return getItemId(sectionAndPos.section(), sectionAndPos.relativePos());
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
      ItemCoord sectionAndPos = getRelativePosition(position);
      return getItemViewType(
          sectionAndPos.section(),
          // offset section view positions
          sectionAndPos.relativePos(),
          position - (sectionAndPos.section() + 1));
    }
  }

  @IntRange(from = 0, to = Integer.MAX_VALUE)
  public int getHeaderViewType(int section) {
    //noinspection ResourceType
    return VIEW_TYPE_HEADER;
  }

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
    holder.setPositionDelegate(positionDelegate);

    StaggeredGridLayoutManager.LayoutParams layoutParams = null;
    if (holder.itemView.getLayoutParams() instanceof GridLayoutManager.LayoutParams)
      layoutParams =
          new StaggeredGridLayoutManager.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    else if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
      layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
    }

    if (isHeader(position)) {
      if (layoutParams != null) {
        layoutParams.setFullSpan(true);
      }
      int sectionIndex = headerLocationMap.get(position);
      onBindHeaderViewHolder(holder, sectionIndex, isSectionExpanded(sectionIndex));
    } else {
      if (layoutParams != null) {
        layoutParams.setFullSpan(false);
      }
      ItemCoord sectionAndPos = getRelativePosition(position);
      int absPos = position - (sectionAndPos.section() + 1);
      onBindViewHolder(
          holder,
          sectionAndPos.section(),
          // offset section view positions
          sectionAndPos.relativePos(),
          absPos);
    }

    if (layoutParams != null) {
      holder.itemView.setLayoutParams(layoutParams);
    }
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
