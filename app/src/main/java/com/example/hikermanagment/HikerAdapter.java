package com.example.hikermanagment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.List;

public class HikerAdapter extends RecyclerView.Adapter<HikerAdapter.ContactViewHolder> {

    private List<Hiker> hikers;
    private IClickItemHiker iClickItemHiker;
    public interface IClickItemHiker{
        void updateHiker(Hiker hiker);

        void deleteHiker(Hiker hiker);
    }

    public HikerAdapter(IClickItemHiker iClickItemHiker) {
        this.iClickItemHiker=iClickItemHiker;
    }

    public void setData(List<Hiker> hikerList){
        this.hikers=hikerList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(com.example.hikermanagment.R.layout.item_contact_card, parent, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Hiker hiker = hikers.get(position);
        holder.hikerName.setText(hiker.name);
        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                iClickItemHiker.updateHiker(hiker);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickItemHiker.deleteHiker(hiker);
            }
        });
    }
    @Override
    public int getItemCount() {return hikers.size();}
    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView hikerName;
        Button btnView, btnDelete;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            hikerName=itemView.findViewById(R.id.hikerName);
            btnView=itemView.findViewById(R.id.btnView);
            btnDelete=itemView.findViewById(R.id.btnDelete);


        }
    }
}
