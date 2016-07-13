/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nostra13.universalimageloader.core;

import android.graphics.Bitmap;
import android.os.Handler;

import com.news.yazhidao.widget.TextViewExtend;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.nostra13.universalimageloader.utils.L;

/**
 * Presents process'n'display image task. Processes image {@linkplain android.graphics.Bitmap} and display it in {@link android.widget.ImageView} using
 * {@link com.nostra13.universalimageloader.core.DisplayBitmapTask}.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.8.0
 */
final class ProcessAndDisplayImageTask implements Runnable {

	private static final String LOG_POSTPROCESS_IMAGE = "PostProcess image before displaying [%s]";

	private final ImageLoaderEngine engine;
	private final Bitmap bitmap;
	private final ImageLoadingInfo imageLoadingInfo;
	private final Handler handler;
    private TextViewExtend tv_title;

	public ProcessAndDisplayImageTask(ImageLoaderEngine engine, Bitmap bitmap, ImageLoadingInfo imageLoadingInfo,
									  Handler handler) {
		this.engine = engine;
		this.bitmap = bitmap;
		this.imageLoadingInfo = imageLoadingInfo;
		this.handler = handler;
	}

    public ProcessAndDisplayImageTask(ImageLoaderEngine engine, Bitmap bitmap, ImageLoadingInfo imageLoadingInfo,
									  Handler handler, TextViewExtend tv_title) {
        this.engine = engine;
        this.bitmap = bitmap;
        this.imageLoadingInfo = imageLoadingInfo;
        this.handler = handler;
        this.tv_title = tv_title;
    }

	@Override
	public void run() {
		L.d(LOG_POSTPROCESS_IMAGE, imageLoadingInfo.memoryCacheKey);

		BitmapProcessor processor = imageLoadingInfo.options.getPostProcessor();
		Bitmap processedBitmap = processor.process(bitmap);

        DisplayBitmapTask displayBitmapTask = null;

        if(tv_title == null){
             displayBitmapTask = new DisplayBitmapTask(processedBitmap, imageLoadingInfo, engine,
                    LoadedFrom.MEMORY_CACHE);
        }else {
             displayBitmapTask = new DisplayBitmapTask(processedBitmap, imageLoadingInfo, engine,
                    LoadedFrom.MEMORY_CACHE, tv_title);
        }
		LoadAndDisplayImageTask.runTask(displayBitmapTask, imageLoadingInfo.options.isSyncLoading(), handler, engine);
	}
}
