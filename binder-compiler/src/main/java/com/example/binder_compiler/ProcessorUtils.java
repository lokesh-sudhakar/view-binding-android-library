package com.example.binder_compiler;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * @author Lokesh chennamchetty
 * @date 07/02/2020
 */
public class ProcessorUtils {

    private ProcessorUtils() {

    }

    public  static Set<TypeElement> getTypeElementToProcess(Set<? extends TypeElement> annotations,
                                                            Set<? extends Element> elements){

        Set<TypeElement> typeElements = new HashSet<>();
        for (Element element : elements) {
            if (element instanceof TypeElement) {
                boolean found = false;
                for (Element subElement : element.getEnclosedElements()) {
                    for (AnnotationMirror annotationMirror : subElement.getAnnotationMirrors()) {
                        for (Element annotation : annotations){
                            if (annotationMirror.getAnnotationType().asElement().equals(annotation)) {
                                found = true;
                                typeElements.add((TypeElement) element);
                                break;
                            }
                        } if (found) break;
                    } if (found) break;
                }
            }
        }
        return typeElements;
    }
}
