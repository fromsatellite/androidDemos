package apttool.satellite.com.apt_compiler;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import apttool.satellite.com.apt_compiler.model.FieldViewBinding;
import apttool.satellite.com.apt_compiler.model.MethodViewBinding;
import apttool.satellite.com.apt_compiler.model.ProxyClass;
import apttool.satellite.com.apttool_annotations.OnClick;
import apttool.satellite.com.apttool_annotations.ViewById;

/**
 * 注解处理器
 */
// 属于auto-service库，可以自动生成META-INF/services/javax.annotation.processing.Processor文件（该文件是所有注解处理器都必须定义的），免去了我们手动配置的麻烦。
@AutoService(Processor.class)
public class AptToolProcessor extends AbstractProcessor{

    // 文件相关工具类
    private Filer mFiler;
    // 元素相关的工具类
    private Elements mElementUtils;
    // 日志相关工具类
    private Messager messager;
    // 保存代理类信息
    private Map<String, ProxyClass> proxyClassMap = new HashMap<>();

    /**
     * 处理器的初始化方法，可以获取相关的工具类
     * @param processingEnv
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
        messager = processingEnv.getMessager();
    }

    /**
     * 处理器的主方法，用于扫描处理注解，生成java、文件
     * @param annotations
     * @param roundEnv
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 1、收集注解信息
        // 处理被ViewById注解的元素
        for (Element element : roundEnv.getElementsAnnotatedWith(ViewById.class)){
            // 2、匹配准则
            if (!isValid(ViewById.class, "fields", element)){
                return true;
            }
            parseViewById(element);
        }

        // 处理被OnClick注解的元素
        for (Element element : roundEnv.getElementsAnnotatedWith(OnClick.class)){
            // 2、匹配准则
            if (!isValid(OnClick.class, "methods", element)){
                return true;
            }
            try {
                parseOnClick(element);
            } catch (IllegalArgumentException e) {
//                e.printStackTrace();
                error(element, e.getMessage());
                return true;
            }
        }

        // 为每个宿主类生成对应的代理类
        for (ProxyClass proxyClass : proxyClassMap.values()){
            try {
                proxyClass.generateProxy().writeTo(mFiler);
            } catch (IOException e) {
//                e.printStackTrace();
                error(null, e.getMessage());
            }
        }
        // 循环结束后我们还执行了mProxyClassMap.clear()，原因就在于process()可以被多次调用，因为新生成的Java文件很可能包括@ViewById注解，所以process方法会多次执行直到没有生成该注解为止。所以我们应该清空之前的数据，避免生成重复的代理类。
        proxyClassMap.clear();

        return true;
    }

    /**
     * 处理{@link ViewById}注解
     * @param element
     */
    private void parseViewById(Element element) {
        ProxyClass proxyClass = getProxyClass(element);
        // 把被注解的View对象封装成一个model，加入到代理类的集合中
        FieldViewBinding bindView = new FieldViewBinding(element);
        proxyClass.add(bindView);
    }

    /**
     * 处理{@link OnClick}注解
     * @param element
     */
    private void parseOnClick(Element element) throws IllegalArgumentException{
        ProxyClass proxyClass = getProxyClass(element);
        // 把被注解的View对象封装成一个model，加入到代理类的集合中
        MethodViewBinding bindView = new MethodViewBinding((ExecutableElement) element);
        proxyClass.add(bindView);
    }

    /**
     * 生成或者获取注解元素对应的{@link ProxyClass}类
     * @param element
     */
    private ProxyClass getProxyClass(Element element) {
        // 被注解的变量所在的类
        TypeElement classElement = (TypeElement) element.getEnclosingElement();
        String qualifiedName = classElement.getQualifiedName().toString();
        ProxyClass proxyClass = proxyClassMap.get(qualifiedName);
        if (proxyClass == null){
            // 生成每个宿主类对应的代理类,后面用于生成java文件
            proxyClass = new ProxyClass(classElement, mElementUtils);
            proxyClassMap.put(qualifiedName, proxyClass);
        }
        return proxyClass;
    }

    private boolean isValid(Class<? extends Annotation> annotationClass, String targetThing,
                            Element element){
        boolean isValid = true;

        // 获取变量所在的父元素，可能是类、接口、枚举
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        // 父元素的全限定名
        String qualifiedName = enclosingElement.getQualifiedName().toString();

        // 注解元素不能是private或者static修饰
        Set<Modifier> elementModifiers = element.getModifiers();
        if (elementModifiers.contains(Modifier.PRIVATE) || elementModifiers.contains(Modifier.STATIC)) {
            error(element, "@%s %s must not be private or static. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            isValid = false;
        }

        // 所在的类不能是private或者static修饰
        Set<Modifier> modifiers = enclosingElement.getModifiers();
        if (modifiers.contains(Modifier.PRIVATE) || modifiers.contains(Modifier.STATIC)){
            error(enclosingElement, "@%s %s must not be private or static. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            isValid = false;
        }

        // 父元素必须是类,而不是接口或者枚举
        if (enclosingElement.getKind() != ElementKind.CLASS){
            error(enclosingElement, "@%s %s may only be contained in classes. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            isValid = false;
        }

        // 不能在Android框架层注解
        if (qualifiedName.startsWith("android.")){
            error(element, "@%s-annotated class incorrectly in Android framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName);
            isValid = false;
        }

        // 不能在java框架层注解
        if (qualifiedName.startsWith("java.")){
            error(element, "@%s-annotated class incorrectly in Java framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName);
            isValid = false;
        }

        return isValid;
    }

    // 3、错误处理
    private void error(Element e, String msg, Object... args){
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    /**
     * 指定哪些注解应该被处理器注册
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(ViewById.class.getName());
        types.add(OnClick.class.getName());
        return types;
    }

    /**
     * 用来指定使用的java版本
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
