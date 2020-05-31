/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                Fragment donde se visualizan mensajes enviados por la app
:*
:* Archivo:      FragmentMensajes.java
:* Autor:        Guillermo Franco Alemán           16130804
:*               Miguel Angel Carranza Esquivel    16130790
:*               Victor Alberto Castillo Rivera    17130016
:*
:* Fecha:        19-05-2020
:* Compilador:   JDK 8
:* Ultima modif: -
:*
:* Fecha            Modificó                        Motivo
:*==========================================================================================
:* 19/05/2020       Franco, Carranza, Castillo      Creación del archivo
:* 29/05/2020       Franco, Carranza, Castillo      Implementación de la funcionalidad
:*==========================================================================================*/

package gps.gmv.akista.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import gps.gmv.akista.R;
import gps.gmv.akista.adaptadores.FragmentMensajesAdapter;
import gps.gmv.akista.databinding.FragmentMensajesBinding;
import gps.gmv.akista.entidades.Usuario;
import gps.gmv.akista.otros.Singleton;

public class FragmentMensajes extends Fragment {

    private FragmentMensajesAdapter adapter;
    private FragmentMensajesBinding binding;

    private List<String> remitentes,    // Se guardan los nombres de los chats ya existentes
                         ids,           // Se guardan los ids de los chats ya existentes y se usan para
                                        // abrir la conversación
                         newMsgDataNom, // Se guardan los nombres para mostrar en un spinner de los usuarios
                                        // existentes y disponibles para enviarles un mensaje
                         newMsgDataId;  // Se guardan los id de los usuarios disponibles para enviar un mensaje

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mensajes, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        setListeners();
    }

    // Se inicializan valores
    private void init() {
        remitentes = new ArrayList<>();
        ids = new ArrayList<>();
        newMsgDataNom = new ArrayList<>();
        newMsgDataId = new ArrayList<>();

        adapter = new FragmentMensajesAdapter(getContext(), remitentes);
        binding.msgs.setAdapter(adapter);
    }

    private void setListeners() {
        binding.btCrear.setOnClickListener(v -> mensaje());
        binding.btRegresar.setOnClickListener(v -> getFragmentManager().popBackStack());

        // Hacer clic en un elemento de la lista de chats invocará a la pantalla de chat para el mismo
        binding.msgs.setOnItemClickListener((adapterView, view, i, l) -> setFragment(new FragmentMensaje(), ids.get(i), remitentes.get(i)));

        // Se obtienen los nombres los chats accediendo directamente al nodo que posee ese atributo
        FirebaseDatabase.getInstance()
        .getReference("mensaje")
        .child(FirebaseAuth.getInstance().getUid())
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotA) {
                for (DataSnapshot snapshot : dataSnapshotA.getChildren()) {
                    FirebaseDatabase.getInstance()
                    .getReference("usuario") // Al estar guardada la información en formato
                                                   // JSON se pueden acceder así a los valores específicos
                                                   // de los objetos almacenados
                    .child(snapshot.getKey())
                    .child("nombre")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshotB) {
                            remitentes.add(dataSnapshotB.getValue(String.class));
                            ids.add(snapshot.getKey());
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Snackbar.make(getView(), databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(getView(), databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

        // Un usuario padre/tutor solo le puede enviar mensaje a un docente y viceversa.
        // Por lo tanto en función del tipo de usuario que se es, se buscarán los usuarios a los
        // que se pueden enviar mensajes
        FirebaseDatabase.getInstance()
        .getReference("usuario")
        .orderByChild("tipoUsuario")
        // Si es usuario normal buscará docentes; si es docente buscará un usuario normal
        .equalTo(Singleton.getInstance().getUsuario().getTipoUsuario() == Usuario.TUTOR ? Usuario.PROFE : Usuario.TUTOR)
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Se guardan tanto el id como el nombre
                        newMsgDataNom.add(snapshot.getValue(Usuario.class).getNombre());
                        newMsgDataId.add(snapshot.getValue(Usuario.class).getId());
                    }
                else
                    binding.btCrear.setEnabled(false);
            }

            @Override public void onCancelled(@NonNull DatabaseError databaseError) {
                binding.btCrear.setEnabled(false);
            }
        });
    }

    private void mensaje() {
        // Para crear un chat nuevo se elige desde una lista con los nombres ya almacenados
        String[] converted = newMsgDataNom.toArray(new String[0]);

        new AlertDialog.Builder(getActivity())
        .setTitle("Elija un destinatario")
        .setItems(converted, (dialogInterface, i) -> setFragment(new FragmentMensaje(), newMsgDataId.get(i), newMsgDataNom.get(i)))
        .create()
        .show();
    }

    // Desde aquí se abre un fragment de chat que enviará tanto el id del usuario con el que se va a
    // chatear como el nombre del mismo
    private void setFragment(FragmentMensaje fragment, String id, String nom) {
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("nom", nom);
        fragment.setArguments(bundle);

        getActivity()
        .getSupportFragmentManager()
        .beginTransaction()
        .replace(getId(), fragment)
        .addToBackStack(null)
        .commit();
    }
}
