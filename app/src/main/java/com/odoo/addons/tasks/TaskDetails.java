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
package com.odoo.addons.tasks;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.odoo.App;
import com.odoo.R;
import com.odoo.addons.checkpoints.CheckPointDetails;
import com.odoo.addons.checkpoints.models.ProjectTaskCheckpoint;
import com.odoo.addons.customers.CustomerDetails;
import com.odoo.addons.customers.Customers;
import com.odoo.addons.customers.utils.ShareUtil;
import com.odoo.addons.tasks.models.ProjectTask;
import com.odoo.base.addons.ir.feature.OFileManager;
import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.rpc.helper.OdooFields;
import com.odoo.core.rpc.helper.utils.gson.OdooResult;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.IntentUtils;
import com.odoo.core.utils.OAlert;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.OCursorUtils;
import com.odoo.core.utils.OResource;
import com.odoo.core.utils.OStringColorUtil;
import com.odoo.core.utils.ODateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import odoo.controls.ExpandableListControl;
import odoo.controls.ODateTimeField;
import odoo.controls.OField;
import odoo.controls.OForm;

public class TaskDetails extends OdooCompatActivity
        implements View.OnClickListener, OField.IOnFieldValueChangeListener, AdapterView.OnItemClickListener{
    public static final String TAG = TaskDetails.class.getSimpleName();
    private final String KEY_MODE = "key_edit_mode";
    private final String KEY_NEW_IMAGE = "key_new_image";
    private Bundle extras;
    private ProjectTask projectTask;
    private ODataRow record = null;
    private ExpandableListControl mList;
    private List<Object> objects = new ArrayList<>();
    private ExpandableListControl.ExpandableListAdapter mAdapter;

    private ResPartner places = null;
    private ProjectTaskCheckpoint checkpoints = null;

    private OForm mForm;
    private App app;
    private Boolean mEditMode = false;
    private Menu mMenu;
    private OFileManager fileManager;
    private String newImage = null;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private FloatingActionButton buttonClock;
    private ArrayList<String> timeFields = new ArrayList<String>(
            Arrays.asList("arrival_time", "departure_time"));

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_detail);

        buttonClock = (FloatingActionButton) findViewById(R.id.btnClock);
        buttonClock.setOnClickListener(this);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.task_collapsing_toolbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fileManager = new OFileManager(this);
        if (toolbar != null)
            collapsingToolbarLayout.setTitle("");
        if (savedInstanceState != null) {
            mEditMode = savedInstanceState.getBoolean(KEY_MODE);
        }
        app = (App) getApplicationContext();
        projectTask = new ProjectTask(this, null);
        extras = getIntent().getExtras();
        if (!hasRecordInExtra())
            mEditMode = true;
        places = new ResPartner(this, null);
        checkpoints = new ProjectTaskCheckpoint(this, null);
        setupToolbar();
        initAdapter();
    }

    private boolean hasRecordInExtra() {
        return extras != null && extras.containsKey(OColumn.ROW_ID);
    }

    private void setMode(Boolean edit) {
        if (mMenu != null) {
            mMenu.findItem(R.id.menu_task_detail_more).setVisible(!edit);
            mMenu.findItem(R.id.menu_task_edit).setVisible(!edit);
            mMenu.findItem(R.id.menu_task_save).setVisible(edit);
            mMenu.findItem(R.id.menu_task_cancel).setVisible(edit);
        }
        int color = Color.DKGRAY;
        if (record != null) {
            color = OStringColorUtil.getStringColor(this, record.getString("name"));
        }
        if (edit) {
            if (!hasRecordInExtra()) {
                collapsingToolbarLayout.setTitle("New");
            }
            mForm = (OForm) findViewById(R.id.taskFormEdit);
            findViewById(R.id.task_view_layout).setVisibility(View.GONE);
            findViewById(R.id.task_edit_layout).setVisibility(View.VISIBLE);
        } else {
            mForm = (OForm) findViewById(R.id.taskForm);
            findViewById(R.id.task_edit_layout).setVisibility(View.GONE);
            findViewById(R.id.task_view_layout).setVisibility(View.VISIBLE);
        }
        setColor(color);
    }

    private void initAdapter() {
        mList = (ExpandableListControl) findViewById(R.id.expListCheckPoint);
        mList.setVisibility(View.VISIBLE);
        if (extras != null && record != null) {
            List<ODataRow> lines = record.getO2MRecord("checkpoint_ids").browseEach();
            objects.addAll(lines);
        }
        mAdapter = mList.getAdapter(R.layout.task_checkpoint_line_item, objects,
                new ExpandableListControl.ExpandableListAdapterGetViewListener() {
                    @Override
                    public View getView(int position, View mView, ViewGroup parent) {
                        ODataRow row = (ODataRow) mAdapter.getItem(position);
                        OControls.setText(mView, R.id.taskCheckpointName, row.getString("name"));
                        OControls.setText(mView, R.id.taskCheckpointArrivalTime, row.getString("arrival_time"));
//                        mView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                boolean a=false;
//                                a = true;
//                                System.out.println("Start activity checkpoint");
//                            }
//                        });
                        return mView;
                    }
                });
        mList.setOnClickListener(this);
        mAdapter.notifyDataSetChanged(objects);
    }

    private void loadActivity(ODataRow row) {
        Bundle data = new Bundle();
        if (row != null) {
            data = row.getPrimaryBundleData();
        }
        IntentUtils.startActivity(this, CheckPointDetails.class, data);
    }

    private void setupToolbar() {
        if (!hasRecordInExtra()) {
            setMode(mEditMode);
//            userImage.setColorFilter(Color.parseColor("#ffffff"));
//            userImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            mForm.setEditable(mEditMode);
            mForm.initForm(null);
        } else {
            int rowId = extras.getInt(OColumn.ROW_ID);
            record = projectTask.browse(rowId);
//            record.put("full_address", resPartner.getAddress(record));
            checkControls();
            setMode(mEditMode);
            mForm.setEditable(mEditMode);
            mForm.initForm(record);
//            collapsingToolbarLayout.setTitle(record.getString("name"));
            setTaskTitle();
        }
    }

    private void setTaskTitle(){
        // display next action
        List<ODataRow> lines = record.getO2MRecord("checkpoint_ids").browseEach();
        boolean updated = false;
        for (ODataRow line : lines) {
            updated = false;
            for (String timeField: timeFields) {
                if (line.getString(timeField).equals("false")){
                    if (timeField.contains("arrival")) {
                        collapsingToolbarLayout.setTitle("Llegada a: \n" + line.getString("name"));
                    } else{
                        collapsingToolbarLayout.setTitle("Salida de: \n" + line.getString("name"));
                    }
                    updated = true;
                    break;
                };
            };
            if (updated) break;
        };
        if (!updated) {
            collapsingToolbarLayout.setTitle("Nada a realizar");
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.full_address:
                IntentUtils.redirectToMap(this, record.getString("full_address"));
                break;
            case R.id.website:
                IntentUtils.openURLInBrowser(this, record.getString("website"));
                break;
            case R.id.email:
                IntentUtils.requestMessage(this, record.getString("email"));
                break;
            case R.id.phone_number:
                IntentUtils.requestCall(this, record.getString("phone"));
                break;
            case R.id.mobile_number:
                IntentUtils.requestCall(this, record.getString("mobile"));
                break;
            case R.id.captureImage:
                fileManager.requestForFile(OFileManager.RequestType.IMAGE_OR_CAPTURE_IMAGE);
                break;
            case R.id.btnClock:
                System.out.println("click clock");
                if (extras != null && record != null) {
                    List<ODataRow> lines = record.getO2MRecord("checkpoint_ids").browseEach();
                    String dateNowUTC = ODateUtils.getUTCDate(ODateUtils.DEFAULT_FORMAT);
                    for (ODataRow line : lines) {
                        boolean updated = false;
                        int place_id = places.selectServerId(line.getInt("place_id"));
                        for (String timeField: timeFields) {
                            if (line.getString(timeField).equals("false")){
                                OValues values = new OValues();
                                values.put(timeField, dateNowUTC);
                                updated = checkpoints.update(line.getInt("_id"), values);
                                break;
                            };
                        };
                        if (updated) break;
                    };
                    // TODO: Resync optimize
                    lines = record.getO2MRecord("checkpoint_ids").browseEach();
                    objects.clear();
                    objects.addAll(lines);
                    mAdapter.notifyDataSetChanged(objects);
                    setTaskTitle();
                }


                break;
        }

    }

    private void checkControls() {
        findViewById(R.id.date_start).setOnClickListener(this);
        findViewById(R.id.expListCheckPoint).setOnClickListener(this);
//        findViewById(R.id.phone_number).setOnClickListener(this);
//        findViewById(R.id.mobile_number).setOnClickListener(this);
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
            case R.id.menu_task_save:
                OValues values = mForm.getValues();
                if (values != null) {
                    if (record != null) {
                        projectTask.update(record.getInt(OColumn.ROW_ID), values);
                        Toast.makeText(this, R.string.toast_information_saved, Toast.LENGTH_LONG).show();
                        mEditMode = !mEditMode;
                        setupToolbar();
                    } else {
                        final int row_id = projectTask.insert(values);
                        if (row_id != OModel.INVALID_ROW_ID) {
                            finish();
                        }
                    }
                }
                break;
            case R.id.menu_task_cancel:
            case R.id.menu_task_edit:
                if (hasRecordInExtra()) {
                    mEditMode = !mEditMode;
                    setMode(mEditMode);
                    mForm.setEditable(mEditMode);
                    mForm.initForm(record);
                } else {
                    finish();
                }
                break;
            case R.id.menu_task_share:
                ShareUtil.shareContact(this, record, true);
                break;
            case R.id.menu_task_import:
                ShareUtil.shareContact(this, record, false);
                break;
            case R.id.menu_task_delete:
                OAlert.showConfirm(this, OResource.string(this,
                        R.string.confirm_are_you_sure_want_to_delete),
                        new OAlert.OnAlertConfirmListener() {
                            @Override
                            public void onConfirmChoiceSelect(OAlert.ConfirmType type) {
                                if (type == OAlert.ConfirmType.POSITIVE) {
                                    // Deleting record and finishing activity if success.
                                    if (projectTask.delete(record.getInt(OColumn.ROW_ID))) {
                                        Toast.makeText(TaskDetails.this, R.string.toast_record_deleted,
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
        getMenuInflater().inflate(R.menu.menu_task_detail, menu);
        mMenu = menu;
        setMode(mEditMode);
        return true;
    }

    @Override
    public void onFieldValueChange(OField field, Object value) {
        if (field.getFieldName().equals("is_company")) {
            Boolean checked = Boolean.parseBoolean(value.toString());
            int view = (checked) ? View.GONE : View.VISIBLE;
            findViewById(R.id.parent_id).setVisibility(view);
        }
    }

//    private class BigImageLoader extends AsyncTask<Integer, Void, String> {
//
//        @Override
//        protected String doInBackground(Integer... params) {
//            String image = null;
//            try {
//                Thread.sleep(300);
//                OdooFields fields = new OdooFields();
//                fields.addAll(new String[]{"image_medium"});
//                OdooResult record = projectTask.getServerDataHelper().read(null, params[0]);
//                if (record != null && !record.getString("image_medium").equals("false")) {
//                    image = record.getString("image_medium");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return image;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            if (result != null) {
//                if (!result.equals("false")) {
//                    OValues values = new OValues();
//                    values.put("large_image", result);
//                    projectTask.update(record.getInt(OColumn.ROW_ID), values);
//                    record.put("large_image", result);
//                    settaskImage();
//                }
//            }
//        }
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_MODE, mEditMode);
        outState.putString(KEY_NEW_IMAGE, newImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        OValues values = fileManager.handleResult(requestCode, resultCode, data);
        if (values != null && !values.contains("size_limit_exceed")) {
            newImage = values.getString("datas");
//            userImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            userImage.setColorFilter(null);
//            userImage.setImageBitmap(BitmapUtils.getBitmapImage(this, newImage));
        } else if (values != null) {
            Toast.makeText(this, R.string.toast_image_size_too_large, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        System.out.println("RARARARRARARRARARAR");

    }
}