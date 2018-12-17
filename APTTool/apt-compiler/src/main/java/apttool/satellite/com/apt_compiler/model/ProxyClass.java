package apttool.satellite.com.apt_compiler.model;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * 注解所在的类
 */
public class ProxyClass {
    // IProxy
    public static final ClassName IPROXY = ClassName.get("com.satellite.apttool_api", "IProxy");
    // android.view.View
    public static final ClassName VIEW = ClassName.get("android.view", "View");
    // android.view.View.OnClickListener
    public static final ClassName VIEW_ONCLICK_LISTENER = ClassName.get("android.view", "View", "OnClickListener");
    //生成代理类的后缀名
    public static final String SUFFIX = "$$Proxy";

    /**
     * 类元素
     */
    public TypeElement typeElement;
    /**
     * 元素相关的辅助类
     */
    private Elements elementUtils;
    /**
     * 注解集合
     */
    private Set<FieldViewBinding> bindViews = new HashSet<>();
    private Set<MethodViewBinding> methods = new HashSet<>();

    public ProxyClass(TypeElement typeElement, Elements elementUtils) {
        this.typeElement = typeElement;
        this.elementUtils = elementUtils;
    }

    public void add(FieldViewBinding binding){
        bindViews.add(binding);
    }

    public void add(MethodViewBinding binding){
        methods.add(binding);
    }

    /**
     * 生成代理类(注意表达式中N(对象的属性) L(参数或者值) T(声明类型)的区别)
     * @return
     */
    public JavaFile generateProxy(){
        // 生成public void inject(final T target, View root)方法,以便api模块中反射调用
        MethodSpec.Builder injectMethodBuilder = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(TypeName.get(typeElement.asType()), "target", Modifier.FINAL)
                .addParameter(VIEW, "root");

        // 在inject方法中，加入我们的findViewById逻辑
        for (FieldViewBinding model : bindViews){
            // find view
            injectMethodBuilder.addStatement("target.$N = ($T)(root.findViewById($L))",
                    model.getVariableName(),
                    ClassName.get(model.getTypeMirror()),
                    model.getResId());
        }

        // 生成注解方法
        // 声明一个listener变量
        if (methods.size() > 0){
            injectMethodBuilder.addStatement("$T listener", VIEW_ONCLICK_LISTENER);
        }
        for (MethodViewBinding method : methods){
            // 初始化Listener,生成匿名内部类
            TypeSpec listener = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(VIEW_ONCLICK_LISTENER)
                    .addMethod(MethodSpec.methodBuilder("onClick")
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(TypeName.VOID)
                            .addParameter(VIEW, "view")
//                            .addStatement("target.$N($L)", method.getMethodName(), method.isParameterEixt() ? method.getParameterName() : "")
                            .addStatement("target.$N($L)", method.getMethodName(), "view")
                            .build())
                    .build();
            injectMethodBuilder.addStatement("listener = $L ", listener);
            for (int id : method.getIds()) {
                // set listeners
                injectMethodBuilder.addStatement("(root.findViewById($L)).setOnClickListener(listener)", id);
            }
        }

        // 添加以$$结尾的类
        TypeSpec finderClass = TypeSpec.classBuilder(typeElement.getSimpleName() + SUFFIX)
                .addModifiers(Modifier.PUBLIC)
                // 添加父接口
                .addSuperinterface(ParameterizedTypeName.get(IPROXY, TypeName.get(typeElement.asType())))
                // 把inject方法添加到类中
                .addMethod(injectMethodBuilder.build())
                .build();

        // 添加包名
        String packageName = elementUtils.getPackageOf(typeElement).getQualifiedName().toString();

        // 生成java文件
        return JavaFile.builder(packageName, finderClass).build();
    }

}
