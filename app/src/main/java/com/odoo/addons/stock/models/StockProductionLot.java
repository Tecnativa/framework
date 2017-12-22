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
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;


public class StockProductionLot extends OModel {
    public static final String TAG = StockProductionLot.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.stock.stock_production_lot";

    OColumn name = new OColumn("Name", OVarchar.class).setSize(100);
    OColumn ref = new OColumn("Ref", OVarchar.class).setSize(100);
    OColumn product_id = new OColumn("Product", ProductProduct.class, OColumn.RelationType.ManyToOne);

    public StockProductionLot(Context context, OUser user) {
        super(context, "stock.production.lot", user);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }
}
