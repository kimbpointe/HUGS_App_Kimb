package com.example.hugsapp.models;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.example.hugsapp.R;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ItemHolder> {

    List<BluetoothDevice> deviceItemList;
    Context context;

    private final OnDeviceItemListener mOnDeviceItemListener;
    Settings settings;

    public DeviceAdapter(List<BluetoothDevice> deviceItemList, Context context, OnDeviceItemListener OnDeviceItemListener) {
        this.deviceItemList = deviceItemList;
        this.context = context;
        this.mOnDeviceItemListener = OnDeviceItemListener;
        settings = new Settings(context);
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false);
        return new ItemHolder(view, mOnDeviceItemListener);
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        //Product object in list being clicked
        BluetoothDevice device = deviceItemList.get(position);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {}
        holder.name.setText(device.getName());
        holder.id.setText(device.getAddress());

        if (device.getName().contains("Arduino")) {
            holder.layout.setBackground(AppCompatResources.getDrawable(context, R.drawable.list_button_highlight));
            holder.name.setTextColor(context.getColor(R.color.white));
            holder.id.setTextColor(context.getColor(R.color.white));
            holder.icon.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

        } else {
            holder.layout.setBackground(AppCompatResources.getDrawable(context, R.drawable.list_button));
            holder.name.setTextColor(context.getColor(R.color.dark_blue));
            holder.id.setTextColor(context.getColor(R.color.dark_blue));
            holder.icon.setColorFilter(ContextCompat.getColor(context, R.color.dark_blue), android.graphics.PorterDuff.Mode.SRC_IN);

        }


    }

    @Override
    public int getItemCount() {
        return deviceItemList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name, id;
        ConstraintLayout layout;
        ImageView icon;

        OnDeviceItemListener onDeviceItemListener;

        public ItemHolder(@NonNull View itemView, OnDeviceItemListener onDeviceItemListener) {
            super(itemView);
            this.onDeviceItemListener = onDeviceItemListener;
            name = itemView.findViewById(R.id.deviceName);
            id = itemView.findViewById(R.id.deviceID);
            icon = itemView.findViewById(R.id.icon);
            layout = itemView.findViewById(R.id.deviceItemLayout);

            //Set on click listeners for inner list items
            layout.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
                onDeviceItemListener.OnDeviceItemClick(view, getAdapterPosition());
        }
    }


    public interface OnDeviceItemListener {
        void OnDeviceItemClick(View v, int position);
    }

}