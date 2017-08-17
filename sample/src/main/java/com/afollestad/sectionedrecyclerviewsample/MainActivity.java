package com.afollestad.sectionedrecyclerviewsample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

/** @author Aidan Follestad (afollestad) */
public class MainActivity extends AppCompatActivity {

  private MainAdapter adapter;
  private boolean hideEmpty;
  private boolean showFooters = true;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    RecyclerView list = (RecyclerView) findViewById(R.id.list);
    adapter = new MainAdapter();
    GridLayoutManager manager =
        new GridLayoutManager(this, getResources().getInteger(R.integer.grid_span));
    list.setLayoutManager(manager);
    adapter.setLayoutManager(manager);
    adapter.shouldShowHeadersForEmptySections(hideEmpty);
    adapter.shouldShowFooters(showFooters);
    list.setAdapter(adapter);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    menu.findItem(R.id.hide_empty_sections).setChecked(!hideEmpty);
    menu.findItem(R.id.show_footers).setChecked(showFooters);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.hide_empty_sections) {
      hideEmpty = !hideEmpty;
      adapter.shouldShowHeadersForEmptySections(hideEmpty);
      item.setChecked(!hideEmpty);
      return true;
    } else if (item.getItemId() == R.id.show_footers) {
      showFooters = !showFooters;
      adapter.shouldShowFooters(showFooters);
      item.setChecked(showFooters);
      return true;
    } else if (item.getItemId() == R.id.home_options_expand_all_sections) {
      adapter.expandAllSections();
      return true;
    } else if (item.getItemId() == R.id.home_options_collapse_all_sections) {
      adapter.collapseAllSections();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
