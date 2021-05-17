package com.minor.vendorapp.Nav.Home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.minor.vendorapp.R;

import java.util.List;

class AdapterOrdersListing extends RecyclerView.Adapter<AdapterOrdersListing.OrdersViewHolder> {

    List<DataTransferOrders> list;
    Context context;

    public AdapterOrdersListing(List<DataTransferOrders> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_listing_tile, parent, false);


        return new OrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersViewHolder holder, int position) {
        if (list.get(position).getName().length() > 14)
            holder.tileDetailOrderCustomerName.setText(String.format("%s...", list.get(position).getName().substring(0, 13)));
        else
            holder.tileDetailOrderCustomerName.setText(list.get(position).getName());

        holder.tileDetailOrderId.setText(list.get(position).getOrderId());
        holder.tileDetailOrderCustomerAddress.setText(list.get(position).getAddress());
        holder.tileDetailOrderDate.setText(list.get(position).getDate());
        holder.tileDetailOrderTime.setText(list.get(position).getTime());
        holder.tileDetailOrderAmount.setText(list.get(position).getBillAmount());
        holder.tileDetailOrderStatus.setText(list.get(position).getOrderStatus());
//        holder.tileDetailOrderStatus.setBackgroundTintList(context.getResources().getColorStateList(R.color.green));

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ActivityViewOrderDetails.class);
            intent.putExtra("Object", list.get(position).getJsonObject().toString());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class OrdersViewHolder extends RecyclerView.ViewHolder {
        public TextView tileDetailOrderId, tileDetailOrderTime, tileDetailOrderDate, tileDetailOrderStatus, tileDetailOrderCustomerName, tileDetailOrderAmount,
                tileDetailOrderCustomerAddress, tileDetailOrderViewDetails;

        public OrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            tileDetailOrderId = (TextView) itemView.findViewById(R.id.tileDetailOrderId);
            tileDetailOrderTime = (TextView) itemView.findViewById(R.id.tileDetailOrderTime);
            tileDetailOrderDate = (TextView) itemView.findViewById(R.id.tileDetailOrderDate);
            tileDetailOrderStatus = (TextView) itemView.findViewById(R.id.tileDetailOrderStatus);
            tileDetailOrderCustomerName = (TextView) itemView.findViewById(R.id.tileDetailOrderCustomerName);
            tileDetailOrderAmount = (TextView) itemView.findViewById(R.id.tileDetailOrderAmount);
            tileDetailOrderCustomerAddress = (TextView) itemView.findViewById(R.id.tileDetailOrderCustomerAddress);
            tileDetailOrderViewDetails = (TextView) itemView.findViewById(R.id.tileDetailOrderViewDetails);
        }
    }
}

