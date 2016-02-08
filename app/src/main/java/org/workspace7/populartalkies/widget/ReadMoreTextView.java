/*******************************************************************************
 *   Copyright (c) 2016 Kamesh Sampath<kamesh.sampath@hotmail.com)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 ******************************************************************************/

package org.workspace7.populartalkies.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import org.workspace7.populartalkies.R;

/**
 * TODO: document your custom view class.
 */
public class ReadMoreTextView extends TextView {

    public ReadMoreTextView(Context context) {
        this(context, null);
    }

    public ReadMoreTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ReadMoreTextView);
        this.trimLength = typedArray
                .getInt(R.styleable.ReadMoreTextView_trimLength, DEFAULT_TRIM_LENGTH) - ELLIPSIS.length();
        typedArray.recycle();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                trim = !trim;
                setText();
                requestFocusFromTouch();
            }
        });
    }
    @Override
    public void setText(CharSequence text, BufferType type) {
        originalText = text;
        ellipsedText = buildEllipsedText();
        bufferType = type;
        setText();

    }
    public CharSequence getOriginalText() {
        return originalText;
    }
    public int getTrimLength() {
        return trimLength;
    }
    public void setTrimLength(int trimLength) {
        this.trimLength = trimLength;
        ellipsedText = buildEllipsedText();
        setText();
    }
    private CharSequence buildEllipsedText() {

        if (originalText != null && originalText.length() > trimLength) {
            return new SpannableStringBuilder(originalText, 0, trimLength + 1).append(ELLIPSIS);
        } else {
            return originalText;
        }
    }
    private void setText() {
        super.setText(getEllipsedText(), bufferType);
    }
    private CharSequence getEllipsedText() {
        return trim ? ellipsedText : originalText;
    }

    private static final int DEFAULT_TRIM_LENGTH = 300;
    private static final String ELLIPSIS = "....";
    private CharSequence originalText;
    private CharSequence ellipsedText;
    private BufferType bufferType;
    private boolean trim = true;
    private int trimLength;
}
