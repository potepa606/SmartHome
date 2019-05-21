package com.example.smarthome;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    //-------------------- Przwijanko Urzadzen---------------------------------//


    private Context dContext;
    private List<Device> mDevice;
    private OnItemClickListener mListener;


    public DeviceAdapter(Context context, List<Device> devices) {
        dContext = context;
        mDevice = devices;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(dContext).inflate(R.layout.items_devices, parent, false);
        return new DeviceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        Device newDewice = mDevice.get(position);

        holder.textViewName.setText(newDewice.getName());
        holder.idDevice.setText(newDewice.getID());
        holder.ValueofStan.setText(newDewice.getStan());


        Picasso.get()
                .load("https://www.gstatic.com/mobilesdk/160503_mobilesdk/logo/2x/firebase_28dp.png")
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageView);


    }



    @Override
    public int getItemCount() {
        return mDevice.size();
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{
        public TextView textViewName,idDevice,ValueofStan;
        public ImageView imageView;

        public DeviceViewHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.ImageName);
            idDevice = itemView.findViewById(R.id.id_dev);
            ValueofStan = itemView.findViewById(R.id.ValueofStan);
            imageView = itemView.findViewById(R.id.image);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);


        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem doWhatever = menu.add(Menu.NONE, 1, 1, "Do whatever");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "Delete");

            doWhatever.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    switch (item.getItemId()) {
                        case 1:
                            mListener.onWhatEverClick(position);
                            return true;
                        case 2:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onWhatEverClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
