package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TareasActivity extends AppCompatActivity {

    Spinner spinnerCategoria;
    ProgressBar progressBar;
    RatingBar ratingBar;
    RadioGroup radioGroupEstado;
    RadioButton rbPendiente;
    CheckBox cbUrgente;
    RecyclerView recyclerViewTareas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tareas);

        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        progressBar = findViewById(R.id.progressBar);
        ratingBar = findViewById(R.id.ratingBar);
        radioGroupEstado = findViewById(R.id.radioGroupEstado);
        rbPendiente = findViewById(R.id.rbPendiente);
        cbUrgente = findViewById(R.id.cbUrgente);
        recyclerViewTareas = findViewById(R.id.recyclerViewTareas);

        String[] categorias = {"Universidad", "Casa", "Trabajo"};
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categorias);
        spinnerCategoria.setAdapter(adapterSpinner);

        progressBar.setProgress(45);
        ratingBar.setRating(3.5f);
        rbPendiente.setChecked(true);

        List<String> misTareas = new ArrayList<>();
        misTareas.add("Terminar la app EasyTasks");
        misTareas.add("Subir código a GitHub");
        misTareas.add("Entregar evaluación en Aula Virtual");

        recyclerViewTareas.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTareas.setAdapter(new AdaptadorEstudiante(misTareas));
    }

    class AdaptadorEstudiante extends RecyclerView.Adapter<AdaptadorEstudiante.ViewHolder> {
        List<String> lista;

        public AdaptadorEstudiante(List<String> lista) {
            this.lista = lista;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.texto.setText(lista.get(position));
        }

        @Override
        public int getItemCount() {
            return lista.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView texto;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                texto = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}