package com.example.appit;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ProductViewHolder> {

    private final Context context;
    private final List<Product> productList;

    public GridAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.nameView.setText(product.getTitle());
        holder.priceView.setText(product.getPrice() + " VND");

        Glide.with(context)
                .load(product.getThumbnail())
                .placeholder(R.drawable.shoe_placeholder)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            // SỬA LỖI: Gửi đi Document ID thay vì ID số
            intent.putExtra("PRODUCT_ID", product.getDocumentId()); 
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameView;
        TextView priceView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.gridImage);
            nameView = itemView.findViewById(R.id.gridText);
            priceView = itemView.findViewById(R.id.gridPrice);
        }
    }
}
