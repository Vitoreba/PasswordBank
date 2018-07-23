package com.vitor.testecedro.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.vitor.testecedro.R;
import com.vitor.testecedro.controller.LoginActivity;
import com.vitor.testecedro.controller.SiteDetailActivity;
import com.vitor.testecedro.model.ApiResponse;
import com.vitor.testecedro.model.Site;
import com.vitor.testecedro.model.persistence.dao.SitesDAO;
import com.vitor.testecedro.rest.client.SiteClient;
import com.vitor.testecedro.rest.service.RetrofitConfig;

import java.sql.SQLException;
import java.util.ArrayList;

import okhttp3.ResponseBody;

public class SiteAdapter extends RecyclerView.Adapter<SiteAdapter.ViewHolder> {
    private static final String TAG = "SiteAdapter";

    private Context context;
    private ArrayList<Site> sites;
    private SitesAdapterListener listener;
    private SitesAdapterOnLongClickListener onLongClickListener;

    public SiteAdapter(ArrayList<Site> users,
                       SitesAdapterListener listener,
                       SitesAdapterOnLongClickListener onLongClickListener) {
        this.sites = users;
        this.listener = listener;
        this.onLongClickListener = onLongClickListener;
    }

    public SiteAdapter(Context context, ArrayList<Site> users,
                       SitesAdapterListener listener,
                       SitesAdapterOnLongClickListener onLongClickListener) {
        this.context = context;
        this.sites = users;
        this.listener = listener;
        this.onLongClickListener = onLongClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.site_recycler_view_cell, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Site site = sites.get(i);

        viewHolder.populate(site);
    }

    @Override
    public int getItemCount() {
        return sites.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView siteLogo;
        private TextView siteUrl, siteLogin;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            siteLogo = itemView.findViewById(R.id.imgLogoSite);
            siteUrl = itemView.findViewById(R.id.txtViewSiteUrl);
            siteLogin = itemView.findViewById(R.id.txtViewSiteLogin);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClickListener(getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        onLongClickListener.onLongItemClickListener(getAdapterPosition());
                        return true;
                    }
                }
            );
        }

        public void populate(final Site site) {
            siteUrl.setText(site.getUrlSite());
            siteLogin.setText(site.getLogin());

            // Sem melhores alternativas aqui, já que a API retorna a imagem e não a URL dela
            SiteClient.getInstance().doGetSiteLogo(Global.token.getToken(), site.getUrlSite()
                    , new RetrofitConfig.OnRestResponseListener<ResponseBody>() {
                @Override
                public void onRestSuccess(ResponseBody body) {
                    Bitmap bm = BitmapFactory.decodeStream(body.byteStream());
                    bm = Bitmap.createScaledBitmap(bm, 100, 100, true);
                    siteLogo.setImageBitmap(bm);
                }

                @Override
                public void onRestError(ResponseBody body, Integer code) {
                    Log.e(TAG, "Codigo de erro: " + code);
                }
            });

        }
    }

    public interface SitesAdapterListener {
        void onItemClickListener(Integer position);
    }

    public interface SitesAdapterOnLongClickListener {
        void onLongItemClickListener(Integer position);
    }
}
