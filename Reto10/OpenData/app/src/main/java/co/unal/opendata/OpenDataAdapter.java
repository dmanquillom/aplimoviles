package co.unal.opendata;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OpenDataAdapter extends RecyclerView.Adapter<OpenDataAdapter.OpenDataViewHolder> {

    List<OpenData> openDataList;

    public OpenDataAdapter(List<OpenData> openDataList){
        this.openDataList = openDataList;
    }

    @NonNull
    @Override
    public OpenDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_layout, parent, false);
        OpenDataViewHolder holder = new OpenDataViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull OpenDataViewHolder holder, int position) {
        OpenData openData = openDataList.get(position);
        holder.setCandidato(openData.getCandidato());
        holder.setVotos(openData.getVotos());
        holder.setPartido(openData.getPartido());
        holder.setPuesto(openData.getPuesto());
    }

    @Override
    public int getItemCount() {
        return openDataList.size();
    }

    public static class OpenDataViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public OpenDataViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setCandidato(String candidato){
            TextView tvResultName = (TextView) mView.findViewById(R.id.tvResultName);
            tvResultName.setText(candidato);
        }

        public void setVotos(int votos) {
            TextView tvResultVotes = (TextView) mView.findViewById(R.id.tvResultVotes);
            tvResultVotes.setText(String.valueOf(votos));
        }

        public void setPartido(String partido) {
            TextView tvResultPartido = (TextView) mView.findViewById(R.id.tvResultPartido);
            tvResultPartido.setText(String.valueOf(partido));
        }

        public void setPuesto(String puesto) {
            TextView tvResultPuesto = (TextView) mView.findViewById(R.id.tvResultPuesto);
            tvResultPuesto.setText(String.valueOf(puesto));
        }
    }
}