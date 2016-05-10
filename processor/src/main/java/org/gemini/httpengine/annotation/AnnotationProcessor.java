package org.gemini.httpengine.annotation;

import org.gemini.httpengine.inject.APIClassInjector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Created by geminiwen on 15/5/20.
 */
public class AnnotationProcessor extends AbstractProcessor {

    private static final String SUFFIX = "$$APIINJECTOR";

    private Filer filer;
    private Elements elementUtils;
    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);

        filer = env.getFiler();
        elementUtils = env.getElementUtils();
        typeUtils = env.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, APIClassInjector> targetClassMap = findAndParseTargets(roundEnv);

        for (Map.Entry<TypeElement, APIClassInjector> entry : targetClassMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            APIClassInjector injector = entry.getValue();
            try {
                String value = injector.brewJava();

                JavaFileObject jfo = filer.createSourceFile(injector.getFqcn(), typeElement);
                Writer writer = jfo.openWriter();
                writer.write(value);
                writer.flush();
                writer.close();
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), typeElement);
            }
        }


        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(NetWorkParams.class.getCanonicalName());
        return supportTypes;
    }

    private Map<TypeElement, APIClassInjector> findAndParseTargets(RoundEnvironment env) {
        Map<TypeElement, APIClassInjector> targetClassMap = new LinkedHashMap<>();

        for (Element element : env.getElementsAnnotatedWith(NetWorkParams.class)) {
            /**
             * Error:Execution failed for task ':examples:compileDebugJavaWithJavac'.
             > java.lang.ClassCastException: com.sun.tools.javac.code.Symbol$ClassSymbol cannot be cast to javax.lang.model.element.ExecutableElement
             dependency cache may be corrupt (this sometimes occurs after a network connection timeout.)
             <a href="syncProject">Re-download dependencies and sync project (requires network)</a></li><li>The state of a Gradle build process (daemon) may be corrupt. Stopping all Gradle daemons may solve this problem.
             <a href="stopGradleDaemons">Stop Gradle build processes (requires restart)</a></li><li>Your project may be using a third-party plugin which is not compatible with the other plugins in the project or the version of Gradle requested by the project.</li></ul>In the case of corrupt Gradle processes, you can also try closing the IDE and then killing all Java processes.

             ERROR:javax.annotation.processing.Processor: Error reading configuration file
             */
            /* This line takes me to much time.
             * (1)how to debug, a little skills ./gradlew clean build --stacktrace
             * (2)ExecutableElement is not same to Element. Method can use ExecutableElement.
             * Class use Element.
             * */
             ExecutableElement executableElement = (ExecutableElement) element;

            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

            APIClassInjector injector = getOrCreateTargetClass(targetClassMap, enclosingElement);
//            APIMethodInjector methodInjector = new APIMethodInjector(executableElement);
//            injector.addMethod(methodInjector);
        }
        return targetClassMap;
    }

    /**
     *
     * @param targetClassMap
     * @param enclosingElement
     * @return
     */
    private APIClassInjector getOrCreateTargetClass(Map<TypeElement, APIClassInjector> targetClassMap, TypeElement enclosingElement) {
        APIClassInjector injector = targetClassMap.get(enclosingElement);
        if (injector == null) {
            String targetType = enclosingElement.getQualifiedName().toString();
            String classPackage = getPackageName(enclosingElement);
            String className = getClassName(enclosingElement, classPackage) + SUFFIX;
            TypeMirror elementType = enclosingElement.asType();
            boolean isInterface = isInterface(elementType);

            injector = new APIClassInjector(classPackage, className, targetType, isInterface);
            targetClassMap.put(enclosingElement, injector);
        }
        return injector;
    }

    private boolean isInterface(TypeMirror typeMirror) {
        if (!(typeMirror instanceof DeclaredType)) {
            return false;
        }
        return ((DeclaredType) typeMirror).asElement().getKind() == ElementKind.INTERFACE;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    /**
     *
     * @param type
     * @param packageName
     * @return
     */
    private String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }

    /**
     *
     * @param type
     * @return
     */
    private String getPackageName(TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    private void writeLog(String str) {
        try {
            FileWriter fw = new FileWriter(new File("D://process.txt"), true);
            fw.write(str + "\n");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
