package com.dalthed.tucan;

import android.widget.SpinnerAdapter;

import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.scraper.EventsScraper;
import com.dalthed.tucan.testmodels.EventsModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Map;

import static org.junit.Assert.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class EventsScraperTest extends TestBase {

    public EventsScraperTest() {
        this.resourcesBaseName = "Events";
        this.testClazzModel = EventsModel.class;
    }

    @Test
    public void testSpinner() {

        for (Map.Entry<String, String> entry : sourcesMap.entrySet()) {

            String id = entry.getKey();
            Object resultObject = resultsMap.get(id);
            if (resultObject instanceof EventsModel) {
                EventsModel result = (EventsModel) resultObject;
                if (result.testSpinner.runTest) {
                    System.out.println("Testing " + id + " (testSpinner)");
                    //System.out.println(result);
                    AnswerObject answer = new AnswerObject(entry.getValue(), "", new CookieManager(), "");
                    EventsScraper scraper = new EventsScraper(RuntimeEnvironment.application, answer);
                    SpinnerAdapter spinnerAdapter = scraper.spinnerAdapter();
                    assertEquals(spinnerAdapter.getCount(), result.testSpinner.data.size());
                    for (int i = 0; i < spinnerAdapter.getCount(); i++) {
                        assertEquals(spinnerAdapter.getItem(i), result.testSpinner.data.get(i));
                    }
                }
            }
        }
    }
}
