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
package com.odoo.addons.tasks.models;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.odoo.BuildConfig;
import com.odoo.addons.checkpoints.models.ProjectTaskCheckpoint;
import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

import org.json.JSONArray;


public class ProjectTask extends OModel {
    public static final String TAG = ProjectTask.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.tasks.project_task";

    OColumn name = new OColumn("Name", OVarchar.class).setSize(100);
    OColumn date_start = new OColumn("Start Date", ODateTime.class);

    OColumn shipping_origin_id = new OColumn("Origin", ResPartner.class, OColumn.RelationType.ManyToOne);
    @Odoo.Functional(method = "getOriginName", store = true, depends = {"shipping_origin_id"})
    OColumn shipping_origin_name = new OColumn("Origin", OVarchar.class);
    OColumn checkpoint_ids = new OColumn("Contacts", ProjectTaskCheckpoint.class, OColumn.RelationType.OneToMany).
            setRelatedColumn("task_id");

    public ProjectTask(Context context, OUser user) {
        super(context, "project.task", user);
        setHasMailChatter(true);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }

    public String getOriginName(OValues row) {
        String name = "";
        try {
            if (!row.getString("shipping_origin_id").equals("false")) {
                JSONArray partner_id = new JSONArray(
                        row.getString("shipping_origin_id"));
                name = partner_id.getString(1);
            }
            if (TextUtils.isEmpty(name)) {
                name = "No Origin";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }


}
