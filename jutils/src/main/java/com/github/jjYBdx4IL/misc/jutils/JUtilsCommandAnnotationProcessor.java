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

import java.io.File;
import java.io.IOException;
import java.lang.annotation.AnnotationFormatError;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
@SupportedAnnotationTypes("com.github.jjYBdx4IL.misc.jutils.JUtilsCommandAnnotation")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class JUtilsCommandAnnotationProcessor extends AbstractProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(JUtilsCommandAnnotationProcessor.class);

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
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
                throw new AnnotationFormatError(elem.toString() + " type not supported by " + JUtilsCommandAnnotation.class);
            }

            TypeMirror requiredIface = elementUtils.getTypeElement(JUtilsCommandInterface.class.getCanonicalName()).asType();
            if (typeUtils.isAssignable(requiredIface, elem.asType())) {
                throw new AnnotationFormatError(elem.toString() + " does not implement " + JUtilsCommandInterface.class);
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
            getAllCommandInstancesMethod.body()
                    .invoke(cmdMapVar, "put")
                    .arg(annotation.name())
                    .arg(JExpr._new(cmdType));
        }
        getAllCommandInstancesMethod.body()._return(cmdMapVar);

        AbstractJClass annotationValuesType = cm.ref(AnnotationValues.class);
        AbstractJClass hashMapStringAVType = cm.ref(HashMap.class).narrow(stringType, annotationValuesType);
        AbstractJClass mapStringAVType = mapType.narrow(stringType, annotationValuesType);

        JMethod getAllCommandHelpTexts = cls.method(JMod.STATIC, mapStringAVType, "getAllCommandAnnotationValues");
        JVar avMapVar = getAllCommandHelpTexts.body()
                .decl(mapStringAVType, "annotationValuesMap", JExpr._new(hashMapStringAVType));
        for (Element element : classesFound) {
            JUtilsCommandAnnotation annotation = element.getAnnotation(JUtilsCommandAnnotation.class);
            getAllCommandHelpTexts.body()
                    .invoke(avMapVar, "put")
                    .arg(annotation.name())
                    .arg(JExpr._new(annotationValuesType)
                            .arg(JExpr.lit(annotation.name()))
                            .arg(JExpr.lit(annotation.help()))
                            .arg(JExpr.lit(annotation.usage()))
                            .arg(JExpr.lit(annotation.minArgs()))
                            .arg(JExpr.lit(annotation.maxArgs())));
        }
        getAllCommandHelpTexts.body()._return(avMapVar);

        File outputDir = new File(System.getProperty("basedir"), "target/generated-sources/anno");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        LOG.info("writing source code to: " + outputDir.getAbsolutePath());
        cm.build(new FileCodeWriter(outputDir));
    }
}
