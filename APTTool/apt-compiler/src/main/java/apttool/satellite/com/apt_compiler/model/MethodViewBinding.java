package apttool.satellite.com.apt_compiler.model;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import apttool.satellite.com.apttool_annotations.OnClick;

public class MethodViewBinding {

    /**
     * 注解元素，即一个方法
     */
    private ExecutableElement executableElement;
    /**
     * 方法名称
     */
    private String methodName;
    /**
     * 监听的View的id集合
     */
    private int[] ids;

    private boolean parameterEixt;
    /**
     * 参数名称
     */
    private String parameterName;

    public MethodViewBinding(ExecutableElement executableElement) throws IllegalArgumentException {
        this.executableElement = executableElement;

        OnClick onClick = executableElement.getAnnotation(OnClick.class);
        // 获取方法名称
        methodName = executableElement.getSimpleName().toString();
        // 获取ids
        ids = onClick.value();

        // 参数
        List<? extends VariableElement> parameters = executableElement.getParameters();
        if (parameters.size() > 1){ // 参数不能超过一个
            throw new IllegalArgumentException(
                    String.format("The method annotated with @%s must less two parameters", OnClick.class.getSimpleName())
            );
        }
        if (parameters.size() == 1) { // 参数必须是View类型
            VariableElement variableElement = parameters.get(0);
            if (!variableElement.asType().toString().equals(ProxyClass.VIEW.toString())){
                throw new IllegalArgumentException(
                        String.format("The method parameter must be %s type", ProxyClass.VIEW.toString()));
            }
            parameterEixt = true;
            parameterName = variableElement.getSimpleName().toString();
        }
    }
//    public ExecutableElement getExecutableElement() {
//        return executableElement;
//    }

    public String getMethodName() {
        return methodName;
    }

    public int[] getIds() {
        return ids;
    }

    public boolean isParameterEixt() {
        return parameterEixt;
    }

    public String getParameterName() {
        return parameterName;
    }
}
