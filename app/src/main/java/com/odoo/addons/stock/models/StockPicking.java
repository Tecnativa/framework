/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p>
 * Created on 2/1/15 2:25 PM
 */
package com.odoo.addons.stock.models;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.odoo.base.addons.res.ResCompany;
import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

import org.json.JSONArray;


public class StockPicking extends OModel {
    public static final String TAG = StockPicking.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.stock.stock_picking";

    OColumn origin = new OColumn("Origin", OVarchar.class).setSize(100);
    OColumn name = new OColumn("Name", OVarchar.class).setSize(100);
    OColumn picking_type_id = new OColumn("Type", StockPickingType.class, OColumn.RelationType.ManyToOne);
    OColumn partner_id = new OColumn("Partner", ResPartner.class, OColumn.RelationType.ManyToOne);
    OColumn company_id = new OColumn("Company", ResCompany.class, OColumn.RelationType.ManyToOne);
//    OColumn state = new OColumn("State", OVarchar.class).setSize(100);
    OColumn state = new OColumn("State", OSelection.class)
            .addSelection("draft", "Draft")
            .addSelection("cancel", "Cancelled")
            .addSelection("waiting", "Waiting Another Operation")
            .addSelection("confirmed", "Waiting Availability")
            .addSelection("partially_available", "Partially Available")
            .addSelection("assigned", "Available")
            .addSelection("done", "Done");

//    Saica Reels Columns
    OColumn forecast_arrival_date = new OColumn("Arrival Date", ODateTime.class);
    OColumn delivery_date = new OColumn("Delivery Date", ODateTime.class);
    OColumn tractor_plate = new OColumn("Tractor", OVarchar.class).setSize(100);
    OColumn saica_final_destination_id = new OColumn("Final Destination", ResPartner.class, OColumn.RelationType.ManyToOne);
    OColumn saica_reel_count = new OColumn("Reels Count", OInteger.class);
    OColumn saica_load_type = new OColumn("Load Type", OVarchar.class).setSize(100);
    OColumn saica_weight = new OColumn("Weight", OFloat.class);


    public StockPicking(Context context, OUser user) {
        super(context, "stock.picking", user);
        setHasMailChatter(true);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }
}
