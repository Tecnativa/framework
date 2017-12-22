/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p/>
 * Created on 8/1/15 5:47 PM
 */
package com.odoo.addons.stock;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.odoo.App;
import com.odoo.R;
import com.odoo.addons.checkpoints.models.ProjectTaskCheckpoint;
import com.odoo.addons.customers.Customers;
import com.odoo.addons.customers.utils.ShareUtil;
import com.odoo.base.addons.ir.feature.OFileManager;
import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.OAlert;
import com.odoo.core.utils.OResource;
import com.odoo.core.utils.OStringColorUtil;

import odoo.controls.OForm;

public class PackOperationDetails extends OdooCompatActivity {
    public static final String TAG = PackOperationDetails.class.getSimpleName();
    public static String KEY_PARTNER_TYPE = "partner_type";
    private final String KEY_MODE = "key_edit_mode";
    private final String KEY_NEW_IMAGE = "key_new_image";
    private Bundle extras;
    private ResPartner resPartner;
    private ProjectTaskCheckpoint projectTaskCheckpoint;
    private ODataRow record = null;
    private ImageView userImage = null;
    private OForm mForm;
    private App app;
    private Boolean mEditMode = false;
    private Menu mMenu;
    private OFileManager fileManager;
    private String newImage = null;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private Customers.Type partnerType = Customers.Type.Customer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkpoint_detail);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.checkpoint_collapsing_toolbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        findViewById(R.id.captureImage).setOnClickListener(this);

        fileManager = new OFileManager(this);
        if (toolbar != null)
            collapsingToolbarLayout.setTitle("");
        if (savedInstanceState != null) {
            mEditMode = savedInstanceState.getBoolean(KEY_MODE);
            newImage = savedInstanceState.getString(KEY_NEW_IMAGE);
        }
        app = (App) getApplicationContext();

        resPartner = new ResPartner(this, null);
        projectTaskCheckpoint = new ProjectTaskCheckpoint(this, null);

        extras = getIntent().getExtras();
        if (!hasRecordInExtra())
            mEditMode = true;
        setupToolbar();
    }

    private boolean hasRecordInExtra() {
        return extras != null && extras.containsKey(OColumn.ROW_ID);
    }

    private void setMode(Boolean edit) {
        if (mMenu != null) {
            mMenu.findItem(R.id.menu_checkpoint_detail_more).setVisible(!edit);
            mMenu.findItem(R.id.menu_checkpoint_edit).setVisible(!edit);
            mMenu.findItem(R.id.menu_checkpoint_save).setVisible(edit);
            mMenu.findItem(R.id.menu_checkpoint_cancel).setVisible(edit);
        }
        int color = Color.DKGRAY;
        if (record != null) {
            color = OStringColorUtil.getStringColor(this, record.getString("name"));
        }
        if (edit) {
            if (!hasRecordInExtra()) {
                collapsingToolbarLayout.setTitle("New");
            }
            mForm = (OForm) findViewById(R.id.checkpointFormEdit);
            findViewById(R.id.checkpoint_view_layout).setVisibility(View.GONE);
            findViewById(R.id.checkpoint_edit_layout).setVisibility(View.VISIBLE);
        } else {
            mForm = (OForm) findViewById(R.id.checkpointForm);
            findViewById(R.id.checkpoint_edit_layout).setVisibility(View.GONE);
            findViewById(R.id.checkpoint_view_layout).setVisibility(View.VISIBLE);
        }
        setColor(color);
    }

    private void setupToolbar() {
        if (!hasRecordInExtra()) {
            setMode(mEditMode);
            userImage.setColorFilter(Color.parseColor("#ffffff"));
            userImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            mForm.setEditable(mEditMode);
            mForm.initForm(null);
        } else {
            int rowId = extras.getInt(OColumn.ROW_ID);
            record = projectTaskCheckpoint.browse(rowId);
//            record.put("full_address", resPartner.getAddress(record.get().place_id));
            checkControls();
            setMode(mEditMode);
            mForm.setEditable(mEditMode);
            mForm.initForm(record);
            collapsingToolbarLayout.setTitle(record.getString("name"));
        }
    }

    private void checkControls() {
//        findViewById(R.id.full_address).setOnClickListener(this);
//        findViewById(R.id.website).setOnClickListener(this);
//        findViewById(R.id.email).setOnClickListener(this);
    }


    private void setColor(int color) {
        mForm.setIconTintColor(color);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_checkpoint_save:
                OValues values = mForm.getValues();
                if (values != null) {
                    if (record != null) {
                        projectTaskCheckpoint.update(record.getInt(OColumn.ROW_ID), values);
                        Toast.makeText(this, R.string.toast_information_saved, Toast.LENGTH_LONG).show();
                        mEditMode = !mEditMode;
                        setupToolbar();
                    } else {
                        final int row_id = projectTaskCheckpoint.insert(values);
                        if (row_id != OModel.INVALID_ROW_ID) {
                            finish();
                        }
                    }
                }
                break;
            case R.id.menu_checkpoint_cancel:
            case R.id.menu_checkpoint_edit:
                if (hasRecordInExtra()) {
                    mEditMode = !mEditMode;
                    setMode(mEditMode);
                    mForm.setEditable(mEditMode);
                    mForm.initForm(record);
                } else {
                    finish();
                }
                break;
            case R.id.menu_checkpoint_share:
                ShareUtil.shareContact(this, record, true);
                break;
            case R.id.menu_checkpoint_import:
                ShareUtil.shareContact(this, record, false);
                break;
            case R.id.menu_checkpoint_delete:
                OAlert.showConfirm(this, OResource.string(this,
                        R.string.confirm_are_you_sure_want_to_delete),
                        new OAlert.OnAlertConfirmListener() {
                            @Override
                            public void onConfirmChoiceSelect(OAlert.ConfirmType type) {
                                if (type == OAlert.ConfirmType.POSITIVE) {
                                    // Deleting record and finishing activity if success.
                                    if (projectTaskCheckpoint.delete(record.getInt(OColumn.ROW_ID))) {
                                        Toast.makeText(PackOperationDetails.this, R.string.toast_record_deleted,
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            }
                        });

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_checkpoint_detail, menu);
        mMenu = menu;
        setMode(mEditMode);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_MODE, mEditMode);
        outState.putString(KEY_NEW_IMAGE, newImage);
    }
}