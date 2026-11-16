package com.liskovsoft.smartyoutubetv2.common.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Personal content filter (news, politics, Shorts and sexual content).
 *
 * Everything lives in this one file so syncing with the official SmartTube
 * rarely conflicts. To change what gets blocked, edit the lists below.
 *
 * A word matches only as a whole word, ignoring upper/lower case,
 * so "news" blocks "Breaking NEWS today" but not "Newsom" or "renews".
 * (Newsom is blocked separately, in the politicians list.)
 */
public final class ContentFilter {
    /** Hide every YouTube Short, everywhere. */
    public static final boolean BLOCK_SHORTS = true;
    /** Remove the "News" section from the side menu. */
    public static final boolean HIDE_NEWS_SECTION = true;
    /** Turn off the official update check (it would replace this personal build). */
    public static final boolean DISABLE_OFFICIAL_UPDATES = true;
    /** Message shown when a search or a video is blocked. */
    public static final String BLOCKED_MESSAGE = "This topic is blocked";

    // ---------------------------------------------------------------------
    // 1. News words
    // ---------------------------------------------------------------------
    private static final List<String> NEWS_WORDS = Arrays.asList(
            "news", "breaking news", "headlines", "top stories",
            "live update", "live updates", "press conference", "press briefing",
            "newscast", "news report", "news update", "news live", "evening news",
            "morning news", "nightly news", "world news", "local news",
            "news anchor", "journalist",
            "ዜና" // "news" in Tigrinya / Amharic
    );

    // ---------------------------------------------------------------------
    // 2. Politics words
    // ---------------------------------------------------------------------
    private static final List<String> POLITICS_WORDS = Arrays.asList(
            "politics", "political", "politician", "politicians", "ፖለቲካ",
            "election", "elections", "midterm", "midterms", "primary election",
            "vote", "votes", "voting", "voter", "voters", "ballot", "ballots",
            "polls", "polling",
            "president", "presidential", "presidency", "white house",
            "congress", "congressman", "congresswoman", "senate", "senator",
            "house speaker", "governor", "mayor",
            "supreme court", "impeach", "impeachment", "indictment", "indicted",
            "democrat", "democrats", "democratic party",
            "republican", "republicans", "gop", "maga",
            "liberal", "liberals", "conservative", "conservatives",
            "left wing", "left-wing", "right wing", "right-wing",
            "leftist", "leftists",
            "trumpism", "socialism", "socialist",
            "immigration", "deportation", "deportations", "border crisis",
            "tariff", "tariffs", "sanctions",
            "parliament", "prime minister", "minister",
            "geopolitics", "protest", "protests",
            "gaza", "hamas", "hezbollah", "ukraine war", "russia ukraine",
            "israel war", "iran war"
    );

    // ---------------------------------------------------------------------
    // 3. Politicians and world leaders (from all sides)
    // ---------------------------------------------------------------------
    private static final List<String> POLITICIANS = Arrays.asList(
            // United States
            "trump", "donald trump", "biden", "joe biden", "obama", "barack obama",
            "michelle obama", "kamala", "kamala harris", "jd vance", "j.d. vance",
            "hillary clinton", "bill clinton", "pelosi", "schumer", "mcconnell",
            "desantis", "newsom", "bernie sanders", "ocasio-cortez",
            "rfk", "rfk jr", "marjorie taylor greene", "mike pence",
            "nikki haley", "ted cruz", "elizabeth warren", "mike johnson",
            "hakeem jeffries", "george bush", "george w bush", "george w. bush",
            "walz", "tim walz", "vivek ramaswamy",
            // World
            "putin", "zelensky", "zelenskyy", "netanyahu", "xi jinping",
            "kim jong un", "modi", "starmer", "macron", "erdogan", "khamenei",
            "isaias afwerki", "afwerki", "abiy ahmed"
    );

    // ---------------------------------------------------------------------
    // 4. Political commentators and TV hosts (also blocked when named in a title)
    // ---------------------------------------------------------------------
    private static final List<String> COMMENTATORS = Arrays.asList(
            "ben shapiro", "tucker carlson", "candace owens", "matt walsh", "charlie kirk",
            "steven crowder", "tim pool", "piers morgan", "bill maher", "hasan piker",
            "brian tyler cohen", "rachel maddow", "sean hannity", "laura ingraham",
            "jesse watters", "megyn kelly", "alex jones", "joy reid", "don lemon",
            "anderson cooper", "jake tapper", "dave rubin", "glenn beck", "jon stewart"
    );

    // ---------------------------------------------------------------------
    // 5. Sexual / suggestive content
    // ---------------------------------------------------------------------
    private static final List<String> ADULT_WORDS = Arrays.asList(
            "sexy", "sex", "sexual", "nsfw", "18+", "xxx", "porn", "nude", "nudes",
            "naked", "erotic", "seductive", "onlyfans", "only fans", "thirst trap",
            "bikini", "bikinis", "lingerie", "try on haul", "try-on haul",
            "twerk", "twerking", "stripper", "strip club", "hot girl", "hot girls",
            "hottest girls", "boobs", "adult content", "playboy"
    );

    // ---------------------------------------------------------------------
    // 6. News and political channels (matched against the channel name)
    // ---------------------------------------------------------------------
    private static final List<String> CHANNELS = Arrays.asList(
            "cnn", "fox news", "fox business", "msnbc", "ms now", "abc news", "cbs news",
            "nbc news", "bbc news", "sky news", "al jazeera english", "al jazeera",
            "reuters", "associated press", "pbs newshour", "newsnation", "newsmax",
            "cnbc", "cnbc television", "bloomberg television", "bloomberg originals",
            "c-span", "dw", "dw news", "dw documentary", "crux", "france 24 english", "wion", "channel 4 news",
            "guardian news", "the guardian", "the new york times", "washington post",
            "the wall street journal", "vice news", "forbes breaking news",
            "the young turks", "breaking points", "meidastouch", "brian tyler cohen",
            "the daily wire", "ben shapiro", "tucker carlson", "tucker carlson network",
            "piers morgan uncensored", "steven crowder", "louder with crowder",
            "tim pool", "timcast", "timcast irl", "philip defranco", "tldr news",
            "the hill", "politico", "axios", "the view", "real time with bill maher",
            "last week tonight", "the daily show", "jimmy kimmel live",
            "the late show with stephen colbert", "late night with seth meyers",
            "candace owens", "matt walsh", "charlie kirk", "turning point usa",
            "prageru", "the rubin report", "hasanabi", "eri-tv", "eritv"
    );

    /** Words and names: checked in titles, the line under the title and channel names. */
    private static final Pattern WORD_PATTERN = buildPattern(
            NEWS_WORDS, POLITICS_WORDS, POLITICIANS, COMMENTATORS, ADULT_WORDS);
    /** Channels: checked only in channel names, so a title like "the view from the top" is fine. */
    private static final Pattern CHANNEL_PATTERN = buildPattern(CHANNELS);

    private ContentFilter() {
    }

    /**
     * True when a video should be hidden.
     *
     * @param title    video title
     * @param subtitle line under the title (usually channel name and views), may be null
     * @param author   channel name, may be null
     * @param isShorts whether the video is a YouTube Short
     */
    public static boolean isBlocked(CharSequence title, CharSequence subtitle, CharSequence author, boolean isShorts) {
        if (BLOCK_SHORTS && isShorts) {
            return true;
        }

        return isBlockedText(title) || isBlockedText(subtitle) || isBlockedText(author)
                || isBlockedChannel(author) || isBlockedChannel(subtitle);
    }

    /** True when the text contains a blocked word, name or channel. */
    public static boolean isBlockedText(CharSequence text) {
        if (text == null || text.length() == 0) {
            return false;
        }

        return WORD_PATTERN.matcher(normalize(text)).find();
    }

    /** True when the channel name is one of the blocked news / political channels. */
    public static boolean isBlockedChannel(CharSequence author) {
        if (author == null || author.length() == 0) {
            return false;
        }

        return CHANNEL_PATTERN.matcher(normalize(author)).find();
    }

    /** True when a search should not run. */
    public static boolean isBlockedSearch(String query) {
        return isBlockedText(query) || isBlockedChannel(query);
    }

    /** Removes blocked suggestions from a list of search suggestions. */
    public static List<String> filterTexts(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            return texts;
        }

        List<String> result = new ArrayList<>();

        for (String text : texts) {
            if (!isBlockedSearch(text)) {
                result.add(text);
            }
        }

        return result;
    }

    private static String normalize(CharSequence text) {
        // Treat "#news", "news_today" and "news-today" like separate words
        return text.toString()
                .toLowerCase(Locale.ROOT)
                .replace('_', ' ')
                .replace('’', '\'');
    }

    @SafeVarargs
    private static Pattern buildPattern(List<String>... lists) {
        List<String> terms = new ArrayList<>();

        for (List<String> list : lists) {
            for (String term : list) {
                terms.add(term.toLowerCase(Locale.ROOT));
            }
        }

        // Longest first, so "breaking news" is tried before "breaking"
        Collections.sort(terms, (a, b) -> b.length() - a.length());

        StringBuilder alternatives = new StringBuilder();

        for (String term : terms) {
            if (alternatives.length() > 0) {
                alternatives.append('|');
            }
            // Spaces in a phrase may be one or more spaces, dashes or dots
            String[] parts = term.split(" ");
            for (int i = 0; i < parts.length; i++) {
                if (i > 0) {
                    alternatives.append("[\\s\\-.]+");
                }
                alternatives.append(Pattern.quote(parts[i]));
            }
        }

        // Whole-word match: no letter or digit right before or right after
        return Pattern.compile(
                "(?<![\\p{L}\\p{N}])(?:" + alternatives + ")(?![\\p{L}\\p{N}])",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }
}
