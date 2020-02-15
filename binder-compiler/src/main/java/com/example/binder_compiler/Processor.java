package com.example.binder_compiler;

import com.example.binder_annotations.BindView;
import com.example.binder_annotations.Keep;
import com.example.binder_annotations.OnClick;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * @author Lokesh chennamchetty
 * @date 07/02/2020
 */
public class Processor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {

            Set<TypeElement> typeElements = ProcessorUtils.getTypeElementToProcess(annotations,
                    roundEnv.getRootElements());

            for (TypeElement typeElement : typeElements) {
                String packageName  = elementUtils.getPackageOf(typeElement)
                        .getQualifiedName().toString();
                String typeName = typeElement.getSimpleName().toString();
                ClassName className = ClassName.get(packageName, typeName);

                ClassName genertedClassName = ClassName.get(packageName,
                        NameStore.getGeneratedClassName(typeName));

                TypeSpec.Builder classBuilder = TypeSpec.classBuilder(genertedClassName)
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Keep.class);

                classBuilder.addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(className, NameStore.Field.ACTIVITY)
                        .addStatement("$N($N)",NameStore.Methods.BIND_VIEW,NameStore.Field.ACTIVITY)
                        .addStatement("$N($N)",NameStore.Methods.BIND_ON_CLICK,NameStore.Field.ACTIVITY)
                        .build());



                MethodSpec.Builder bindViewMethodBuilder = MethodSpec.methodBuilder(NameStore.Methods.BIND_VIEW)
                        .addModifiers(Modifier.PRIVATE)
                        .returns(void.class)
                        .addParameter(className, NameStore.Field.ACTIVITY);

                for (VariableElement variableElement : ElementFilter.fieldsIn(typeElement.getEnclosedElements())) {
                    BindView bindView =  variableElement.getAnnotation(BindView.class);
                    if (bindView != null) {
                        bindViewMethodBuilder
                                .addStatement("$N.$N = ($T)$N.findViewById($L)",
                                        NameStore.Field.ACTIVITY,
                                        variableElement.getSimpleName(),
                                        variableElement,
                                        NameStore.Field.ACTIVITY,
                                        bindView.value());
                    }
                }

                classBuilder.addMethod(bindViewMethodBuilder.build());

                MethodSpec.Builder bindOnClickMethod= MethodSpec.methodBuilder(NameStore.Methods.BIND_ON_CLICK)
                        .returns(void.class)
                        .addParameter(className, NameStore.Field.ACTIVITY,Modifier.FINAL);

                ClassName androidClickListener = ClassName.get(NameStore.Package.ANDROID_VIEW,
                        NameStore.Class.ANDROID_VIEW,
                        NameStore.Class.ANDROID_VIEW_ON_CLICK_LISTENER);

                ClassName viedClass = ClassName.get(NameStore.Package.ANDROID_VIEW,NameStore.Class.ANDROID_VIEW);

                ParameterSpec viewParameter = ParameterSpec
                        .builder(viedClass, NameStore.Field.VIEW)
                        .build();


                for (ExecutableElement methhod : ElementFilter.methodsIn(typeElement.getEnclosedElements())) {
                    OnClick onClick = methhod.getAnnotation(OnClick.class);
                    if (onClick != null) {
                        TypeSpec listenerMethod = TypeSpec.anonymousClassBuilder("").
                                addSuperinterface(androidClickListener)
                                .addMethod(MethodSpec.methodBuilder(NameStore.Methods.ANDROID_VIEW_ON_CLICK)
                                        .addModifiers(Modifier.PUBLIC)
                                        .addParameter(viewParameter)
                                        //                activity.bt2Click(view);
                                        .addStatement("$N.$N()",
                                                NameStore.Field.ACTIVITY,
                                                methhod.getSimpleName())
                                        .returns(void.class).build())
                                .build();

                        bindOnClickMethod.addStatement("$N.findViewById($L).setOnClickListener($L)",
                                        NameStore.Field.ACTIVITY,
                                        onClick.value(),
                                        listenerMethod);
                    }
                }

                classBuilder.addMethod(bindOnClickMethod.build());

                JavaFile javaFile = JavaFile.builder(packageName, classBuilder.build())
                        .build();

                try {
                    javaFile.writeTo(filer);
                } catch (IOException e) {
                    messager.printMessage(Diagnostic.Kind.ERROR,e.getMessage(),typeElement);
                }
            }
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new TreeSet<>(Arrays.asList(
                BindView.class.getCanonicalName(),
                OnClick.class.getCanonicalName(),
                Keep.class.getCanonicalName()));

    }
}
