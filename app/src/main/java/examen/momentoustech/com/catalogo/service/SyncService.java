package examen.momentoustech.com.catalogo.service;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by fede on 10/26/16.
 */

public class SyncService extends IntentService {

    public static final String NEW_DATA = "new_data";
    public SyncService() {
        super("SyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {



    }
}
