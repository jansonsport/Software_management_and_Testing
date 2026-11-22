import java.io.File;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.report.DirectorySourceFileLocator;
import org.jacoco.report.FileMultiReportOutput;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.html.HTMLFormatter;

class JaCoCoCoverageReport {
    public static void main(String[] args) throws Exception {
        ExecFileLoader execution = new ExecFileLoader();
        execution.load(new File(args[0]));

        CoverageBuilder coverage = new CoverageBuilder();
        new Analyzer(execution.getExecutionDataStore(), coverage).analyzeAll(new File(args[1]));

        HTMLFormatter formatter = new HTMLFormatter();
        IReportVisitor report = formatter.createVisitor(new FileMultiReportOutput(new File(args[3])));
        report.visitInfo(execution.getSessionInfoStore().getInfos(), execution.getExecutionDataStore().getContents());
        report.visitBundle(coverage.getBundle(args[4]), new DirectorySourceFileLocator(new File(args[2]), "UTF-8", 4));
        report.visitEnd();
    }
}
