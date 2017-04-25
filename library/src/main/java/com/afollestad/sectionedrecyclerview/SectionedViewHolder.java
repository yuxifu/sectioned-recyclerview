package com.afollestad.sectionedrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class SectionedViewHolder extends RecyclerView.ViewHolder {

  interface PositionDelegate {
    ItemCoord relativePosition(int absolutePosition);

    boolean isHeader(int absolutePosition);
  }

  private PositionDelegate positionDelegate;

  public SectionedViewHolder(View itemView) {
    super(itemView);
  }

  void setPositionDelegate(PositionDelegate positionDelegate) {
    this.positionDelegate = positionDelegate;
  }

  protected ItemCoord getRelativePosition() {
    return positionDelegate.relativePosition(getAdapterPosition());
  }

  protected boolean isHeader() {
    return positionDelegate.isHeader(getAdapterPosition());
  }
}
