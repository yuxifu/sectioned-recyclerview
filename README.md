# Sectioned RecyclerView

Sectioned RecyclerView allows you to easily split a RecyclerView into sections with headers.

![Screenshots](https://raw.githubusercontent.com/afollestad/sectioned-recyclerview/master/art/showcase.png)

---

# Gradle Dependency

[ ![jCenter](https://api.bintray.com/packages/drummer-aidan/maven/sectioned-recyclerview/images/download.svg) ](https://bintray.com/drummer-aidan/maven/sectioned-recyclerview/_latestVersion)
[![Build Status](https://travis-ci.org/afollestad/sectioned-recyclerview.svg)](https://travis-ci.org/afollestad/sectioned-recyclerview)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0.html)

The Gradle dependency is available via [jCenter](https://bintray.com/drummer-aidan/maven/sectioned-recyclerview/view).
jCenter is the default Maven repository used by Android Studio.

### Dependency

Add this to your module's `build.gradle` file:

```gradle
dependencies {
    ...
    compile('com.afollestad:sectioned-recyclerview:0.2.2') {
        transitive = true
    }
}
```

# Adapter

Here's a basic example:

```java
public class MainAdapter extends SectionedRecyclerViewAdapter<MainAdapter.MainVH> {

    @Override
    public int getSectionCount() {
        return 20; // number of sections.
    }

    @Override
    public int getItemCount(int section) {
        return 8; // number of items in section (section index is parameter).
    }

    @Override
    public void onBindHeaderViewHolder(MainVH holder, int section) {
        // Setup header view.
    }

    @Override
    public void onBindViewHolder(MainVH holder, int section, int relativePosition, int absolutePosition) {
        // Setup non-header view.
        // 'section' is section index.
        // 'relativePosition' is index in this section.
        // 'absolutePosition' is index out of all non-header items.
        // See sample project for a visual of how these indices work.
    }

    @Override
    public MainVH onCreateViewHolder(ViewGroup parent, int viewType) {
        // Change inflated layout based on 'header'. 
        View v = LayoutInflater.from(parent.getContext())
                .inflate(viewType == VIEW_TYPE_HEADER ? R.layout.header : R.layout.normal, parent, false);
        return new MainVH(v);
    }

    public static class MainVH extends RecyclerView.ViewHolder {

        public MainVH(View itemView) {
            super(itemView);
            // Setup view holder.
            // You'd want some views to be optional, e.g. for header vs. normal.
        }
    }
}
```

# Layout Manager

If you're using a `LinearLayoutManager`, you're all set. If you're using a `GridLayoutManager`,
you need to tell the adapter:

```java
GridLayoutManager manager = // ...
adapter.setLayoutManager(manager);
```

This is vital to getting headers to span all columns.
