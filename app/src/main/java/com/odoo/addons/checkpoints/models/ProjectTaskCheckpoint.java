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
package com.odoo.addons.checkpoints.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.addons.tasks.models.ProjectTask;
import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;


public class ProjectTaskCheckpoint extends OModel {
    public static final String TAG = ProjectTaskCheckpoint.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.checkpoints.project_task_checkpoint";

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn sequence = new OColumn("Sequence", OInteger.class);
    OColumn place_id = new OColumn("Place", ResPartner.class, OColumn.RelationType.ManyToOne);
    OColumn distance_estimated = new OColumn("Distance Estimated", OFloat.class);
    OColumn duration_estimated = new OColumn("Duration Estimated", OFloat.class);
    OColumn arrival_time = new OColumn("Arrival Time", ODateTime.class);
    OColumn departure_time = new OColumn("Departure Time", ODateTime.class);
    OColumn stopped_time = new OColumn("Stopped Time", OFloat.class);
    OColumn task_id = new OColumn("Task", ProjectTask.class, OColumn.RelationType.ManyToOne);

    public ProjectTaskCheckpoint(Context context, OUser user) {
        super(context, "project.task.checkpoint", user);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }

}
