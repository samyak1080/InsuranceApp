package Adapters;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.infosys.touchless.claims.ClaimsDecisioning;
import com.infosys.touchless.claims.MainActivity;
import com.infosys.touchless.claims.R;

//import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        View v = LayoutInflater.from(mContext).inflate(R.layout.recy_show_claim_sitem, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        final ClaimsModel claimCurrent = mClaims.get(position);
        holder.claimid.setText(claimCurrent.getClaimid());
        holder.overView.setText(claimCurrent.getOverView());
        holder.status.setText(claimCurrent.getStatus());

        holder.show_estimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity.estimation_required=new ClaimsModel(mClaims.get(position).getOverView(),mClaims.get(position).getImageurl(),mClaims.get(position).getStatus());
                MainActivity.estimation_required.setClaimid(mClaims.get(position).getClaimid());
                MainActivity.estimation_required.setIncident_date(mClaims.get(position).getIncident_date());


                Log.e("dollar","MainActivity.estimation_required path:"+MainActivity.estimation_required.getImageurl());
                MainActivity.viewPager.setCurrentItem(2);
                ClaimsDecisioning.callModel();

            }
        });

     new Fetch_Images(mClaims.get(position).getImageurl(),holder.proof).execute();

    }


    @Override
    public int getItemCount() {
        return mClaims.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView overView,status,claimid;
        public ImageView proof,show_estimation;

        public ImageViewHolder(View itemView) {
            super(itemView);
            claimid= itemView.findViewById(R.id.claimid);
            show_estimation =itemView.findViewById(R.id.show_estimation);
            status= itemView.findViewById(R.id.progress);
        //    approve= itemView.findViewById(R.id.but);
            overView = itemView.findViewById(R.id.overview);
            proof = itemView.findViewById(R.id.claimproof);
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

    public class  UpdateStatus extends AsyncTask<Void,Void, JSONObject>{
        String status,claimid;
        CardView cd;
        TextView tv_status;
        public UpdateStatus(String status,String claimid,TextView tv_status,CardView cd){
            this.status=status;
            this.tv_status=tv_status;
            this.cd=cd;
            this.claimid=claimid;
        }


        @Override
        protected JSONObject doInBackground(Void... voids) {

            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("claimid",claimid)
                        .addFormDataPart("status",status)
                        .build();


                Request request = new Request.Builder()
                        .url(mContext.getResources().getString(R.string.api_server)+"update_status")
                        .post(requestBody)
                        .build();

                Log.e("Request","sent");

                Response response = client.newCall(request).execute();
                InputStream inputStream = response.body().byteStream();
                InputStreamReader isReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(isReader);
                StringBuffer sb = new StringBuffer();
                String str;
                while((str = reader.readLine())!= null){
                    sb.append(str);
                }
                Log.e("Response",sb.toString());

                JSONObject myresponse= new JSONObject(sb.toString());

                Log.e("Request",response.message());
                return myresponse;

            } catch (IOException e) {
                Log.e("IO",e.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();

            }
        return null;
        }


        @Override
        protected void onPostExecute(JSONObject s) {
          //  i2.setImageBitmap(s);
            Log.e("ImageRequest","Post");
            try{
                if(s.getString("Status Updated").equals("true")){
                    Toast.makeText(mContext,"Claim Approved succesfully.",Toast.LENGTH_LONG).show();
                    tv_status.setText("APPROVED");
                    cd.setVisibility(View.INVISIBLE);
                }
                else{
                    Toast.makeText(mContext,"Claim Approval failed.",Toast.LENGTH_LONG).show();

                }
            }
            catch (Exception e){

            }
            super.onPostExecute(s);
        }
    }

}