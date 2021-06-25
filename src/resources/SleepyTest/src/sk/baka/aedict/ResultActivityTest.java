/**
 *     Aedict - an EDICT browser for Android
 Copyright (C) 2009 Martin Vysny

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package sk.baka.aedict;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import sk.baka.aedict.dict.DictEntry;
import sk.baka.aedict.dict.DictTypeEnum;
import sk.baka.aedict.dict.EdictEntry;
import sk.baka.aedict.dict.MatcherEnum;
import sk.baka.aedict.dict.SearchQuery;
import sk.baka.aedict.kanji.RomanizationEnum;
import android.app.Activity;
import android.content.Intent;
import android.widget.ListView;

/**
 * Tests the ResultActivity activity.
 *
 * @author Martin Vysny
 *
 */
public class ResultActivityTest extends AbstractAedictTest<ResultActivity> {

    public ResultActivityTest() {
        super(ResultActivity.class);
    }

    public void testSimpleEnglishSearch() throws Exception {
        final SearchQuery q = new SearchQuery(DictTypeEnum.Edict);
        q.isJapanese = false;
        q.matcher = MatcherEnum.Exact;
        q.query = new String[] { "mother" };
        final List<DictEntry> result = launch(q);
        assertTrue(tester.getText(R.id.textSelectedDictionary).contains("Default"));
        final DictEntry entry = result.get(0);
        assertEquals("(n) (hum) mother/(P)", entry.english);
        assertEquals("母", entry.getJapanese());
        assertEquals("はは", entry.reading);
        assertEquals(38, result.size());
    }

    private List<DictEntry> launch(final SearchQuery q) throws Exception {
        final Intent i = new Intent(getInstrumentation().getContext(), ResultActivity.class);
        i.putExtra(ResultActivity.INTENTKEY_SEARCH_QUERY, (Serializable) Collections.singletonList(q));
        tester.startActivity(i);
        final ListView lv = getActivity().getListView();
        assertEquals(1, lv.getCount());
        final DictEntry entry = (DictEntry) lv.getItemAtPosition(0);
        assertEquals("Searching", entry.english);
        Thread.sleep(500);
        tester.assertRequestedActivity(ResultActivity.class);
        final Intent i2 = getStartedActivityIntent();
        final List<DictEntry> result = (List<DictEntry>) i2.getSerializableExtra(ResultActivity.INTENTKEY_RESULT_LIST);
        return result;
    }

    private void launch(boolean isSimeji) {
        final Intent i = new Intent(getInstrumentation().getContext(), ResultActivity.class);
        final DictEntry entry = new EdictEntry("母", "はは", "(n) (hum) mother/(P)");
        i.putExtra(ResultActivity.INTENTKEY_RESULT_LIST, (Serializable) Collections.singletonList(entry));
        if (isSimeji) {
            i.putExtra(ResultActivity.INTENTKEY_SIMEJI, isSimeji);
        }
        final SearchQuery q = new SearchQuery(DictTypeEnum.Edict);
        q.isJapanese = false;
        q.matcher = MatcherEnum.Exact;
        q.query = new String[] { "mother" };
        i.putExtra(ResultActivity.INTENTKEY_SEARCH_QUERY, (Serializable) Collections.singletonList(q));
        tester.startActivity(i);
    }

    private void launch() {
        launch(false);
    }

    public void testSimpleJapaneseSearch() throws Exception {
        final SearchQuery q = new SearchQuery(DictTypeEnum.Edict);
        q.isJapanese = true;
        q.matcher = MatcherEnum.Exact;
        q.query = new String[] { RomanizationEnum.Hepburn.toHiragana("haha") };
        final List<DictEntry> result = launch(q);
        assertTrue(tester.getText(R.id.textSelectedDictionary).contains("Default"));
        assertEquals(1, result.size());
        final DictEntry entry = result.get(0);
        assertEquals("(n) (hum) mother/(P)", entry.english);
        assertEquals("母", entry.getJapanese());
    }

    public void testSubstringJapaneseSearch() throws Exception {
        final SearchQuery q = new SearchQuery(DictTypeEnum.Edict);
        q.isJapanese = true;
        q.matcher = MatcherEnum.Substring;
        q.query = new String[] { RomanizationEnum.Hepburn.toHiragana("haha"), RomanizationEnum.Hepburn.toKatakana("haha") };
        final List<DictEntry> result = launch(q);
        assertTrue(tester.getText(R.id.textSelectedDictionary).contains("Default"));
        final DictEntry entry = result.get(0);
        assertEquals("(n) (hum) mother/(P)", entry.english);
        assertEquals("母", entry.getJapanese());
        assertEquals(36, result.size());
    }

    /**
     * Test for the http://code.google.com/p/aedict/issues/detail?id=30 bug. The
     * problem was that there are ~2500 matches for kyou however only the first
     * 100 were retrieved from Lucene and they were further filtered.
     */
    public void testSearchForKyou() throws Exception {
        final SearchQuery q = new SearchQuery(DictTypeEnum.Edict);
        q.isJapanese = true;
        q.matcher = MatcherEnum.Exact;
        q.query = new String[] { RomanizationEnum.Hepburn.toHiragana("kyou") };
        final List<DictEntry> result = launch(q);
        assertTrue(tester.getText(R.id.textSelectedDictionary).contains("Default"));
        assertEquals(18, result.size());
        DictEntry entry = result.get(0);
        assertEquals("(n) (1) imperial capital (esp. Kyoto)/(2) final word of an iroha-uta/(3) 10^16/10,000,000,000,000,000/ten quadrillion (American)/(obs) ten thousand billion (British)/(P)", entry.english);
        assertEquals("京", entry.getJapanese());
        entry = (DictEntry) result.get(6);
        assertEquals("(n-t) (1) today/this day/(P)", entry.english);
        assertEquals("今日", entry.getJapanese());
    }

    public void testSwitchToRomaji() {
        launch();
        tester.optionMenu(10000);
        assertTrue(getActivity().showRomaji.isShowingRomaji());
    }

    public void testShowEntryDetail() {
        launch();
        final ListView lv = getActivity().getListView();
        lv.performItemClick(null, 0, 0);
        tester.assertRequestedActivity(EdictEntryDetailActivity.class);
        final DictEntry entry = (DictEntry) getStartedActivityIntent().getSerializableExtra(EdictEntryDetailActivity.INTENTKEY_ENTRY);
        assertEquals("(n) (hum) mother/(P)", entry.english);
        assertEquals("母", entry.getJapanese());
        assertEquals("はは", entry.reading);
    }

    public void testAddToNotepad() {
        launch();
        final ListView lv = getActivity().getListView();
        tester.contextMenu(lv, 1, 0);
        tester.assertRequestedActivity(NotepadActivity.class);
        final DictEntry entry = (DictEntry) getStartedActivityIntent().getSerializableExtra(NotepadActivity.INTENTKEY_ADD_ENTRY);
        assertEquals("(n) (hum) mother/(P)", entry.english);
        assertEquals("母", entry.getJapanese());
        assertEquals("はは", entry.reading);
    }

    public void testSimejiSearch() throws Exception {
        final Intent i = new Intent(getInstrumentation().getContext(), ResultActivity.class);
        i.setAction(ResultActivity.SIMEJI_ACTION_INTERCEPT);
        i.putExtra(ResultActivity.SIMEJI_INTENTKEY_REPLACE, "mother");
        tester.startActivity(i);
        assertTrue(tester.getText(R.id.textSelectedDictionary).contains("Default"));
        final ListView lv = getActivity().getListView();
        assertEquals(1, lv.getCount());
        DictEntry entry = (DictEntry) lv.getItemAtPosition(0);
        assertEquals("Searching", entry.english);
        Thread.sleep(500);
        final Intent i2 = getStartedActivityIntent();
        final List<DictEntry> result = (List<DictEntry>) i2.getSerializableExtra(ResultActivity.INTENTKEY_RESULT_LIST);
        final boolean isSimeji = i2.getBooleanExtra(ResultActivity.INTENTKEY_SIMEJI, false);
        entry = result.get(0);
        assertEquals("(n) (hum) mother/(P)", entry.english);
        assertEquals("母", entry.getJapanese());
        assertEquals("はは", entry.reading);
        assertEquals(38, result.size());
        assertEquals(ResultActivity.SIMEJI_ACTION_INTERCEPT, i2.getAction());
    }

    public void testEdictExternSearch() throws Exception {
        final Intent i = new Intent(getInstrumentation().getContext(), ResultActivity.class);
        i.setAction(ResultActivity.EDICT_ACTION_INTERCEPT);
        i.putExtra(ResultActivity.EDICT_INTENTKEY_KANJIS, "空白");
        tester.startActivity(i);
        assertTrue(tester.getText(R.id.textSelectedDictionary).contains("Default"));
        final ListView lv = getActivity().getListView();
        assertEquals(1, lv.getCount());
        DictEntry entry = (DictEntry) lv.getItemAtPosition(0);
        assertEquals("Searching", entry.english);
        Thread.sleep(500);
        final Intent i2 = getStartedActivityIntent();
        final List<DictEntry> result = (List<DictEntry>) i2.getSerializableExtra(ResultActivity.INTENTKEY_RESULT_LIST);
        entry = result.get(0);
        assertEquals("(adj-na,n,adj-no) blank space/vacuum/space/null (NUL)/(P)", entry.english);
        assertEquals("空白", entry.getJapanese());
        assertEquals("くうはく", entry.reading);
        assertEquals(1, result.size());
    }

    public void testSimejiSearchKanji() throws Exception {
        launch(true);
        final ListView lv = getActivity().getListView();
        tester.contextMenu(lv, 2, 0);
        assertSimejiReturn("母");
    }

    public void testSimejiSearchReading() {
        launch(true);
        final ListView lv = getActivity().getListView();
        tester.contextMenu(lv, 3, 0);
        assertSimejiReturn("はは");
    }

    public void testSimejiSearchEnglish() {
        launch(true);
        final ListView lv = getActivity().getListView();
        tester.contextMenu(lv, 4, 0);
        assertSimejiReturn("(n) (hum) mother/(P)");
    }

    private void assertSimejiReturn(final String expected) {
        assertEquals(Activity.RESULT_OK, getFinishedActivityRequest());
        assertEquals(expected, tester.getResultIntent().getStringExtra(ResultActivity.SIMEJI_INTENTKEY_REPLACE));
    }

    public void testSimpleEnglishSearchInTanaka() throws Exception {
        final SearchQuery q = new SearchQuery(DictTypeEnum.Tanaka);
        q.isJapanese = false;
        q.matcher = MatcherEnum.Substring;
        q.query = new String[] { "mother" };
        final List<DictEntry> result = launch(q);
        assertTrue(tester.getText(R.id.textSelectedDictionary).contains("Tanaka"));
        final DictEntry entry = result.get(0);
        assertEquals("Mother is away from home.", entry.english);
        assertEquals("母は留守です。", entry.getJapanese());
        assertEquals("はは は るす です。", entry.reading);
        assertEquals(100, result.size());
    }

    public void testMultiwordEnglishSearchInTanaka() throws Exception {
        final SearchQuery q = new SearchQuery(DictTypeEnum.Tanaka);
        q.isJapanese = false;
        q.matcher = MatcherEnum.Substring;
        q.query = new String[] { "mother tongue" };
        final List<DictEntry> result = launch(q);
        assertTrue(tester.getText(R.id.textSelectedDictionary).contains("Tanaka"));
        final DictEntry entry = (DictEntry) result.get(0);
        assertEquals("My mother tongue is Japanese.", entry.english);
        assertEquals("私の母語は日本語です。", entry.getJapanese());
        assertEquals(RomanizationEnum.Hepburn.toHiragana("watashino bogo ha nihongo desu。"), entry.reading);
        assertEquals(15, result.size());
    }

    public void testComplexJapaneseSearchInTanaka() throws Exception {
        final SearchQuery q = new SearchQuery(DictTypeEnum.Tanaka);
        q.isJapanese = true;
        q.matcher = MatcherEnum.Substring;
        q.query = new String[] { "母", "はは" };
        final List<DictEntry> result = launch(q);
        assertTrue(tester.getText(R.id.textSelectedDictionary).contains("Tanaka"));
        final DictEntry entry = (DictEntry) result.get(0);
        assertEquals("Mother has old-fashioned ideas.", entry.english);
        assertEquals("母は頭が古い。", entry.getJapanese());
        assertEquals(RomanizationEnum.Hepburn.toHiragana("haha ha atama ga furui。"), entry.reading);
        assertEquals(100, result.size());
    }

    public void testComplexJapaneseAndSearch() throws Exception {
        final SearchQuery q = new SearchQuery(DictTypeEnum.Edict);
        q.isJapanese = true;
        q.matcher = MatcherEnum.Substring;
        q.query = new String[] {"はは AND 父" };
        final List<DictEntry> result = launch(q);
        assertTrue(tester.getText(R.id.textSelectedDictionary).contains("Default"));
        final DictEntry entry = result.get(0);
        assertEquals("(n) father and mother/parents", entry.english);
        assertEquals("父母", entry.getJapanese());
        assertEquals("ちちはは", entry.reading);
        assertEquals(1, result.size());
    }

    public void testSodAnalysis() {
        launch();
        tester.contextMenu(getActivity().getListView(), 6, 0);
        tester.assertRequestedActivity(StrokeOrderActivity.class);
        final String q = getStartedActivityIntent().getStringExtra(StrokeOrderActivity.INTENTKEY_KANJILIST);
        assertEquals("母", q);
    }
}
