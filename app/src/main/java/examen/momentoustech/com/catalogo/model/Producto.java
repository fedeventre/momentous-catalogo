package examen.momentoustech.com.catalogo.model;

import android.webkit.URLUtil;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by fede on 10/25/16.
 */

public class Producto implements Serializable {

    @Expose
    @SerializedName("id")
    private String productorId;

    @Expose
    private String name;

    @Expose
    private String image;

    @Expose
    private String detail;

    public Producto(String productorId, String name, String image, String detail) {
        this.productorId = productorId;
        this.name = name;
        this.image = image;
        this.detail = detail;
    }

    public String getProductorId() {
        return productorId;
    }

    public void setProductorId(String productorId) {
        this.productorId = productorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
            return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
