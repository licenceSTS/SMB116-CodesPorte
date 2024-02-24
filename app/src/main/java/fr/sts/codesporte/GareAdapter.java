package fr.sts.codesporte;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GareAdapter extends RecyclerView.Adapter<GareAdapter.GareViewHolder> {
    private final List<GareItem> gareList;
    private OnItemClickListener listener;

    public GareAdapter(List<GareItem> gareList) {
        this.gareList = gareList;
    }

    @NonNull
    @Override
    public GareViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gare, parent, false);
        return new GareViewHolder(itemView, listener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull GareViewHolder holder, int position) {
        GareItem currentItem = gareList.get(position);
        holder.gareName.setText(currentItem.getName());
        holder.gareCodeCount.setText("Nombre de codes: " + currentItem.getCodes().size());
    }

    @Override
    public int getItemCount() {
        return gareList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class GareViewHolder extends RecyclerView.ViewHolder {
        public TextView gareName;
        public TextView gareCodeCount;

        public GareViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            gareName = itemView.findViewById(R.id.gare_name);
            gareCodeCount = itemView.findViewById(R.id.gare_code_count);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}