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
package com.odoo.addons.products.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;


public class ProductProduct extends OModel {
    public static final String TAG = ProductProduct.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.stock.product_product";

    OColumn name = new OColumn("Name", OVarchar.class).setSize(100);
    OColumn default_code = new OColumn("Code", OVarchar.class).setSize(100);
    OColumn barcode = new OColumn("Barcode", OVarchar.class).setSize(100);


    public ProductProduct(Context context, OUser user) {
        super(context, "product.product", user);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }
}
