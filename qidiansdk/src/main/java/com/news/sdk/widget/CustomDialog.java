package com.news.sdk.widget;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.news.sdk.R;
import com.news.sdk.utils.TextUtil;

/**
 * 重写dialog
 * @author wyy
 *
 */
public class CustomDialog extends Dialog {

	private static TextViewExtend mTitle;
	private static TextView message;
	private static TextView messageContent;
	private static TextView negativeButton;
	private static TextView positiveButton;
	private static View mDialogBottomLine;

	public CustomDialog(Context context) {
		super(context);

	}

	public CustomDialog(Context context, int theme) {
		super(context, theme);
	}

	public static class Builder {
//		private final AlertController.AlertParams P;
		private Context context;
		private String title;
		private String message;
		private String positiveButtonText;
		private String negativeButtonText;
		private View contentView;
		private boolean cancelable=true;
		private OnClickListener positiveButtonClickListener;
		private OnClickListener negativeButtonClickListener;
		private OnCancelListener cancelListener;
		
		public Builder(Context context) {
			this.context = context;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 * 
		 * @param
		 * @return
		 */
		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		/**
		 * Set the Dialog title from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 * 
		 * @param title
		 * @return
		 */

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		/**
		 * Set the positive button resource and it's listener
		 * 
		 * @param positiveButtonText
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText,
				OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
				OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int negativeButtonText,
				OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String negativeButtonText,
				OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		
		 /**
		 * Sets whether the dialog is cancelable or not. Default is true.
		 *
		 * @return This Builder object to allow for chaining of calls to set
		 methods
		 */
		 public Builder setCancelable(boolean cancelable) {
			 this.cancelable = cancelable;
		 return this;
		 }
		
		 /**
		 * Sets the callback that will be called if the dialog is canceled.
		 * @see #setCancelable(boolean)
		 *
		 * @return This Builder object to allow for chaining of calls to set
		 methods
		 */
		 public Builder setOnCancelListener(OnCancelListener onCancelListener)
		 {
			 this.cancelListener = onCancelListener;
			 return this;
		 }

		public CustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomDialog dialog = new CustomDialog(context,
					R.style.Dialog);
			View layout = inflater.inflate(R.layout.custom_dialog, null);
			TextUtil.setLayoutBgResource(context,layout,R.drawable.bg_update_shape);

			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			// set the dialog title
			mTitle = (TextViewExtend) layout.findViewById(R.id.title);
			mDialogBottomLine = layout.findViewById(R.id.mDialogBottomLine);
			messageContent = (TextView)layout.findViewById(R.id.message);
			negativeButton = (TextView)layout.findViewById(R.id.negativeButton);
			positiveButton = (TextView)layout.findViewById(R.id.positiveButton);
			mTitle.setText(title);
			TextUtil.setTextColor(context,mTitle,R.color.color2);
			TextUtil.setTextColor(context,messageContent,R.color.color2);
			TextUtil.setTextColor(context,negativeButton,R.color.color11);
			TextUtil.setTextColor(context,positiveButton,R.color.color11);
			TextUtil.setLayoutBgResource(context,negativeButton,R.drawable.bg_update_cancel_shape);
			TextUtil.setLayoutBgResource(context,positiveButton,R.drawable.bg_update_confim_shape);
			TextUtil.setLayoutBgResource(context,mDialogBottomLine,R.color.color5);

			// set the confirm button
			if (positiveButtonText != null) {
				((TextView) layout.findViewById(R.id.positiveButton))
						.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((TextView) layout.findViewById(R.id.positiveButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.positiveButton).setVisibility(
						View.GONE);
			}
			// set the cancel button
			if (negativeButtonText != null) {
				((TextView) layout.findViewById(R.id.negativeButton))
						.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					((TextView) layout.findViewById(R.id.negativeButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									negativeButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.negativeButton).setVisibility(
						View.GONE);
			}
			// set the content message
			if (message != null) {
				((TextView) layout.findViewById(R.id.message)).setText(message);
			} else if (contentView != null) {
				// if no message set
				// add the contentView to the dialog body
				((LinearLayout) layout.findViewById(R.id.content))
						.removeAllViews();
				((LinearLayout) layout.findViewById(R.id.content)).addView(
						contentView, new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT));
			}
			dialog.setCancelable(cancelable);
            if (cancelable) {
                dialog.setCanceledOnTouchOutside(true);
            }
            dialog.setOnCancelListener(cancelListener);
            
//			dialog.setCancelable(false);// 屏蔽所有的点击事件
			// dialog.setCanceledOnTouchOutside(false);//点击以外的不消失
			dialog.setContentView(layout);
			return dialog;
		}
	}

}
