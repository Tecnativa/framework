/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 *
 * Created on 2/1/15 11:07 AM
 */
package com.odoo.addons.stock.services;

import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.odoo.addons.stock.models.StockPickingType;
import com.odoo.addons.stock.models.StockPicking;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.service.ISyncFinishListener;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;

import java.util.ArrayList;
import java.util.List;


public class StockPickingSyncService extends OSyncService implements ISyncFinishListener {
    public static final String TAG = StockPickingSyncService.class.getSimpleName();
    public Boolean firstSync = false;

    @Override
    public OSyncAdapter getSyncAdapter(OSyncService service, Context context) {
        return new OSyncAdapter(context, StockPicking.class, service, true);
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user) {
        if (adapter.getModel().getModelName().equals("stock.picking")) {
            ODomain domain = new ODomain();
            StockPicking stockPicking = new StockPicking(getApplicationContext(), user);
            List<Integer> newIds = new ArrayList<>();
            for (ODataRow row : stockPicking.select(new String[]{}, "state = ?", new String[]{"assgined"})) {
                newIds.add(row.getInt("id"));
            }
            if (newIds.size() > 0) {
                domain.add("id", "in", newIds);
            }
            if (!firstSync)
                adapter.onSyncFinish(this);
//            domain.add("user_id", "=", user.getUserId());
            adapter.setDomain(domain).syncDataLimit(500);
            adapter.setDomain(domain);
        }
        if (adapter.getModel().getModelName().equals("stock.picking.type")) {
            adapter.onSyncFinish(syncFinishListener);
        }
    }

    @Override
    public OSyncAdapter performNextSync(OUser user, SyncResult syncResult) {
        return new OSyncAdapter(getApplicationContext(), StockPickingType.class, StockPickingSyncService.this, true);
    }

    ISyncFinishListener syncFinishListener = new ISyncFinishListener() {
        @Override
        public OSyncAdapter performNextSync(OUser user, SyncResult syncResult) {
            firstSync = true;
            return new OSyncAdapter(getApplicationContext(), StockPicking.class, StockPickingSyncService.this, true);
        }
    };
}
