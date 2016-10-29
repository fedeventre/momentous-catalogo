package examen.momentoustech.com.catalogo.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import examen.momentoustech.com.catalogo.R;
import examen.momentoustech.com.catalogo.manager.RequestManager;
import examen.momentoustech.com.catalogo.model.Producto;

/**
 * Created by fede on 10/26/16.
 */

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private final Context mContext;
    private final ArrayList<Producto> mProductos;

    public ProductoAdapter(Context mContext, ArrayList<Producto> mProductos) {
        this.mContext = mContext;
        this.mProductos = mProductos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_producto, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Producto producto = mProductos.get(position);

        holder.mImage.setImageUrl(producto.getImage(), RequestManager.getInstance().getImageLoader());
        holder.mName.setText(producto.getName());
        holder.mDetail.setText(producto.getDetail());

    }

    @Override
    public int getItemCount() {
        return mProductos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final NetworkImageView mImage;
        private final TextView mName;
        private final TextView mDetail;

        public ViewHolder(View itemView) {
            super(itemView);
            mImage = (NetworkImageView) itemView.findViewById(R.id.product_photo);
            mName = (TextView) itemView.findViewById(R.id.product_name);
            mDetail = (TextView) itemView.findViewById(R.id.product_detail);
        }
    }
}
