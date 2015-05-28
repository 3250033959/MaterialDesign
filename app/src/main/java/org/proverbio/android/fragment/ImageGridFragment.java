package org.proverbio.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import org.proverbio.android.activity.DetailActivity;
import org.proverbio.android.material.R;

public class ImageGridFragment extends BaseFragment implements AdapterView.OnItemClickListener
{
    public static final String TAG = ImageGridFragment.class.getSimpleName();

    private GridView gridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        if (gridView == null)
        {
            //Gets {@see GridView} instance from inflated layout
            gridView = (GridView)inflater.inflate(R.layout.fragment_image_grid, container, false);

            //Sets the GridView Adapter
            gridView.setAdapter(new GridAdapter());

            //Sets the onItemClickListener
            gridView.setOnItemClickListener(this);

            getSwipeRefreshLayout().addView(gridView);
        }

        return getSwipeRefreshLayout();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        String url = (String) view.getTag();
        DetailActivity.launch(getAppCompatActivity(), view.findViewById(R.id.image), url);
    }

    private static class GridAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return 10;
        }

        @Override
        public Object getItem(int i)
        {
            return "Item " + String.valueOf(i + 1);
        }

        @Override
        public long getItemId(int i)
        {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            if (view == null)
            {
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.grid_item, viewGroup, false);
            }

            String imageUrl = "http://lorempixel.com/800/600/sports/" + String.valueOf(i + 1);
            view.setTag(imageUrl);

            ImageView image = (ImageView) view.findViewById(R.id.image);
            Picasso.with(view.getContext())
                    .load(imageUrl)
                    .into(image);

            TextView text = (TextView) view.findViewById(R.id.name);
            text.setText(getItem(i).toString());

            return view;
        }
    }
}
