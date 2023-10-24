package com.poczinha.log.helper.generate;

import com.poczinha.log.processor.Processor;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GenerateHelper {

    public static List<Diagnostic<? extends JavaFileObject>> execute(List<File> inputs) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(inputs);

        List<String> options = new ArrayList<>();
        options.add("-d");
        options.add("target/generated-sources/annotations/");

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits);
        task.setProcessors(List.of(new Processor()));

        task.call();

        return diagnostics.getDiagnostics();
    }

    public static String fileToString(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }
}
