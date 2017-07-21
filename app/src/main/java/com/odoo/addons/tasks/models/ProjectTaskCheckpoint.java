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

import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

import org.json.JSONArray;


public class ProjectTaskCheckpoint extends OModel {
    public static final String TAG = ProjectTaskCheckpoint.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.tasks.project_task_checkpoint";

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn sequence = new OColumn("Sequence", OInteger.class);
    OColumn place_id = new OColumn("Place", ResPartner.class, OColumn.RelationType.ManyToOne);
    OColumn distance_estimated = new OColumn("Distance Estimated", OFloat.class);
    OColumn duration_estimated = new OColumn("Duration Estimated", OFloat.class);
    OColumn arrival_time = new OColumn("Arrival Time", ODateTime.class);
    OColumn departure_time = new OColumn("Departure Time", ODateTime.class);
    OColumn stopped_time = new OColumn("Stopped Time", OFloat.class);
    OColumn task_id = new OColumn("Task", ProjectTask.class, OColumn.RelationType.ManyToOne);

//    package_origin_ids = fields.Many2many(
//    comodel_name='tms.package',
//    relation='project_task_checkpoint_tms_package_origin_rel',
//    column1='checkpoint_id',
//    column2='package_id',
//    string='Packages Origin',
//            )
//    package_destination_ids = fields.Many2many(
//    comodel_name='tms.package',
//    relation='project_task_checkpoint_tms_package_destination_rel',
//    column1='checkpoint_id',
//    column2='package_id',
//    string='Packages Destination',
//            )

    public ProjectTaskCheckpoint(Context context, OUser user) {
        super(context, "project.task.checkpoint", user);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }

    public String getPlaceName(OValues row) {
        String name = "";
        try {
            if (!row.getString("place_id").equals("false")) {
                JSONArray place_id = new JSONArray(
                        row.getString("place_id"));
                name = place_id.getString(1);
            }
            if (TextUtils.isEmpty(name)) {
                name = "No Place";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }


}
