package com.minor.vendorapp.ui.products;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.minor.vendorapp.ActivityProductAdd;
import com.minor.vendorapp.Helpers.Functions;
import com.minor.vendorapp.R;

import org.json.JSONObject;

import java.util.List;

class AdapterProductsListing extends RecyclerView.Adapter<AdapterProductsListing.ProductsViewHolder> {

    List<DataTransfer> list;
    Context context;

    public AdapterProductsListing(List<DataTransfer> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_listing_tile, parent, false);


        return new ProductsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsViewHolder holder, int position) {
        if (list.get(position).getName().length() > 14)
            holder.tileDetailProductName.setText(String.format("%s...", list.get(position).getName().substring(0, 13)));
        else
            holder.tileDetailProductName.setText(list.get(position).getName());

        holder.tileDetailProductDescription.setText(list.get(position).getDescription());
        holder.tileDetailProductQuantity.setText(list.get(position).getQuantity());
        holder.tileDetailProductSellingPrice.setText(list.get(position).getSellingPrice());

        holder.tileDetailProductMRP.setText(list.get(position).getMRP());
        holder.tileDetailProductMRP.setPaintFlags(holder.tileDetailProductMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.tileDetailProductImage.setImageBitmap(Functions.base64ToBitmap(list.get(position).getImage()));
//        holder.tileDetailProductImage.setImageResource(null);
        Log.i("Photo", "" + Functions.base64ToBitmap(list.get(position).getImage()));

        holder.itemView.setOnClickListener(view -> {
//            Toast.makeText(context, " "+ position, Toast.LENGTH_SHORT).show();
            JSONObject jsonObject = new JSONObject();
            Intent intent = new Intent(context, ActivityProductAdd.class);
            intent.putExtra("Object", list.get(position).getJsonObject().toString());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ProductsViewHolder extends RecyclerView.ViewHolder {
        public ImageView tileDetailProductImage;
        public TextView tileDetailProductName, tileDetailProductDescription, tileDetailProductQuantity, tileDetailProductSellingPrice, tileDetailProductMRP;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            tileDetailProductImage = (ImageView) itemView.findViewById(R.id.tileDetailProductImage);
            tileDetailProductName = (TextView) itemView.findViewById(R.id.tileDetailProductName);
            tileDetailProductDescription = (TextView) itemView.findViewById(R.id.tileDetailProductDescription);
            tileDetailProductQuantity = (TextView) itemView.findViewById(R.id.tileDetailProductQuantity);
            tileDetailProductSellingPrice = (TextView) itemView.findViewById(R.id.tileDetailProductSellingPrice);
            tileDetailProductMRP = (TextView) itemView.findViewById(R.id.tileDetailProductMRP);
        }
    }
}
