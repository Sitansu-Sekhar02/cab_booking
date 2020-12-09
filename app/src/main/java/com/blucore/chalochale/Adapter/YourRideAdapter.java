package com.blucore.chalochale.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blucore.chalochale.Model.CabBookingModel;
import com.blucore.chalochale.Model.YourRideModel;
import com.blucore.chalochale.R;

import java.util.ArrayList;
import java.util.List;

public class YourRideAdapter extends  RecyclerView.Adapter<YourRideAdapter.ViewHolder>{
    ArrayList source;
    private List<YourRideModel> mModel;
    Context context;

    public YourRideAdapter() {

    }


    @NonNull
    @Override
    public YourRideAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_userriding, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull YourRideAdapter.ViewHolder holder, int position) {
        holder.source.setText(source.get(position).toString());
        //holder.imageView.setImageResource(listdata[position].getImgId());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView Date;
        TextView Cab_no;
        TextView total_price;
        TextView source;
        TextView destination;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Date = (TextView) itemView.findViewById(R.id.date);
            Cab_no = (TextView) itemView.findViewById(R.id.cab_no);
            total_price = (TextView) itemView.findViewById(R.id.total_price);
            source = (TextView) itemView.findViewById(R.id.source_address);
            destination = (TextView) itemView.findViewById(R.id.dest_address);

        }
    }
}
