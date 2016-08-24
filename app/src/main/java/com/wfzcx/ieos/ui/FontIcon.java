package com.wfzcx.ieos.ui;


import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class FontIcon extends TextView {
    private static Typeface FONT_NAME;

    public FontIcon(Context context, AttributeSet attrs) {
        super(context, attrs);  
        this.loadFont(context);
    }  
    public void loadFont(Context context){
    	if(!isInEditMode()){
    		 super.setTypeface(getloadFont(context));
    	}else{
    		if(FONT_NAME == null) 
    	   		 FONT_NAME = Typeface.createFromFile("/system/fonts/iconfont.ttf");

    			//FONT_NAME = Typeface.createFromFile(new File(Environment.getExternalStorageDirectory(), "assets/iconfont.ttf"));
             super.setTypeface(FONT_NAME);
    	}
    	
    }
    public static Typeface getloadFont(Context context){
   	 if(FONT_NAME == null) 
   		 FONT_NAME = Typeface.createFromAsset(context.getAssets(), "iconfont.ttf");
    	return FONT_NAME;
    }
  
    public void setTypeface(Typeface f){
    	
    }
	public FontIcon(Context context) {
		super(context);
		  this.loadFont(context);
	}
 
	public FontIcon(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	    this.loadFont(context);
	   
	}
}
