package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
    EditText etNuevaTarea;
    Button btnAgregarDescripcion;
    Button btnGuardarTarea;

    List<Tarea> misTareas;
    AdaptadorEstudiante adaptador;
    String descripcionTemporal = "";

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
        etNuevaTarea = findViewById(R.id.etNuevaTarea);
        btnAgregarDescripcion = findViewById(R.id.btnAgregarDescripcion);
        btnGuardarTarea = findViewById(R.id.btnGuardarTarea);

        String[] categorias = {"Universidad", "Casa", "Trabajo"};
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categorias);
        spinnerCategoria.setAdapter(adapterSpinner);

        progressBar.setProgress(50);
        ratingBar.setRating(3f);
        rbPendiente.setChecked(true);

        misTareas = new ArrayList<>();
        adaptador = new AdaptadorEstudiante(misTareas);

        recyclerViewTareas.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTareas.setAdapter(adaptador);

        btnAgregarDescripcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = new EditText(TareasActivity.this);
                input.setMinLines(5);
                input.setGravity(android.view.Gravity.TOP | android.view.Gravity.START);
                input.setHint("Escribe todos los detalles de la tarea aquí...");

                new AlertDialog.Builder(TareasActivity.this)
                        .setTitle("Descripción de la tarea")
                        .setView(input)
                        .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                descripcionTemporal = input.getText().toString();
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });

        btnGuardarTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = etNuevaTarea.getText().toString();

                if (!nombre.isEmpty()) {
                    misTareas.add(new Tarea(nombre, descripcionTemporal));
                    adaptador.notifyDataSetChanged();
                    etNuevaTarea.setText("");
                    descripcionTemporal = "";
                }
            }
        });
    }

    class Tarea {
        String nombre;
        String descripcion;

        public Tarea(String nombre, String descripcion) {
            this.nombre = nombre;
            this.descripcion = descripcion;
        }
    }

    class AdaptadorEstudiante extends RecyclerView.Adapter<AdaptadorEstudiante.ViewHolder> {
        List<Tarea> lista;

        public AdaptadorEstudiante(List<Tarea> lista) {
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
            Tarea tareaActual = lista.get(position);
            holder.texto.setText(tareaActual.nombre);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(holder.itemView.getContext())
                            .setTitle(tareaActual.nombre)
                            .setMessage(tareaActual.descripcion.isEmpty() ? "Sin descripción" : tareaActual.descripcion)
                            .setPositiveButton("OK", null)
                            .show();
                }
            });
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