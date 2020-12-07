package com.blucore.chalochale.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blucore.chalochale.R;

import java.util.ArrayList;
import java.util.List;

public class CabListAadapter extends  RecyclerView.Adapter<CabListAadapter.ViewHolder> {
    ArrayList cabNames;
    Context context;
    public CabListAadapter(Context context, ArrayList cabNames) {
        this.context = context;
        this.cabNames = cabNames;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cab_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.Cabname.setText(cabNames.get(position).toString());

    }

    @Override
    public int getItemCount() {
        return cabNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView Cabname;
        TextView CabTime;
        TextView CabPrice;
        ImageView Cabimage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Cabname = (TextView) itemView.findViewById(R.id.tvCabName);
            Cabimage = (ImageView) itemView.findViewById(R.id.Cabimage);
            CabPrice = (TextView) itemView.findViewById(R.id.tvCabprice);
            CabTime = (TextView) itemView.findViewById(R.id.tvTime);


        }
    }
}
