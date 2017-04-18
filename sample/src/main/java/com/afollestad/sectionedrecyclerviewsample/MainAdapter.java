package com.afollestad.sectionedrecyclerviewsample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.afollestad.sectionedrecyclerview.SectionedViewHolder;

/** @author Aidan Follestad (afollestad) */
class MainAdapter extends SectionedRecyclerViewAdapter<MainAdapter.MainVH> {

  @Override
  public int getSectionCount() {
    return 20;
  }

  @Override
  public int getItemCount(int section) {
    switch (section) {
      case 0:
        return 4;
      case 1:
        return 0;
      case 2:
        return 10;
      default:
        return 6;
    }
  }

  @Override
  public void onBindHeaderViewHolder(MainVH holder, int section, boolean expanded) {
    holder.title.setText(String.format("Section %d", section));
    holder.caret.setImageResource(expanded ? R.drawable.ic_collapse : R.drawable.ic_expand);
  }

  @Override
  public void onBindViewHolder(
      MainVH holder, int section, int relativePosition, int absolutePosition) {
    holder.title.setText(
        String.format("S:%d, P:%d, A:%d", section, relativePosition, absolutePosition));
  }

  @Override
  public int getItemViewType(int section, int relativePosition, int absolutePosition) {
    if (section == 1) {
      return 0; // VIEW_TYPE_HEADER is -2, VIEW_TYPE_ITEM is -1. You can return 0 or greater.
    }
    return super.getItemViewType(section, relativePosition, absolutePosition);
  }

  @Override
  public MainVH onCreateViewHolder(ViewGroup parent, int viewType) {
    int layout;
    switch (viewType) {
      case VIEW_TYPE_HEADER:
        layout = R.layout.list_item_header;
        break;
      case VIEW_TYPE_ITEM:
        layout = R.layout.list_item_main;
        break;
      default:
        layout = R.layout.list_item_main_bold;
        break;
    }

    View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
    return new MainVH(v, this);
  }

  static class MainVH extends SectionedViewHolder implements View.OnClickListener {

    Toast toast;
    final TextView title;
    final ImageView caret;
    final MainAdapter adapter;

    MainVH(View itemView, MainAdapter adapter) {
      super(itemView);
      this.title = (TextView) itemView.findViewById(R.id.title);
      this.caret = (ImageView) itemView.findViewById(R.id.caret);
      this.adapter = adapter;
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      if (isHeader()) {
        adapter.toggleSectionExpanded(getRelativePosition().section());
      } else {
        if (toast != null) {
          toast.cancel();
        }
        toast =
            Toast.makeText(view.getContext(), getRelativePosition().toString(), Toast.LENGTH_SHORT);
        toast.show();
      }
    }
  }
}
