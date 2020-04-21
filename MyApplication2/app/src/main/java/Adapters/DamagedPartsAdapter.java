package Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;


import com.notez.com.myapplication.R;

import java.util.List;

import Models.Damaged_part;

public class DamagedPartsAdapter extends RecyclerView.Adapter<DamagedPartsAdapter.RecyclerViewHolder> {


        private List<Damaged_part> damModels;
        Damaged_part generatemodel;
    public DamagedPartsAdapter(Context context , List<Damaged_part> damModels)
        {
            this.damModels=damModels;
        }



        @NonNull
        @Override
        public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.damaged_part_card, parent, false);
            // SearchChemistAdapter.ViewHolder viewHolder = new SearchChemistAdapter.ViewHolder(v);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {
            generatemodel=damModels.get(position);

            holder.part_name.setText(generatemodel.getPart_name());/*to store cid inside hint of name*/
            holder.cost.setText( "₹ "+generatemodel.getCost());
            /*if(position%2==0)
                holder.rl.setBackgroundResource(R.color.tintblue);*/
        }

        @Override
        public int getItemCount() {
            return damModels.size();
        }
        @Override
        public int getItemViewType(int Position) {
            return Position;
        }

        public class RecyclerViewHolder extends RecyclerView.ViewHolder{

            public TextView part_name,cost;
            RelativeLayout rl;

            public RecyclerViewHolder(View itemView) {
                super(itemView);
                part_name = (TextView)itemView.findViewById(R.id.part_name);
                cost = (TextView)itemView.findViewById(R.id.cost);

            }
        }


    }


