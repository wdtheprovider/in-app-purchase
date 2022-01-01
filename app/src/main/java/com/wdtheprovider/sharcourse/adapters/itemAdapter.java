package com.wdtheprovider.sharcourse.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.wdtheprovider.sharcourse.R;

import org.w3c.dom.Text;

import java.util.List;

public class itemAdapter extends RecyclerView.Adapter<itemAdapter.ViewHolder> {

    List<SkuDetails> list;
    Context context;
    Activity activity;


    public itemAdapter(List<SkuDetails> list, Context context, Activity activity) {
        this.list = list;
        this.context = context;
        this.activity = activity;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SkuDetails item = list.get(position);

        holder.clicks.setText(item.getDescription());
        holder.btn_buy.setText(item.getPrice());

        Log.d("itemA", " " + list.size());
        Log.d("itemA", " " + item.getPrice());

    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView clicks;
        Button btn_buy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            clicks = itemView.findViewById(R.id.clickss);
            btn_buy = itemView.findViewById(R.id.btn_buy);
        }
    }
}
