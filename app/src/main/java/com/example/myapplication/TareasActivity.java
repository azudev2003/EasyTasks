package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class TareasActivity extends AppCompatActivity {

    Spinner spinnerCategoria;
    ProgressBar progressBar;
    RatingBar ratingBar;
    RadioGroup radioGroupEstado;
    RadioButton rbPendiente, rbEnProceso, rbCompletada;
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
        rbEnProceso = findViewById(R.id.rbEnProceso);
        rbCompletada = findViewById(R.id.rbCompletada);

        cbUrgente = findViewById(R.id.cbUrgente);
        recyclerViewTareas = findViewById(R.id.recyclerViewTareas);
        etNuevaTarea = findViewById(R.id.etNuevaTarea);
        btnAgregarDescripcion = findViewById(R.id.btnAgregarDescripcion);
        btnGuardarTarea = findViewById(R.id.btnGuardarTarea);

        String[] categorias = {"Universidad", "Casa", "Trabajo"};
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categorias) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setTextColor(Color.BLACK);
                return view;
            }
        };
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapterSpinner);

        ratingBar.setRating(3f);
        rbPendiente.setChecked(true);

        misTareas = new ArrayList<>();
        cargarTareasSharedPrefs();
        ordenarTareas(); // Ordenar al iniciar

        adaptador = new AdaptadorEstudiante(misTareas);
        recyclerViewTareas.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTareas.setAdapter(adaptador);
        actualizarProgresoGeneral();

        btnAgregarDescripcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = new EditText(TareasActivity.this);
                input.setMinLines(5);
                input.setGravity(android.view.Gravity.TOP | android.view.Gravity.START);
                input.setHint("Escribe todos los detalles de la tarea aquí...");
                input.setText(descripcionTemporal);

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
                    String estadoSeleccionado = "Pendiente";
                    int idSeleccionado = radioGroupEstado.getCheckedRadioButtonId();
                    if (idSeleccionado == rbEnProceso.getId()) {
                        estadoSeleccionado = "En Proceso";
                    } else if (idSeleccionado == rbCompletada.getId()) {
                        estadoSeleccionado = "Completada";
                    }

                    boolean esUrgente = cbUrgente.isChecked();
                    float estrellas = ratingBar.getRating();
                    misTareas.add(new Tarea(nombre, descripcionTemporal, estadoSeleccionado, esUrgente, estrellas));
                    ordenarTareas();
                    guardarTareasSharedPrefs();
                    adaptador.notifyDataSetChanged();
                    actualizarProgresoGeneral();

                    etNuevaTarea.setText("");
                    descripcionTemporal = "";
                    cbUrgente.setChecked(false);
                    rbPendiente.setChecked(true);
                    ratingBar.setRating(3f);
                }
            }
        });
    }

    private void ordenarTareas() {
        // Ordenar de mayor a menor importancia (estrellas)
        java.util.Collections.sort(misTareas, new java.util.Comparator<Tarea>() {
            @Override
            public int compare(Tarea t1, Tarea t2) {
                return Float.compare(t2.importancia, t1.importancia);
            }
        });
    }

    private void actualizarProgresoGeneral() {
        if (misTareas.isEmpty()) {
            progressBar.setProgress(0);
            return;
        }
        int completadas = 0;
        for (Tarea t : misTareas) {
            if (t.estado.equals("Completada")) {
                completadas++;
            }
        }
        int porcentaje = (completadas * 100) / misTareas.size();
        progressBar.setProgress(porcentaje);
    }

    private void guardarTareasSharedPrefs() {
        SharedPreferences prefs = getSharedPreferences("MisTareasDB", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray jsonArray = new JSONArray();

        try {
            for (Tarea t : misTareas) {
                JSONObject obj = new JSONObject();
                obj.put("nombre", t.nombre);
                obj.put("descripcion", t.descripcion);
                obj.put("estado", t.estado);
                obj.put("urgente", t.urgente);
                obj.put("importancia", (double) t.importancia);
                jsonArray.put(obj);
            }
            editor.putString("lista_tareas", jsonArray.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void cargarTareasSharedPrefs() {
        SharedPreferences prefs = getSharedPreferences("MisTareasDB", Context.MODE_PRIVATE);
        String jsonString = prefs.getString("lista_tareas", null);
        if (jsonString != null) {
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    float imp = 3f;
                    if (obj.has("importancia")) {
                        imp = (float) obj.getDouble("importancia");
                    }
                    misTareas.add(new Tarea(
                            obj.getString("nombre"),
                            obj.getString("descripcion"),
                            obj.getString("estado"),
                            obj.getBoolean("urgente"),
                            imp
                    ));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class Tarea {
        String nombre;
        String descripcion;
        String estado;
        boolean urgente;
        float importancia;

        public Tarea(String nombre, String descripcion, String estado, boolean urgente, float importancia) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.estado = estado;
            this.urgente = urgente;
            this.importancia = importancia;
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarea, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Tarea tareaActual = lista.get(position);
            holder.tvTitulo.setText(tareaActual.nombre);
            holder.tvEstado.setText("Estado: " + tareaActual.estado);

            if (tareaActual.urgente) {
                holder.tvTitulo.setTextColor(Color.RED);
                holder.tvUrgenteBadge.setVisibility(View.VISIBLE);
            } else {
                holder.tvTitulo.setTextColor(Color.BLACK);
                holder.tvUrgenteBadge.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                    builder.setTitle(tareaActual.nombre);
                    builder.setMessage("Descripción:\n" + (tareaActual.descripcion.isEmpty() ? "Sin descripción" : tareaActual.descripcion) + "\n\nEstado: " + tareaActual.estado);

                    builder.setPositiveButton("Completar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tareaActual.estado = "Completada";
                            guardarTareasSharedPrefs();
                            notifyDataSetChanged();
                            actualizarProgresoGeneral();
                        }
                    });

                    builder.setNeutralButton("Editar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mostrarDialogoEditar(tareaActual, position);
                        }
                    });

                    builder.setNegativeButton("Eliminar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            misTareas.remove(position);
                            guardarTareasSharedPrefs();
                            notifyDataSetChanged();
                            actualizarProgresoGeneral();
                        }
                    });

                    builder.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return lista.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitulo, tvEstado, tvUrgenteBadge;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitulo = itemView.findViewById(R.id.tvTituloTarea);
                tvEstado = itemView.findViewById(R.id.tvEstadoTarea);
                tvUrgenteBadge = itemView.findViewById(R.id.tvUrgenteBadge);
            }
        }
    }

    private void mostrarDialogoEditar(Tarea tarea, int position) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 30, 40, 30);

        final EditText inputNombre = new EditText(this);
        inputNombre.setText(tarea.nombre);
        inputNombre.setHint("Nombre de la tarea");
        layout.addView(inputNombre);

        final EditText inputDesc = new EditText(this);
        inputDesc.setText(tarea.descripcion);
        inputDesc.setHint("Descripción");
        layout.addView(inputDesc);

        final CheckBox checkUrgente = new CheckBox(this);
        checkUrgente.setText("Marcar como Urgente");
        checkUrgente.setChecked(tarea.urgente);
        layout.addView(checkUrgente);

        final Spinner spinnerEstado = new Spinner(this);
        String[] estados = {"Pendiente", "En Proceso", "Completada"};
        ArrayAdapter<String> adapterEst = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, estados);
        spinnerEstado.setAdapter(adapterEst);

        if(tarea.estado.equals("En Proceso")) spinnerEstado.setSelection(1);
        else if(tarea.estado.equals("Completada")) spinnerEstado.setSelection(2);
        else spinnerEstado.setSelection(0);

        layout.addView(spinnerEstado);

        new AlertDialog.Builder(this)
                .setTitle("Editar Tarea")
                .setView(layout)
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nuevoNombre = inputNombre.getText().toString();
                        if (!nuevoNombre.isEmpty()) {
                            tarea.nombre = nuevoNombre;
                            tarea.descripcion = inputDesc.getText().toString();
                            tarea.urgente = checkUrgente.isChecked();
                            tarea.estado = spinnerEstado.getSelectedItem().toString();

                            guardarTareasSharedPrefs();
                            ordenarTareas();
                            adaptador.notifyDataSetChanged();
                            actualizarProgresoGeneral();
                        }
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}