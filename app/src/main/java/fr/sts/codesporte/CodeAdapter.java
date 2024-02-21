package fr.sts.codesporte;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CodeAdapter extends RecyclerView.Adapter<CodeAdapter.CodeViewHolder> {
    private List<CodeItem> codeList;

    public CodeAdapter(List<CodeItem> codeList) {
        this.codeList = codeList;
    }

    @NonNull
    @Override
    public CodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_code, parent, false);
        return new CodeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CodeViewHolder holder, int position) {
        CodeItem currentItem = codeList.get(position);
        holder.textViewDescription.setText(currentItem.getDescription());
        holder.textViewCode.setText(currentItem.getCode());
    }

    @Override
    public int getItemCount() {
        return codeList.size();
    }

    public static class CodeViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewDescription;
        public TextView textViewCode;

        public CodeViewHolder(View itemView) {
            super(itemView);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
        }
    }
}