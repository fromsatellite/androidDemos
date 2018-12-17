package apttool.satellite.com.apt_compiler.model;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import apttool.satellite.com.apttool_annotations.ViewById;

public class FieldViewBinding {

    /**
     * 注解元素，即一个Field
     */
    private VariableElement element;
    /**
     * 资源id
     */
    private int resId;
    /**
     * 变量名
     */
    private String variableName;
    /**
     * 变量类型
     */
    private TypeMirror typeMirror;

    public FieldViewBinding(Element element) {
        this.element = (VariableElement) element;
        // 资源id
        ViewById viewById = element.getAnnotation(ViewById.class);
        resId = viewById.value();
        // 变量名
        variableName = element.getSimpleName().toString();
        // 变量类型
        typeMirror = element.asType();
    }

//    public VariableElement getElement() {
//        return element;
//    }

    public int getResId() {
        return resId;
    }

    public String getVariableName() {
        return variableName;
    }

    public TypeMirror getTypeMirror() {
        return typeMirror;
    }
}
