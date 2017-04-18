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
    adapter.shouldShowHeadersForEmptySections(false);
    list.setAdapter(adapter);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.hide_empty_sections) {
      hideEmpty = !hideEmpty;
      adapter.shouldShowHeadersForEmptySections(hideEmpty);
      adapter.notifyDataSetChanged();
      item.setChecked(hideEmpty);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
