package io.bloc.android.blocly.api.model;

/**
 * Created by jeffbrys on 11/13/15.
 */
public abstract class Model {

    public final long rowId;

    public Model(long rowId) {
        this.rowId = rowId;
    }

    public long getRowId() {
        return rowId;
    }
}
