package com.blucore.chalochale.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.blucore.chalochale.Model.CabBookingModel;
import com.blucore.chalochale.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class CabListAadapter extends  RecyclerView.Adapter<CabListAadapter.ViewHolder> {
    ArrayList cabNames;
    //private int selectedPosition = 0;


    private List<CabBookingModel> mModel;
    Context context;
    private static int lastClickedPosition = -1;
    private int selectedItem;

    public CabListAadapter(Context context, ArrayList cabNames) {
        this.context = context;
        this.cabNames = cabNames;
        selectedItem = 0;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cab_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull  ViewHolder holder, final int position) {

        holder.Cabname.setText(cabNames.get(position).toString());

        holder.cabList.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));

        if (selectedItem == position) {
            holder.cabList.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDarkR));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int previousItem = selectedItem;
                selectedItem = position;
                notifyItemChanged(previousItem);
                notifyItemChanged(position);

            }
        });


        /*if(position == selectedPosition){
            holder.itemView.setSelected(true);
        } else {
            holder.itemView.setSelected(false);
        }

        // Actual selection / deselection logic
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getLayoutPosition();
                if(selectedPosition != currentPosition){
                    // Temporarily save the last selected position
                    int lastSelectedPosition = selectedPosition;
                    // Save the new selected position
                    selectedPosition = currentPosition;
                    // update the previous selected row
                    notifyItemChanged(lastSelectedPosition);
                    // select the clicked row
                    holder.itemView.setSelected(true);
                }
            }
        });*/
        /*Glide.with(context)
                .load(mModel.get(position).getCab_image())
                .into(holder.Cabimage);
        holder.CabTime.setText(mModel.get(position).getCab_time());
        holder.CabPrice.setText(mModel.get(position).getCab_price());*/

    }

    @Override
    public int getItemCount() {
        return cabNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cabList;
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
            cabList=itemView.findViewById(R.id.card_view);
            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int positions = getAdapterPosition();
                        if (positions != RecyclerView.NO_POSITION){
                            listener.onItemClick(positions);
                            selected_position = positions;
                            notifyDataSetChanged();
                        }
                    }
                }
            });*/


        }
    }
}
