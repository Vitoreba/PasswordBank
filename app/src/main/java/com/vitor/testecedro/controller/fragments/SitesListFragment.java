package com.vitor.testecedro.controller.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vitor.testecedro.R;
import com.vitor.testecedro.controller.SiteDetailActivity;
import com.vitor.testecedro.model.Site;
import com.vitor.testecedro.model.persistence.dao.SitesDAO;
import com.vitor.testecedro.util.SiteAdapter;

import java.sql.SQLException;
import java.util.ArrayList;

public class SitesListFragment extends Fragment {

    private RecyclerView sitesRecyclerView;
    private SiteAdapter siteAdapter;

    private SitesDAO dao;
    private ArrayList<Site> sites;

    private SiteAdapter.SitesAdapterListener sitesListener;
    private SiteAdapter.SitesAdapterOnLongClickListener onLongClickListener;

    public SitesListFragment() {
        // Required empty public constructor
    }

    public static SitesListFragment newInstance() {
        return new SitesListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sites_list, container, false);

        init(view);

        return view;
    }

    private void init(View view) {
        sitesRecyclerView = view.findViewById(R.id.recSitesList);

        try {
            dao = new SitesDAO(getActivity());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(dao != null) {
            sites = (ArrayList<Site>) dao.findAll();
        } else {
            sites = new ArrayList<Site>();
        }

        sitesListener = new SiteAdapter.SitesAdapterListener() {
            @Override
            public void onItemClickListener(Integer position) {
                toSiteDetailActivity(position);
            }
        };

        onLongClickListener = new SiteAdapter.SitesAdapterOnLongClickListener() {
            @Override
            public void onLongItemClickListener(Integer position) {
                deleteSite(position);
            }
        };
        siteAdapter = new SiteAdapter(sites, sitesListener, onLongClickListener);
        sitesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sitesRecyclerView.setAdapter(siteAdapter);

    }

    private void deleteSite(int position) {
        final int myPosition = position;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Excluir site");
        builder.setMessage("Deseja mesmo excluir este site?");

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Site site = sites.get(myPosition);

                sites.remove(myPosition);
                siteAdapter.notifyDataSetChanged();

                dao.delete(site);
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void toSiteDetailActivity(Integer position) {
        Intent intent = new Intent(getActivity(), SiteDetailActivity.class);
        intent.putExtra("site", sites.get(position));
        startActivity(intent);
        getActivity().finish();
    }

}
