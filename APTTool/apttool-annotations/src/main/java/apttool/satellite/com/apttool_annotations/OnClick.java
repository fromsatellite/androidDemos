package apttool.satellite.com.apttool_annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bind a method to an android.view.View.OnClickListener on the view for each ID specified.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD})
public @interface OnClick {
    public int[] value();
}
