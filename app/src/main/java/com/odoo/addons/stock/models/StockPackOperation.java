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

import com.odoo.addons.products.models.ProductProduct;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

import org.json.JSONArray;


public class StockPackOperation extends OModel {
    public static final String TAG = StockPackOperation.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.stock.stock_pack_operation";

    OColumn picking_id = new OColumn("Picking", StockPicking.class, OColumn.RelationType.ManyToOne);
    OColumn product_id = new OColumn("Product", ProductProduct.class, OColumn.RelationType.ManyToOne);
    OColumn product_qty = new OColumn("To Do", OFloat.class);
    OColumn ordered_qty = new OColumn("Ordered Quantity", OFloat.class);
    OColumn qty_done = new OColumn("Done", OFloat.class);
    OColumn pack_lot_ids = new OColumn("Serial Numbers", StockPackOperationLot.class, OColumn.RelationType.OneToMany)
            .setRelatedColumn("operation_id");
    OColumn state = new OColumn("State", OSelection.class)
            .addSelection("draft", "Draft")
            .addSelection("cancel", "Cancelled")
            .addSelection("waiting", "Waiting Another Operation")
            .addSelection("confirmed", "Waiting Availability")
            .addSelection("partially_available", "Partially Available")
            .addSelection("assigned", "Available")
            .addSelection("done", "Done");

    public StockPackOperation(Context context, OUser user) {
        super(context, "stock.pack.operation", user);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }

    public String storeProductName(OValues values) {
        try {
            if (!values.getString("product_id").equals("false")) {
                JSONArray partner_id = new JSONArray(values.getString("product_id"));
                return partner_id.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false";
    }

}
