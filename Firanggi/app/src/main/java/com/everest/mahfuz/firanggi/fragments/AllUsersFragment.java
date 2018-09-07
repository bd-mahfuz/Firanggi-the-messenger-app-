package com.everest.mahfuz.firanggi.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.everest.mahfuz.firanggi.R;
import com.everest.mahfuz.firanggi.UsersActivity;
import com.everest.mahfuz.firanggi.adapter.UsersRVAdapter;
import com.everest.mahfuz.firanggi.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllUsersFragment extends Fragment {

    private RecyclerView mAllUserRv;

    private RecyclerView.Adapter  mUserListAdapter;

    private ArrayList<User> userList = new ArrayList<>();

    private DatabaseReference mDbRef;

    private ProgressDialog progressDialog;

    private TextView mNoUserMsgTv;


    public AllUsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_users, container, false);

        progressDialog = new ProgressDialog(getActivity());

        mNoUserMsgTv = view.findViewById(R.id.noUserTv);

        mAllUserRv = view.findViewById(R.id.allUserRv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mAllUserRv.setLayoutManager(layoutManager);


        mDbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mDbRef.keepSynced(true);

        progressDialog.setMessage("Loading data...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userList.clear();
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    userList.add(user);
                }
                if (userList.size() == 0) {
                    mNoUserMsgTv.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }else {
                    Log.d("user list size:", userList.get(0).getUserImage()+"");
                    mUserListAdapter = new UsersRVAdapter(getActivity(), userList);
                    mAllUserRv.setAdapter(mUserListAdapter);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //progressDialog.dismiss();
                //Toast.makeText(getActivity(), "error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

}
