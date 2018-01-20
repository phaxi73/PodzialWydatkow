package com.example.bartek.podzialwydatkow;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by igor on 19.01.18.
 */

public class ExpenseDetailsRecyclerViewAdapter extends RecyclerView.Adapter<ExpenseDetailsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Friends> listOfFriends;

    public ExpenseDetailsRecyclerViewAdapter(ArrayList<Friends> listOfFriends) {
        this.listOfFriends = listOfFriends;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.payer_single_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Friends payer = listOfFriends.get(position);
        // Obliczamy dług
        String payerDebtValue = String.format("%.2f", payer.amount) + " zł";
        // Bindingi do elementów UI
        holder.payerName.setText(payer.name);
        holder.payerEmail.setText(payer.email);
        holder.payerDebt.setText(payerDebtValue);

        if (payer.thumb_image.equals("default")) {
            Picasso.with(holder.payerImage.getContext()).load(R.drawable.account_icon_orange).into(holder.payerImage);
        } else {
            Picasso.with(holder.payerImage.getContext()).load(payer.thumb_image).into(holder.payerImage);
        }

        holder.itemView.setTag(payer);
    }

    @Override
    public int getItemCount() {
        return listOfFriends.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView payerImage;
        public TextView payerName;
        public TextView payerEmail;
        public TextView payerDebt;

        public ViewHolder(View itemView) {
            super(itemView);
            payerImage = itemView.findViewById(R.id.payer_single_image);
            payerName = itemView.findViewById(R.id.payer_single_name);
            payerEmail = itemView.findViewById(R.id.payer_single_email);
            payerDebt = itemView.findViewById(R.id.payer_single_debt);

        }
    }
}