package com.example.appit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private final Context context;
    private final List<Address> addressList;

    public AddressAdapter(Context context, List<Address> addressList) {
        this.context = context;
        this.addressList = addressList;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.address_item, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addressList.get(position);

        holder.recipientName.setText(address.getRecipientName());
        holder.phone.setText(address.getPhone());
        
        String fullAddress = String.format("%s, %s, %s", 
            address.getStreet(), 
            address.getDistrict(), 
            address.getCity());
        holder.fullAddress.setText(fullAddress);

        if (address.isDefault()) {
            holder.defaultBadge.setVisibility(View.VISIBLE);
        } else {
            holder.defaultBadge.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView recipientName, phone, fullAddress, defaultBadge;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            recipientName = itemView.findViewById(R.id.address_recipient_name);
            phone = itemView.findViewById(R.id.address_phone);
            fullAddress = itemView.findViewById(R.id.address_full);
            defaultBadge = itemView.findViewById(R.id.address_default_badge);
        }
    }
}
