package Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.notez.com.myapplication.MainActivity;
import com.notez.com.myapplication.R;

import java.util.List;

import Models.ClaimsModel;

public class MySpaceAdapter extends RecyclerView.Adapter<MySpaceAdapter.RecyclerViewHolder> {

        Context mContext;
        private List<ClaimsModel> claims_models;
        ClaimsModel generatemodel;
    public MySpaceAdapter(Context context , List<ClaimsModel> claims_models)
        {
            this.mContext=context;
            this.claims_models=claims_models;
        }



        @NonNull
        @Override
        public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.my_space_item, parent, false);
            return new RecyclerViewHolder(view);
        }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
            generatemodel=claims_models.get(position);

            holder.Subnoti.setText("Your insurance claim with CLAIM ID: "+generatemodel.getClaimid()+" has been "+generatemodel.getStatus()+" .");/*to store cid inside hint of name*/


        holder.Subnoti.setCompoundDrawables(mContext.getResources().getDrawable(R.drawable.ic_submited),null,null,null);

        if(generatemodel.getStatus().equals("SUBMITTED"))
            holder.Subnoti.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.ic_submited),null,null,null);
        if(generatemodel.getStatus().equals("APPROVED"))
            holder.Subnoti.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.ic_approved),null,null,null);
        if(generatemodel.getStatus().equals("REJECTED"))
            holder.Subnoti.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.ic_rejected),null,null,null);
        if(generatemodel.getStatus().equals("ON HOLD"))
            holder.Subnoti.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.ic_on_hold),null,null,null);

        holder.Subnoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.viewPager.setCurrentItem(1);
            }
        });

    }


        @Override
        public int getItemCount() {
            return claims_models.size();
        }
        @Override
        public int getItemViewType(int Position) {
            return Position;
        }

        public class RecyclerViewHolder extends RecyclerView.ViewHolder{

            public TextView Subnoti;
            RelativeLayout rl;

            public RecyclerViewHolder(View itemView) {
                super(itemView);
                Subnoti = (TextView)itemView.findViewById(R.id.SUBNOTI);


            }
        }

    }
