/*
 * Copyright © 2016 jjYBdx4IL (https://github.com/jjYBdx4IL)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jjYBdx4IL.misc.jutils;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;
import com.helger.jcodemodel.writer.FileCodeWriter;
import com.helger.jcodemodel.writer.JCMWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.AnnotationFormatError;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * JUtilsCommandAnnotationProcessor.
 * 
 * @author jjYBdx4IL
 */
@SupportedAnnotationTypes("com.github.jjYBdx4IL.misc.jutils.JUtilsCommandAnnotation")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class JUtilsCommandAnnotationProcessor extends AbstractProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(JUtilsCommandAnnotationProcessor.class);

    private Types typeUtils;
    private Elements elementUtils;
    @SuppressWarnings("unused")
    private Filer filer;
    @SuppressWarnings("unused")
    private Messager messager;

    public JUtilsCommandAnnotationProcessor() {
        super();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return true;
        }

        List<Element> classesFound = new ArrayList<>();

        for (Element elem : roundEnv.getElementsAnnotatedWith(JUtilsCommandAnnotation.class)) {
            LOG.info("annotation found in " + elem.toString());

            if (!elem.getKind().isClass() || !(elem instanceof QualifiedNameable)) {
                throw new AnnotationFormatError(
                    elem.toString() + " type not supported by " + JUtilsCommandAnnotation.class);
            }

            TypeMirror requiredIface = elementUtils.getTypeElement(JUtilsCommandInterface.class.getCanonicalName())
                .asType();
            if (typeUtils.isAssignable(requiredIface, elem.asType())) {
                throw new AnnotationFormatError(
                    elem.toString() + " does not implement " + JUtilsCommandInterface.class);
            }

            classesFound.add(elem);
        }

        try {
            writeSourceCode(classesFound);
        } catch (JClassAlreadyExistsException | IOException ex) {
            throw new AnnotationFormatError("failed to write source code model", ex);
        }

        return true; // no further processing of this annotation type
    }

    private void writeSourceCode(List<Element> classesFound) throws JClassAlreadyExistsException, IOException {
        JCodeModel cm = new JCodeModel();
        JDefinedClass cls = cm._class(getClass().getPackage().getName() + ".GeneratedCommandCollector");

        AbstractJClass cmdIfaceType = cm.ref(JUtilsCommandInterface.class);
        AbstractJClass stringType = cm.ref(String.class);
        AbstractJClass mapType = cm.ref(Map.class);
        AbstractJClass hashMapStringIfaceType = cm.ref(HashMap.class).narrow(stringType, cmdIfaceType);
        AbstractJClass mapStringIfaceType = mapType.narrow(stringType, cmdIfaceType);

        JMethod getAllCommandInstancesMethod = cls.method(JMod.STATIC, mapStringIfaceType, "getAllCommandInstances");
        JVar cmdMapVar = getAllCommandInstancesMethod.body()
            .decl(mapStringIfaceType, "cmdMap", JExpr._new(hashMapStringIfaceType));
        for (Element element : classesFound) {
            JUtilsCommandAnnotation annotation = element.getAnnotation(JUtilsCommandAnnotation.class);
            AbstractJClass cmdType = cm.ref(element.toString());
            getAllCommandInstancesMethod.body().add(
                JExpr.invoke(cmdMapVar, "put")
                    .arg(annotation.name())
                    .arg(JExpr._new(cmdType)));
        }
        getAllCommandInstancesMethod.body()._return(cmdMapVar);

        AbstractJClass annotationValuesType = cm.ref(AnnotationValues.class);
        AbstractJClass hashMapStringAvType = cm.ref(HashMap.class).narrow(stringType, annotationValuesType);
        AbstractJClass mapStringAvType = mapType.narrow(stringType, annotationValuesType);

        JMethod getAllCommandHelpTexts = cls.method(JMod.STATIC, mapStringAvType, "getAllCommandAnnotationValues");
        JVar avMapVar = getAllCommandHelpTexts.body()
            .decl(mapStringAvType, "annotationValuesMap", JExpr._new(hashMapStringAvType));
        for (Element element : classesFound) {
            JUtilsCommandAnnotation annotation = element.getAnnotation(JUtilsCommandAnnotation.class);
            getAllCommandHelpTexts.body().add(
                JExpr.invoke(avMapVar, "put")
                    .arg(annotation.name())
                    .arg(JExpr._new(annotationValuesType)
                        .arg(JExpr.lit(annotation.name()))
                        .arg(JExpr.lit(annotation.help()))
                        .arg(JExpr.lit(annotation.usage()))
                        .arg(JExpr.lit(annotation.minArgs()))
                        .arg(JExpr.lit(annotation.maxArgs()))));
        }
        getAllCommandHelpTexts.body()._return(avMapVar);

        File outputDir = new File(System.getProperty("basedir"), "target/generated-sources/anno");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        LOG.info("writing source code to: " + outputDir.getAbsolutePath());
        new JCMWriter(cm).setCharset(StandardCharsets.UTF_8).setNewLine("\n").build(new FileCodeWriter(outputDir));
    }
}
