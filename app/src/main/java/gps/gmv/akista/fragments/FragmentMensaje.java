/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                Fragment donde se visualizan mensajes de un chat específico
:*
:* Archivo:      FragmentMensaje.java
:* Autor:        Guillermo Franco Alemán           16130804
:*               Miguel Angel Carranza Esquivel    16130790
:*               Victor Alberto Castillo Rivera    17130016
:*
:* Fecha:        29-05-2020
:* Compilador:   JDK 8
:* Ultima modif: -
:*
:* Fecha            Modificó                        Motivo
:*==========================================================================================
:* 29/05/2020       Franco, Carranza, Castillo      Creación del archivo, funcionalidad
:*==========================================================================================*/

package gps.gmv.akista.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import gps.gmv.akista.R;
import gps.gmv.akista.adaptadores.FragmentMensajeAdapter;
import gps.gmv.akista.databinding.FragmentMensajeBinding;
import gps.gmv.akista.entidades.Mensaje;
import gps.gmv.akista.entidades.Usuario;

public class FragmentMensaje extends Fragment {

    private FragmentMensajeAdapter adapter;
    private FragmentMensajeBinding binding;

    private List<Mensaje> mensajes; // Aquí se guardan los mensajes de la conversación

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mensaje, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        setListeners();
    }

    private void init() {
        // Se setea el mensaje inicial a cadena vacía para evitar excepciones por valores nulos
        binding.setMsj("");
        mensajes = new ArrayList<>();
        // Al abrir el fragment se recibe el nombre del otro participante mediante un bundle
        // y se envía al adaptador
        adapter = new FragmentMensajeAdapter(getContext(), mensajes, getArguments().getString("nom"));
        binding.mensajes.setAdapter(adapter);

        // Se baja de la base de datos el objeto Usuario que pertenece al otro participante del chat
        // para integrarlo en el layout
        FirebaseDatabase.getInstance()
        .getReference("usuario")
        .child(getArguments().getString("id"))
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    // El usuario debe de existir pero se atrapan los errores por cualquierr detalle
                    // que pueda ocurrir
                    binding.setUsuarioDestino(dataSnapshot.getValue(Usuario.class));
                else {
                    Snackbar.make(getView(), "Ocurrió un error", Snackbar.LENGTH_LONG).show();
                    binding.send.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(getView(), databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
                binding.send.setEnabled(false);
            }
        });

        // Se descargan los mensajes de la base de datos siguiendo la ruta:
        //mensaje/id-del-usuario-actual/id-del-otro-miembro-del-chat
        FirebaseDatabase.getInstance()
        .getReference("mensaje/" + FirebaseAuth.getInstance().getUid() + '/' + getArguments().getString("id"))
        .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // El listener se activa cada que se agrega un mensaje a la conversación o durante
                // la descarga inicial de todos

                // Al recibir los mensajes se ordenan por fechas apoyados del metodo sort
                // de la clase Collections
                mensajes.add(dataSnapshot.getValue(Mensaje.class));
                Collections.sort(mensajes);

                // Se actualiza el adaptador para visualizar los mensajes
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(getView(), databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
            }

            // Métodos sin usar
            @Override public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
        });
    }

    private void setListeners() {
        binding.btRegresar.setOnClickListener(v -> getFragmentManager().popBackStack());
        binding.send.setOnClickListener(v -> enviar());
    }

    // Para enviar un mensaje éste debe tener al menos un caracter, no se pueden enviar mensajes
    // vacíos. Dichos mensajes se guardan en dos ubicaciones: en la lista de mensajes de los chats
    // del propio usuario y del otro usuario miembro del chat
    private void enviar() {
        if (!binding.getMsj().isEmpty()) {
            Mensaje m = new Mensaje();
            m.setIdDestinatario(binding.getUsuarioDestino().getId());
            m.setIdRemitente(FirebaseAuth.getInstance().getUid());
            m.setMensaje(binding.getMsj());

            String uuid = UUID.randomUUID().toString();

            FirebaseDatabase.getInstance()
            .getReference("mensaje")
            .child(m.getIdRemitente())
            .child(m.getIdDestinatario())
            .child(uuid)
            .setValue(m);

            FirebaseDatabase.getInstance()
            .getReference("mensaje")
            .child(m.getIdDestinatario())
            .child(m.getIdRemitente())
            .child(uuid)
            .setValue(m);

            binding.setMsj("");
        }
    }
}
