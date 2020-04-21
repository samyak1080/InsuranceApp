package Adapters;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.notez.com.myapplication.ClaimEstimation;
import com.notez.com.myapplication.MainActivity;
import com.notez.com.myapplication.R;
//import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import Models.ClaimsModel;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShowClaimsAdapter extends RecyclerView.Adapter<ShowClaimsAdapter.ImageViewHolder> {
    private Context mContext;
    private List<ClaimsModel> mClaims;


    public ShowClaimsAdapter(Context context, List<ClaimsModel> claims) {
        mContext = context;
        mClaims = claims;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.showclaimsitem, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {


        ClaimsModel claimCurrent = mClaims.get(position);
        holder.claimid.setText(claimCurrent.getClaimid());
        holder.overView.setText(claimCurrent.getOverView());
        holder.status.setText(claimCurrent.getStatus());
        if(claimCurrent.getStatus().equals("APPROVED"))
            holder.show_estimation.setVisibility(View.VISIBLE);
        if(claimCurrent.getStatus().equals("SUBMITTED"))
        {

            holder.st_submitted_line.setBackgroundColor(Color.parseColor("#49AA0A"));
            holder.st_submitted.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_dot_green_24dp));

        }
        else if(claimCurrent.getStatus().equals(("APPROVED")))
        {
            holder.st_submitted_line.setBackgroundColor(Color.parseColor("#49AA0A"));
            holder.st_submitted.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_dot_green_24dp));
            holder.st_approved_line.setBackgroundColor(Color.parseColor("#49AA0A"));
            holder.st_approved.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_dot_green_24dp));
            holder.if_approved_options.setVisibility(View.VISIBLE);

        }

        else if(claimCurrent.getStatus().equals(("REJECTED")))
        {
            holder.st_submitted_line.setBackgroundColor(Color.parseColor("#aa2222"));
            holder.st_submitted.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_dot_red_24dp));
            holder.st_approved_line.setBackgroundColor(Color.parseColor("#aa2222"));
            holder.st_approved.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_dot_red_24dp));
            holder.st_accepted_line.setBackgroundColor(Color.parseColor("#aa2222"));
            holder.st_accepted.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_dot_red_24dp));
        }
        else if(claimCurrent.getStatus().equals(("ON HOLD")))
        {
            holder.st_submitted_line.setBackgroundColor(Color.parseColor("#bb7777"));
            holder.st_submitted.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_dot_pale_red));
            holder.st_approved_line.setBackgroundColor(Color.parseColor("#bb7777"));
            holder.st_approved.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_dot_pale_red));
        }

        holder.show_estimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity.estimation_required=new ClaimsModel(mClaims.get(position).getOverView(),mClaims.get(position).getImageurl(),mClaims.get(position).getStatus());
                MainActivity.estimation_required.setClaimid(mClaims.get(position).getClaimid());
                Log.e("dollar","MainActivity.estimation_required path:"+MainActivity.estimation_required.getImageurl());
                MainActivity.viewPager.setCurrentItem(2);
                ClaimEstimation.callModel();
            }
        });
       /* new DownloadImageTask(holder.proof)
                .execute(claimCurrent.getImageurl());*/
       new Fetch_Images(mClaims.get(position).getImageurl(),holder.proof).execute();


    }

    public static void LoadImageFromWebOperations(String url,ImageView imageView) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            imageView.setBackground(d);
            //return d;
        } catch (Exception e) {
            //return null;
        }

    }
    @Override
    public int getItemCount() {
        return mClaims.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView overView,status,claimid;
        public ImageView proof,st_approved,st_submitted,st_accepted,show_estimation;
        public LinearLayout st_approved_line,st_submitted_line,st_accepted_line,if_approved_options;

        public ImageViewHolder(View itemView) {
            super(itemView);
            claimid= itemView.findViewById(R.id.claimid);
            status= itemView.findViewById(R.id.progress);
            overView = itemView.findViewById(R.id.overview);
            proof = itemView.findViewById(R.id.claimproof);
            show_estimation =itemView.findViewById(R.id.show_estimation);
            st_accepted= itemView.findViewById(R.id.st_accepted);
            st_approved= itemView.findViewById(R.id.st_approved);
            st_submitted= itemView.findViewById(R.id.st_submitted);
            st_submitted_line= itemView.findViewById(R.id.st_submitted_line);
            st_approved_line= itemView.findViewById(R.id.st_approved_line);
            st_accepted_line= itemView.findViewById(R.id.st_accepted_line);

            if_approved_options= itemView.findViewById(R.id.if_approved_options);
        }
    }

    public class  Fetch_Images extends AsyncTask<Void,Void,Bitmap>{
        String path;
        Bitmap image;
        ImageView i2;

        public Fetch_Images(String path,ImageView i2){
            this.path=path;
            this.i2=i2;
        }


        @Override
        protected Bitmap doInBackground(Void... voids) {

            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("path", path)
                        .build();


                Request request = new Request.Builder()
                        .url(mContext.getResources().getString(R.string.api_server)+"download/"+path)
                        .post(requestBody)
                        .build();

                Log.e("Request","sent");

                Response response = client.newCall(request).execute();
                Log.e("Response",response.message()+"      "+response.body());
                InputStream inputStream = response.body().byteStream();
                image = BitmapFactory.decodeStream(inputStream);

            } catch (IOException e) {
                Log.e("IO",e.getMessage());
            }
            return image;
        }


        @Override
        protected void onPostExecute(Bitmap s) {
            i2.setImageBitmap(s);
            Log.e("ImageRequest","Post");

            super.onPostExecute(s);
        }
    }



}