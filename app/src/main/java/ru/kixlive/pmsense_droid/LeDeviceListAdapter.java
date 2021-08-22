package ru.kixlive.pmsense_droid;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LeDeviceListAdapter extends RecyclerView.Adapter<LeDeviceListAdapter.ViewHolder> implements View.OnClickListener {

    private static final String TAG = LeDeviceListAdapter.class.getCanonicalName();
    private List<BluetoothDevice> devices;

    public LeDeviceListAdapter(List<BluetoothDevice> devices) {
        this.devices=devices;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_device, parent, false);
        ViewHolder holder = new ViewHolder(v);
        holder.itemView.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BluetoothDevice device = devices.get(position);
        final String deviceName = device.getName();
        if (deviceName != null && deviceName.length() > 0){
            holder.deviceName.setText(deviceName);
        } else {
            holder.deviceName.setText(R.string.unknown_device);
        }

        holder.deviceAddress.setText(device.getAddress());
        holder.itemView.setTag(device);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void add(BluetoothDevice device, int position) {
        if(!devices.contains(device)) {
            devices.add(position, device);
            notifyItemInserted(position);
        }
    }
    public void add(BluetoothDevice device) {
        if(!devices.contains(device)) {
            devices.add(device);
            notifyItemInserted(devices.size()-1);
        }
    }

    public void clear(){
        devices.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
    }


    public static class ViewHolder extends RecyclerView.ViewHolder  {
        private final TextView deviceName;
        private final TextView deviceAddress;

        public ViewHolder(View itemView) {
            super(itemView);
            deviceName = (TextView) itemView.findViewById(R.id.device_name);
            deviceAddress = (TextView) itemView.findViewById(R.id.device_address);
        }
    }
}
