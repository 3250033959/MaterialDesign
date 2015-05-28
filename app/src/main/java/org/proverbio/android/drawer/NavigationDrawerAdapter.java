package org.proverbio.android.drawer;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.proverbio.android.material.R;
import org.proverbio.android.recycler.RecyclerItem;
import org.proverbio.android.recycler.ViewHolder;

import java.util.List;


/**
 * @author Juan Pablo Proverbio <proverbio8@gmail.com>
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<ViewHolder>
{
    /**
     * The RecyclerView items
     */
    private final List<RecyclerItem> items;

    /**
     * The RecyclerAdapter
     */
    private final DrawerCallback drawerCallback;

    /**
     * The current position
     */
    private int selectedPosition;

    public NavigationDrawerAdapter(List<RecyclerItem> items, DrawerCallback drawerCallback)
    {
        this.items = items;
        this.drawerCallback = drawerCallback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View v;

        if ( viewType == 0 )
        {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.drawer_item, viewGroup, false);

        }
        else
        {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_drawer_header, viewGroup, false);
        }

        final ViewHolder viewholder = new ViewHolder(v);

        viewholder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                drawerCallback.onDrawerPositionChanged(viewholder.getAdapterPosition());
            }
        });

        return viewholder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i)
    {
        RecyclerItem selectedItem = items.get(i);

        if ( i == 0 )
        {
            viewHolder.nameView.setText(selectedItem.getName());
            viewHolder.emailView.setText(selectedItem.getEmail());
        }
        else
        {
            viewHolder.nameView.setText(selectedItem.getName());
            viewHolder.nameView.setCompoundDrawablesWithIntrinsicBounds(selectedItem.getIconDrawable(), null, null, null);

            viewHolder.countView.setText(selectedItem.getCount());
            viewHolder.countLayout.setVisibility(selectedItem.isCountVisible() ? View.VISIBLE : View.GONE);

            if (selectedPosition == i )
            {
                viewHolder.itemView.setBackgroundColor(viewHolder.itemView.getContext().getResources().getColor(R.color.selected_gray));
            }
            else
            {
                viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    public void selectPosition(int position)
    {
        int lastPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(lastPosition);
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount()
    {
        return items != null ? items.size() : 0;
    }

    @Override
    public int getItemViewType( int position )
    {
        if ( position == 0 )
        {
            return 1;
        }

        return 0;
    }


    /**
     * Adapter callbacks
     */
    public interface DrawerCallback
    {
        void onDrawerPositionChanged(int position);
    }
}
