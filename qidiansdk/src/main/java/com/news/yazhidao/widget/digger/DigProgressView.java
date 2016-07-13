package com.news.yazhidao.widget.digger;//package com.news.yazhidao.widget.digger;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.news.yazhidao.R;
//
//
///**
// * Created by fengjigang on 15/7/17.
// */
//public class DigProgressView extends LinearLayout {
//    private final DigCircleImage step1Icon;
//    private final View step1RightLine;
//    private final DigCircleImage step2Icon;
//    private final View step3LeftLine;
//    private final DigCircleImage step3Icon;
//    private final View step3RightLine;
//    private final View step4LeftLine;
//    private final DigCircleImage step4Icon;
//    private final View step4RightLine;
//    private final View step5LeftLine;
//    private final DigCircleImage step5Icon;
//    private final View step5RightLine;
//    private final View step6LeftLine;
//    private final DigCircleImage step6Icon;
//    private final View step2RightLine;
//    private final View step2LeftLine;
//    private final TextView step1Title;
//    private final TextView step2Title;
//    private final TextView step3Title;
//    private final TextView step4Title;
//    private final TextView step5Title;
//    private final TextView step6Title;
//
//
//    public DigProgressView(Context context) {
//        this(context,null);
//    }
//
//    public DigProgressView(Context context, AttributeSet attrs) {
//        this(context, attrs,-1);
//    }
//
//    public DigProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        View viewParent = View.inflate(context, R.layout.dig_progress, this);
//        step1Icon = (DigCircleImage) viewParent.findViewById(R.id.step1Icon);
//        step1RightLine = viewParent.findViewById(R.id.step1RightLine);
//        step1Title = (TextView)viewParent.findViewById(R.id.step1Title);
//
//        step2LeftLine = viewParent.findViewById(R.id.step2LeftLine);
//        step2Icon = (DigCircleImage) viewParent.findViewById(R.id.step2Icon);
//        step2RightLine = viewParent.findViewById(R.id.step2RightLine);
//        step2Title = (TextView)viewParent.findViewById(R.id.step2Title);
//
//        step3LeftLine = viewParent.findViewById(R.id.step3LeftLine);
//        step3Icon = (DigCircleImage) viewParent.findViewById(R.id.step3Icon);
//        step3RightLine = viewParent.findViewById(R.id.step3RightLine);
//        step3Title = (TextView)viewParent.findViewById(R.id.step3Title);
//
//        step4LeftLine = viewParent.findViewById(R.id.step4LeftLine);
//        step4Icon = (DigCircleImage) viewParent.findViewById(R.id.step4Icon);
//        step4RightLine = viewParent.findViewById(R.id.step4RightLine);
//        step4Title = (TextView)viewParent.findViewById(R.id.step4Title);
//
//        step5LeftLine = viewParent.findViewById(R.id.step5LeftLine);
//        step5Icon = (DigCircleImage) viewParent.findViewById(R.id.step5Icon);
//        step5RightLine = viewParent.findViewById(R.id.step5RightLine);
//        step5Title = (TextView)viewParent.findViewById(R.id.step5Title);
//
//        step6LeftLine = viewParent.findViewById(R.id.step6LeftLine);
//        step6Icon = (DigCircleImage) viewParent.findViewById(R.id.step6Icon);
//        step6Title = (TextView)viewParent.findViewById(R.id.step6Title);
//
//    }
//    public void setCurrentStep(int step){
//        resetViews();
//        switch (step){
//            case 1:
//                step1Icon.setBackgroundResource(R.drawable.ic_doing1);
//                step1Icon.startPin();
//                step1Title.setTextColor(getResources().getColor(R.color.dig_title_doing));
//                break;
//            case 2:
//                step1Icon.setBackgroundResource(R.drawable.ic_completed);
//                step1Icon.stopPinning();
//                step1Title.setTextColor(getResources().getColor(R.color.dig_title_done));
//                step1RightLine.setBackgroundResource(R.drawable.ic_done_line);
//
//                step2LeftLine.setBackgroundResource(R.drawable.ic_done_line);
//                step2Icon.setBackgroundResource(R.drawable.ic_doing2);
//                step2Icon.startPin();
//                step2Title.setTextColor(getResources().getColor(R.color.dig_title_doing));
//                break;
//            case 3:
//                step1Icon.setBackgroundResource(R.drawable.ic_completed);
//                step1Icon.stopPinning();
//                step1Title.setTextColor(getResources().getColor(R.color.dig_title_done));
//                step1RightLine.setBackgroundResource(R.drawable.ic_done_line);
//
//                step2LeftLine.setBackgroundResource(R.drawable.ic_done_line);
//                step2Icon.setBackgroundResource(R.drawable.ic_completed);
//                step2Icon.stopPinning();
//                step2RightLine.setBackgroundResource(R.drawable.ic_done_line);
//                step2Title.setTextColor(getResources().getColor(R.color.dig_title_done));
//
//                step3LeftLine.setBackgroundResource(R.drawable.ic_done_line);
//                step3Icon.setBackgroundResource(R.drawable.ic_doing3);
//                step3Icon.startPin();
//                step3Title.setTextColor(getResources().getColor(R.color.dig_title_doing));
//                break;
//            case 4:
//                step1Icon.setBackgroundResource(R.drawable.ic_completed);
//                step1Icon.stopPinning();
//                step1Title.setTextColor(getResources().getColor(R.color.dig_title_done));
//                step1RightLine.setBackgroundResource(R.drawable.ic_done_line);
//
//                step2LeftLine.setBackgroundResource(R.drawable.ic_done_line);
//                step2Icon.stopPinning();
//                step2Icon.setBackgroundResource(R.drawable.ic_completed);
//                step2RightLine.setBackgroundResource(R.drawable.ic_done_line);
//                step2Title.setTextColor(getResources().getColor(R.color.dig_title_done));
//
//                step3LeftLine.setBackgroundResource(R.drawable.ic_done_line);
//                step3Icon.setBackgroundResource(R.drawable.ic_completed);
//                step3Icon.stopPinning();
//                step3Title.setTextColor(getResources().getColor(R.color.dig_title_done));
//                step3RightLine.setBackgroundResource(R.drawable.ic_done_line);
//
//                step4LeftLine.setBackgroundResource(R.drawable.ic_done_line);
//                step4Icon.setBackgroundResource(R.drawable.ic_doing4);
//                step4Icon.startPin();
//                step4Title.setTextColor(getResources().getColor(R.color.dig_title_doing));
//                break;
//            case 5:
//                step1Icon.setBackgroundResource(R.drawable.ic_completed);
//                step1Icon.stopPinning();
//                step1Title.setTextColor(getResources().getColor(R.color.dig_title_done));
//                step1RightLine.setBackgroundResource(R.drawable.ic_done_line);
//
//                step2LeftLine.setBackgroundResource(R.drawable.ic_done_line);
//                step2Icon.stopPinning();
//                step2Icon.setBackgroundResource(R.drawable.ic_completed);
//                step2RightLine.setBackgroundResource(R.drawable.ic_done_line);
//                step2Title.setTextColor(getResources().getColor(R.color.dig_title_done));
//
//                step3LeftLine.setBackgroundResource(R.drawable.ic_done_line);
//                step3Icon.setBackgroundResource(R.drawable.ic_completed);
//                step3Icon.stopPinning();
//                step3Title.setTextColor(getResources().getColor(R.color.dig_title_done));
//                step3RightLine.setBackgroundResource(R.drawable.ic_done_line);
//
//                step4LeftLine.setBackgroundResource(R.drawable.ic_done_line);
//                step4Icon.setBackgroundResource(R.drawable.ic_completed);
//                step4Icon.stopPinning();
//                step4Title.setTextColor(getResources().getColor(R.color.dig_title_done));
//                step4RightLine.setBackgroundResource(R.drawable.ic_done_line);
//
//                step5LeftLine.setBackgroundResource(R.drawable.ic_done_line);
//                step5Icon.setBackgroundResource(R.drawable.ic_doing5);
//                step5Icon.startPin();
//                step5Title.setTextColor(getResources().getColor(R.color.dig_title_doing));
//                break;
//            case 6:
//                step1Icon.setBackgroundResource(R.drawable.ic_completed);
//                step1Icon.stopPinning();
//                step1Title.setTextColor(getResources().getColor(R.color.dig_title_done));
//                step1RightLine.setBackgroundResource(R.drawable.ic_done_line);
//
//                step2LeftLine.setBackgroundResource(R.drawable.ic_done_line);
//                step2Icon.stopPinning();
//                step2Icon.setBackgroundResource(R.drawable.ic_completed);
//                step2RightLine.setBackgroundResource(R.drawable.ic_done_line);
//                step2Title.setTextColor(getResources().getColor(R.color.dig_title_done));
//
//                step3LeftLine.setBackgroundResource(R.drawable.ic_done_line);
//                step3Icon.setBackgroundResource(R.drawable.ic_completed);
//                step3Icon.stopPinning();
//                step3Title.setTextColor(getResources().getColor(R.color.dig_title_done));
//                step3RightLine.setBackgroundResource(R.drawable.ic_done_line);
//
//                step4LeftLine.setBackgroundResource(R.drawable.ic_done_line);
//                step4Icon.setBackgroundResource(R.drawable.ic_completed);
//                step4Icon.stopPinning();
//                step4Title.setTextColor(getResources().getColor(R.color.dig_title_done));
//                step4RightLine.setBackgroundResource(R.drawable.ic_done_line);
//
//                step5LeftLine.setBackgroundResource(R.drawable.ic_done_line);
//                step5Icon.setBackgroundResource(R.drawable.ic_completed);
//                step5Icon.stopPinning();
//                step5Title.setTextColor(getResources().getColor(R.color.dig_title_done));
//                step5RightLine.setBackgroundResource(R.drawable.ic_done_line);
//
//                step6LeftLine.setBackgroundResource(R.drawable.ic_done_line);
//                step6Icon.setBackgroundResource(R.drawable.ic_doing6);
//                step6Icon.startPin();
//                step6Title.setTextColor(getResources().getColor(R.color.dig_title_doing));
//                break;
//            case 0:
//                step1Icon.setBackgroundResource(R.drawable.ic_completed);
//                step1Icon.stopPinning();
//                step1Title.setTextColor(getResources().getColor(R.color.dig_title_done));
//                step1RightLine.setBackgroundResource(R.drawable.ic_done_line);
//
//                step2LeftLine.setBackgroundResource(R.drawable.ic_done_line);
//                step2Icon.stopPinning();
//                step2Icon.setBackgroundResource(R.drawable.ic_completed);
//                step2RightLine.setBackgroundResource(R.drawable.ic_done_line);
//                step2Title.setTextColor(getResources().getColor(R.color.dig_title_done));
//
//                step3LeftLine.setBackgroundResource(R.drawable.ic_done_line);
//                step3Icon.setBackgroundResource(R.drawable.ic_completed);
//                step3Icon.stopPinning();
//                step3Title.setTextColor(getResources().getColor(R.color.dig_title_done));
//                step3RightLine.setBackgroundResource(R.drawable.ic_done_line);
//
//                step4LeftLine.setBackgroundResource(R.drawable.ic_done_line);
//                step4Icon.setBackgroundResource(R.drawable.ic_completed);
//                step4Icon.stopPinning();
//                step4Title.setTextColor(getResources().getColor(R.color.dig_title_done));
//                step4RightLine.setBackgroundResource(R.drawable.ic_done_line);
//
//                step5LeftLine.setBackgroundResource(R.drawable.ic_done_line);
//                step5Icon.setBackgroundResource(R.drawable.ic_completed);
//                step5Icon.stopPinning();
//                step5Title.setTextColor(getResources().getColor(R.color.dig_title_done));
//                step5RightLine.setBackgroundResource(R.drawable.ic_done_line);
//
//                step6LeftLine.setBackgroundResource(R.drawable.ic_done_line);
//                step6Icon.setBackgroundResource(R.drawable.ic_completed);
//                step6Icon.stopPinning();
//                step6Title.setTextColor(getResources().getColor(R.color.dig_title_done));
//                break;
//        }
//    }
//    public void resetViews(){
//        step1Icon.setBackgroundResource(R.drawable.ic_step1);
//        step1Icon.stopPinning();
//        step1Title.setTextColor(getResources().getColor(R.color.dig_title_undone));
//        step1RightLine.setBackgroundResource(R.drawable.ic_undone_line);
//
//        step2LeftLine.setBackgroundResource(R.drawable.ic_undone_line);
//        step2Icon.setBackgroundResource(R.drawable.ic_step2);
//        step2Icon.stopPinning();
//        step2RightLine.setBackgroundResource(R.drawable.ic_undone_line);
//        step2Title.setTextColor(getResources().getColor(R.color.dig_title_undone));
//
//        step3LeftLine.setBackgroundResource(R.drawable.ic_undone_line);
//        step3Icon.setBackgroundResource(R.drawable.ic_step3);
//        step3Icon.stopPinning();
//        step3RightLine.setBackgroundResource(R.drawable.ic_undone_line);
//        step3Title.setTextColor(getResources().getColor(R.color.dig_title_undone));
//
//        step4LeftLine.setBackgroundResource(R.drawable.ic_undone_line);
//        step4Icon.setBackgroundResource(R.drawable.ic_step4);
//        step4Icon.stopPinning();
//        step4RightLine.setBackgroundResource(R.drawable.ic_undone_line);
//        step4Title.setTextColor(getResources().getColor(R.color.dig_title_undone));
//
//        step5LeftLine.setBackgroundResource(R.drawable.ic_undone_line);
//        step5Icon.setBackgroundResource(R.drawable.ic_step5);
//        step5Icon.stopPinning();
//        step5RightLine.setBackgroundResource(R.drawable.ic_undone_line);
//        step5Title.setTextColor(getResources().getColor(R.color.dig_title_undone));
//
//        step6LeftLine.setBackgroundResource(R.drawable.ic_undone_line);
//        step6Icon.setBackgroundResource(R.drawable.ic_step6);
//        step6Icon.stopPinning();
//        step6Title.setTextColor(getResources().getColor(R.color.dig_title_undone));
//
//    }
//}
