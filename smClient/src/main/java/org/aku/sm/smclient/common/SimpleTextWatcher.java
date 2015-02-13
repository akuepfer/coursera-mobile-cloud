package org.aku.sm.smclient.common;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by armin on 23.10.14.
 */
public abstract class SimpleTextWatcher implements TextWatcher {

    private final EditText editText;

    protected SimpleTextWatcher() {
        this.editText = null;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

}
