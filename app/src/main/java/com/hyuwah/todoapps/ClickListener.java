package com.hyuwah.todoapps;

import android.view.View;

/**
 * Created by Wahyu on 06/05/2017.
 */

public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
