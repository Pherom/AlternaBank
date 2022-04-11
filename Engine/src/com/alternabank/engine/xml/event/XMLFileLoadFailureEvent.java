package com.alternabank.engine.xml.event;

import com.alternabank.engine.xml.XMLLoader;

import java.nio.file.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class XMLFileLoadFailureEvent extends XMLLoadFailureEvent<Path> {

    public XMLFileLoadFailureEvent(XMLLoader source, List<XMLLoadFailureEvent.Cause<Path>> causes, Path trigger) {
        super(source, causes, trigger);
    }

    public enum Cause implements XMLLoadFailureEvent.Cause<Path> {

        NO_SUCH_FILE((filePath) -> !Files.exists(filePath),
                (filePath) -> String.format("The file at: \"%s\" does not exist!", filePath.toAbsolutePath())),
        FILE_NOT_XML((filePath) -> !FileSystems.getDefault().getPathMatcher("glob:**.xml").matches(filePath),
                (filePath) -> String.format("The file at: \"%s\" is not an xml file!", filePath.toAbsolutePath()));

        private final Predicate<Path> predicate;
        private final Function<Path, String> errorMessageGenerator;

        Cause(Predicate<Path> predicate, Function<Path, String> errorMessageGenerator) {
            this.predicate = predicate;
            this.errorMessageGenerator = errorMessageGenerator;
        }

        @Override
        public Predicate<Path> getPredicate() {
            return predicate;
        }

        @Override
        public String getErrorMessage(Path trigger) {
            return errorMessageGenerator.apply(trigger);
        }
    }

}
