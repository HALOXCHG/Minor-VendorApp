package com.minor.vendorapp.Nav.Home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.minor.vendorapp.Helpers.Functions;
import com.minor.vendorapp.R;

import java.util.List;

class AdapterOrderDetailsItems extends RecyclerView.Adapter<AdapterOrderDetailsItems.OrdersViewHolder> {

    List<DataTransferOrderDetailsItems> list;
    Context context;

    public AdapterOrderDetailsItems(List<DataTransferOrderDetailsItems> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ordered_items_listing_tile, parent, false);

        return new OrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersViewHolder holder, int position) {
        holder.orderedItemImage.setImageBitmap(Functions.base64ToBitmap(list.get(position).getItemImage()));

        if (list.get(position).getItemImage().length() > 20)
            holder.orderedItemName.setText(String.format("%s...", list.get(position).getItemName().substring(0, 19)));
        else
            holder.orderedItemName.setText(list.get(position).getItemName());

        holder.orderedItemUnitsOffered.setText(list.get(position).getOfferedUnits());
        holder.orderedItemUnits.setText(String.format("Rs. %s", list.get(position).getOrderedUnits()));
        holder.orderedItemSellingPrice.setText(String.format("Rs. %s", list.get(position).getItemSellingPrice()));
        holder.orderedItemTotalPrice.setText(list.get(position).getItemTotalCost());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class OrdersViewHolder extends RecyclerView.ViewHolder {
        public ImageView orderedItemImage;
        public TextView orderedItemName, orderedItemUnitsOffered, orderedItemUnits, orderedItemSellingPrice, orderedItemTotalPrice;

        public OrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            orderedItemImage = (ImageView) itemView.findViewById(R.id.orderedItemImage);
            orderedItemName = (TextView) itemView.findViewById(R.id.orderedItemName);
            orderedItemUnitsOffered = (TextView) itemView.findViewById(R.id.orderedItemUnitsOffered);
            orderedItemUnits = (TextView) itemView.findViewById(R.id.orderedItemUnits);
            orderedItemSellingPrice = (TextView) itemView.findViewById(R.id.orderedItemSellingPrice);
            orderedItemTotalPrice = (TextView) itemView.findViewById(R.id.orderedItemTotalPrice);
        }
    }
}

