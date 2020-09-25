package edu.rit.se.testsmells.testsmell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

public class ReportControllerTest {
    TestFile tf;
    AbstractSmell smell;
    List<TestFile> files;
    ReportController sut;
    ResultsWriter resultsWriter;

    @BeforeEach
    public void setUp() throws IOException {
        files = new ArrayList<>();
        smell = mock(AbstractSmell.class);
        tf = mock(TestFile.class);
        resultsWriter = mock(ResultsWriter.class);
        sut = new ReportController(resultsWriter);
    }

    @Test
    public void testReportDoesNotThrow() {
        assertDoesNotThrow(() -> sut.report(files));
    }

    @Test
    public void testReportNotCallExportSmells() throws IOException {
        sut.report(files);
        verify(resultsWriter, never()).exportSmells(any());
    }

    @Test
    public void testReportCallExportSmellsFromFile() throws IOException {
        files.add(tf);
        when(tf.getTestSmells()).thenReturn(Arrays.asList(smell));

        sut.report(files);

        verify(resultsWriter, atLeastOnce()).exportSmells(tf);
    }

    @Test
    public void testReportCallExportSmells() throws IOException {
        files.add(tf);
        TestMethod smellyElem = new TestMethod("anyMethod");
        when(smell.getSmellyElements()).thenReturn(Arrays.asList(smellyElem));
        when(tf.getTestSmells()).thenReturn(Arrays.asList(smell));

        sut.report(files);

        verify(resultsWriter, atLeastOnce()).exportSmells(tf);
        //verify(resultsWriter, atLeastOnce()).exportSmells(smellyElem); TODO: make exportSmells(smellyElem) be detectable
    }
}
