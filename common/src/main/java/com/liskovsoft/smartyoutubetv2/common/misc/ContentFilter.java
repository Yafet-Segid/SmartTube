package com.liskovsoft.smartyoutubetv2.common.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
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
    /** A channel with even one video about a politician gets blocked permanently. */
    public static final boolean BLOCK_CHANNELS_THAT_MENTION_POLITICIANS = true;
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
            "morning news", "nightly news", "world news", "local news", "canada news", "canadian news",
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
            "isaias afwerki", "afwerki", "abiy ahmed",
            // Canada
            "mark carney", "carney", "pierre poilievre", "poilievre", "justin trudeau", "trudeau",
            "jagmeet singh", "doug ford", "danielle smith", "chrystia freeland",
            // Texas
            "greg abbott", "governor abbott", "gov abbott", "gov. abbott", "ken paxton", "john cornyn", "cornyn",
            "lt gov dan patrick", "beto", "beto o'rourke", "colin allred", "jasmine crockett",
            "james talarico"
    );

    // ---------------------------------------------------------------------
    // 3b. Political topics (hide the video, but don't block the whole channel)
    // ---------------------------------------------------------------------
    private static final List<String> POLITICAL_TOPICS = Arrays.asList(
            "parliament hill",
            "texas politics", "texas election", "texas elections", "texas legislature",
            "texas senate", "texas house", "texas runoff", "texas primary", "redistricting",
            "midterm elections", "midterm election", "2026 midterms", "election day",
            "nato", "nato summit", "otan",
            "usmca", "cusma", "nafta", "t-mec", "trade war"
    );

    // ---------------------------------------------------------------------
    // 4. Political commentators and TV hosts (also blocked when named in a title)
    // ---------------------------------------------------------------------
    private static final List<String> COMMENTATORS = Arrays.asList(
            "ben shapiro", "tucker carlson", "candace owens", "matt walsh", "charlie kirk",
            "steven crowder", "tim pool", "piers morgan", "bill maher", "hasan piker",
            "brian tyler cohen", "rachel maddow", "sean hannity", "laura ingraham",
            "jesse watters", "megyn kelly", "alex jones", "joy reid", "don lemon",
            "anderson cooper", "jake tapper", "dave rubin", "glenn beck", "jon stewart",
            // Your own additions
            "veronica weffer", "verónica weffer", "veronicaweffer",
            "nathaniel drew", "nathanieldrew", "afroguajira", "afro guajira", "afro-guajira",
            "claus kellerman", "clauskellerman", "tyler bucket", "tylerbucket", "jamin laine", "jaminlaine",
            "rebel group", "rebel groups",
            // GTA (all games, including GTA 6)
            "gta", "gta6", "gtavi", "gta vi", "gta 6", "gta5", "gtav", "gta v",
            "grand theft auto", "rockstar games",
            // Gambling and betting
            "gambling", "gamble", "casino", "casinos", "slot machine", "slot machines", "slots",
            "sports betting", "betting", "bet365", "draftkings", "fanduel", "parlay", "parlays",
            "poker", "blackjack", "roulette", "jackpot", "lottery", "scratch off", "scratch offs",
            "crypto casino", "get rich quick",
            // Celebrity drama and gossip
            "celebrity gossip", "hollywood gossip", "gossip", "influencer drama", "youtuber drama",
            "drama alert", "dramaalert", "spill the tea", "spilling the tea", "exposed", "scandal",
            "feud", "diss track", "tmz", "the shade room", "keemstar"
    );

    // ---------------------------------------------------------------------
    // 5. Sexual / suggestive content
    // ---------------------------------------------------------------------
    private static final List<String> ADULT_WORDS = Arrays.asList(
            "sexy", "sex", "sexual", "nsfw", "18+", "xxx", "porn", "nude", "nudes",
            "naked", "erotic", "seductive", "onlyfans", "only fans", "thirst trap",
            "bikini", "bikinis", "lingerie", "try on haul", "try-on haul",
            "twerk", "twerking", "stripper", "strip club", "hot girl", "hot girls",
            "hottest girls", "boobs", "adult content", "playboy",
            "adults only", "adult only", "adults-only", "adult-only", "18+ cruise",
            "clothing optional", "nude beach", "nudist", "naturist", "topless",
            "swimsuit", "swimsuits", "swimwear", "micro bikini", "thong",
            "bikini haul", "pornstar", "pornstars", "porn star", "porno", "adult film",
            "adult star", "xxx expo", "exxxotica", "avn awards"
    );

    // ---------------------------------------------------------------------
    // 6. News and political channels (matched against the channel name)
    // ---------------------------------------------------------------------
    private static final List<String> CHANNELS = Arrays.asList(
            "cnn", "fox news", "fox business", "drm", "drm news", "livenow from fox", "livenow", "live now from fox", "fox weather", "fox 4", "fox 26 houston", "fox 7 austin", "wfaa", "khou", "khou 11", "kvue", "kxan", "kprc", "ksat", "kdfw", "nbc dfw", "nbc 5 dallas-fort worth", "fox 4 dallas-fort worth", "cbs news texas", "texas tribune", "the texas tribune", "cbc", "cbc news", "ctv news", "ctv", "global news", "cpac", "the national", "rebel news", "the globe and mail", "toronto star", "national post", "msnbc", "ms now", "abc news", "cbs news",
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
            "prageru", "the rubin report", "hasanabi", "eri-tv", "eritv", "rockstar games"
    );

    /** Words and names: checked in titles, the line under the title and channel names. */
    // ---------------------------------------------------------------------
    // 7. Words that mark a channel as a news channel (checked only in channel names)
    // ---------------------------------------------------------------------
    private static final List<String> NEWS_CHANNEL_WORDS = Arrays.asList(
            "tribune", "gazette", "herald", "chronicle", "dispatch", "newsroom", "newshour",
            "newsnight", "newsline", "newsday", "newswire", "press tv", "the daily",
            "politics", "political", "report tv", "news tv", "tv news", "24/7 news"
    );

    private static final Pattern WORD_PATTERN = buildPattern(
            NEWS_WORDS, POLITICS_WORDS, POLITICIANS, POLITICAL_TOPICS, COMMENTATORS, ADULT_WORDS);
    /** Politicians only: a channel that talks about them gets blocked for good. */
    private static final Pattern POLITICIAN_PATTERN = buildPattern(POLITICIANS);
    /** Channels: checked only in channel names, so a title like "the view from the top" is fine. */
    private static final Pattern CHANNEL_PATTERN = buildPattern(CHANNELS, NEWS_CHANNEL_WORDS);
    /** Hashtags like #news or #BreakingNews in a description. */
    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#([\\p{L}\\p{N}_]+)");
    /** A description with this many different blocked words counts as news / politics. */
    private static final int DESCRIPTION_WORD_LIMIT = 3;

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

    /**
     * True when a video description looks like news or politics: a hashtag such as
     * #news, #politics or #trump, or several different blocked words in the text.
     * Only used when a video is opened (lists don't include descriptions).
     */
    public static boolean isBlockedDescription(CharSequence description) {
        if (description == null || description.length() == 0) {
            return false;
        }

        String text = normalize(description);

        // 1. Hashtags: "#BreakingNews" -> "breaking news" style checks on each tag
        Matcher tags = HASHTAG_PATTERN.matcher(text);
        while (tags.find()) {
            String tag = tags.group(1);
            if (isBlockedText(tag) || isBlockedChannel(tag) || isBlockedHashtag(tag)) {
                return true;
            }
        }

        // 2. Several different blocked words in the description
        Set<String> found = new HashSet<>();
        Matcher words = WORD_PATTERN.matcher(text);
        while (words.find()) {
            found.add(words.group());
            if (found.size() >= DESCRIPTION_WORD_LIMIT) {
                return true;
            }
        }

        return false;
    }

    /** Joined hashtags like "breakingnews" or "uspolitics" that contain a news word. */
    private static boolean isBlockedHashtag(String tag) {
        return (tag.contains("news") && !tag.contains("newsletter")) || tag.contains("politic") || tag.contains("election")
                || tag.contains("trump") || tag.contains("biden") || tag.contains("maga");
    }

    /**
     * True when the text names a politician. Used to block the whole channel
     * permanently, not just the one video.
     */
    public static boolean mentionsPolitician(CharSequence... texts) {
        if (!BLOCK_CHANNELS_THAT_MENTION_POLITICIANS) {
            return false;
        }

        for (CharSequence text : texts) {
            if (text != null && text.length() > 0 && POLITICIAN_PATTERN.matcher(normalize(text)).find()) {
                return true;
            }
        }

        return false;
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
