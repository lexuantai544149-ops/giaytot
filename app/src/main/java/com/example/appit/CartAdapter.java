package com.example.appit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final Context context;
    private final List<Product> cartItems;

    public CartAdapter(Context context, List<Product> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = cartItems.get(position);

        holder.name.setText(product.getTitle());
        holder.price.setText(product.getPrice() + " VND");
        holder.checkBox.setChecked(product.isSelected());

        Glide.with(context).load(product.getThumbnail()).into(holder.image);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            product.setSelected(isChecked);
            updateTotal();
        });

        holder.removeButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                Product productToRemove = cartItems.get(currentPosition);
                CartManager.getInstance().removeProductFromCart(productToRemove, new CartManager.CartListener() {
                    @Override
                    public void onCartUpdated() {
                        notifyItemRemoved(currentPosition);
                        updateTotal();
                    }
                    @Override
                    public void onError(String message) {
                        Toast.makeText(context, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void updateTotal() {
        if (context instanceof CartActivity) {
            ((CartActivity) context).updateTotalPrice();
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, price;
        ImageButton removeButton;
        CheckBox checkBox;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.cart_item_image);
            name = itemView.findViewById(R.id.cart_item_name);
            price = itemView.findViewById(R.id.cart_item_price);
            removeButton = itemView.findViewById(R.id.btn_remove_from_cart);
            checkBox = itemView.findViewById(R.id.cart_item_checkbox);
        }
    }
}