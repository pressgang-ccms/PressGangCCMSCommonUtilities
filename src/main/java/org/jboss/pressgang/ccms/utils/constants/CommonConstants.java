package org.jboss.pressgang.ccms.utils.constants;

/**
 * This class defines the constants that are used between PressGang and its various
 * components
 */
public class CommonConstants {
    public static final int CS_BOOK = 0;
    public static final int CS_ARTICLE = 1;
    public static final int CS_BOOK_DRAFT = 2;
    public static final int CS_ARTICLE_DRAFT = 3;

    public static final int CS_NODE_TOPIC = 0;
    public static final int CS_NODE_SECTION = 1;
    public static final int CS_NODE_CHAPTER = 2;
    public static final int CS_NODE_APPENDIX = 3;
    public static final int CS_NODE_PART = 4;
    public static final int CS_NODE_PROCESS = 5;
    public static final int CS_NODE_COMMENT = 6;
    public static final int CS_NODE_META_DATA = 7;
    public static final int CS_NODE_PREFACE = 8;
    public static final int CS_NODE_INNER_TOPIC = 9;
    public static final int CS_NODE_META_DATA_TOPIC = 10;
    public static final int CS_NODE_FILE = 11;

    public static final int CS_RELATIONSHIP_PREREQUISITE = 0;
    public static final int CS_RELATIONSHIP_REFER_TO = 1;
    public static final int CS_RELATIONSHIP_LINK_LIST = 2;
    public static final int CS_RELATIONSHIP_NEXT = 3;
    public static final int CS_RELATIONSHIP_PREVIOUS = 4;

    public static final int CS_RELATIONSHIP_MODE_ID = 0;
    public static final int CS_RELATIONSHIP_MODE_TARGET = 1;

    public static final int FILTER_TOPIC = 0;
    public static final int FILTER_CONTENT_SPEC = 1;

    public static final int DOCBOOK_45 = 0;
    public static final int DOCBOOK_50 = 1;

    /**
     * The Regular Expression the defines the search format for a property tag
     */
    public static String PROPERTY_TAG_SEARCH_RE = "propertyTag(?<PropertyTagID>\\d+)";
    /**
     * The regular expression that matches the Build ID field, without the prefixed topic id
     */
    public static String BUGZILLA_BUILD_ID_RE = "-[0-9]+ [0-9]{2} [A-Za-z]{3} [0-9]{4} [0-9]{2}:[0-9]{2}( .{2}_.{2})?( \\[\\w+\\])?";
    /**
     * The regular expression that matches the Build ID field, with the individual fields grouped and named
     */
    public static String BUGZILLA_BUILD_ID_NAMED_RE = "(?<TopicID>\\d+)-(?<TopicRevision>\\d+) (?<TopicRevisionDay>\\d{2}) " +
            "(?<TopicRevisionMonth>\\w{3}) (?<TopicRevisionYear>\\d{4}) (?<TopicRevisionHour>\\d{2}):(?<TopicRevisionMinute>\\d{2})\\s*" +
            "(?<TopicLocale>.{2}_.{2})?\\s*(?<CSType>\\[\\w+\\])?";

    /**
     * The bugzilla url
     */
    public static final String BUGZILLA_URL_PROPERTY = "pressgang.bugzillaUrl";
    /**
     * The bugzilla username
     */
    public static final String BUGZILLA_USERNAME_PROPERTY = "pressgang.bugzillaUsername";
    /**
     * The bugzilla password
     */
    public static final String BUGZILLA_PASSWORD_PROPERTY = "pressgang.bugzillaPassword";
    /**
     * The bugzilla minimum time between calls property name
     */
    public static final String BUGZILLA_MIN_API_CALL_INTERVAL_PROPERTY = "topicIndex.bugzillaMinCallInterval";
    /**
     * The format of the date to be displayed by any date widget
     */
    public static final String FILTER_DISPLAY_DATE_FORMAT = "dd MMM yyyy HH:mm";
    /**
     * The ISO8601 date format, used for SQL queries
     */
    public static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    /**
     * The Verbatim property key for the XML Elements StringConstant properties file.
     */
    public static final String VERBATIM_XML_ELEMENTS_PROPERTY_KEY = "VERBATIM_XML_ELEMENTS";
    /**
     * The Inline property key for the XML Elements StringConstant properties file.
     */
    public static final String INLINE_XML_ELEMENTS_PROPERTY_KEY = "INLINE_XML_ELEMENTS";
    /**
     * The Contents Inline property key for the XML Elements StringConstant properties file.
     */
    public static final String CONTENTS_INLINE_XML_ELEMENTS_PROPERTY_KEY = "CONTENTS_INLINE_XML_ELEMENTS";
    /**
     * The system property that defines the STOMP message server
     */
    public static final String STOMP_MESSAGE_SERVER_SYSTEM_PROPERTY = "pressgang.stompMessageServer";
    /**
     * The system property that defines the STOMP message server port
     */
    public static final String STOMP_MESSAGE_SERVER_PORT_SYSTEM_PROPERTY = "pressgang.stompMessageServerPort";
    /**
     * The system property that defines the STOMP message server username
     */
    public static final String STOMP_MESSAGE_SERVER_USER_SYSTEM_PROPERTY = "pressgang.stompMessageServerUser";
    /**
     * The system property that defines the STOMP message server password
     */
    public static final String STOMP_MESSAGE_SERVER_PASS_SYSTEM_PROPERTY = "pressgang.stompMessageServerPass";
    /**
     * The system property that defines the STOMP message queue that a service
     * should listen to
     */
    public static final String STOMP_MESSAGE_SERVER_QUEUE_SYSTEM_PROPERTY = "pressgang.stompMessageServerQueue";
    /**
     * The system property that defines the PressGang REST Server
     */
    public static final String PRESS_GANG_REST_SERVER_SYSTEM_PROPERTY = "pressgang.restServer";
    /**
     * The system property that defines the PressGang REST Server
     */
    public static final String PRESS_GANG_UI_SYSTEM_PROPERTY = "pressgang.ui";

    /**
     * The ZIP file MIME type
     */
    public static final String ZIP_MIME_TYPE = "application/zip";

    /**
     * The encoding of the XML, used when converting a DOM object to a string
     */
    public static final String XML_ENCODING = "UTF-8";

    /**
     * The regular expression string used to check if an email is valid.
     */
    public static final String EMAIL_REGEX = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1," +
            "" + "3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
    /**
     * A prefix for error xref ids
     */
    public static final String ERROR_XREF_ID_PREFIX = "TagErrorXRef";

    // Content Spec Meta Data Constants
    public static final String CS_CHECKSUM_TITLE = "CHECKSUM";
    public static final String CS_ID_TITLE = "ID";
    public static final String CS_TITLE_TITLE = "Title";
    public static final String CS_SUBTITLE_TITLE = "Subtitle";
    public static final String CS_EDITION_TITLE = "Edition";
    public static final String CS_BOOK_VERSION_TITLE = "Book Version";
    public static final String CS_PUBSNUMBER_TITLE = "Pubsnumber";
    public static final String CS_PRODUCT_TITLE = "Product";
    public static final String CS_ABSTRACT_TITLE = "Abstract";
    public static final String CS_ABSTRACT_ALTERNATE_TITLE = "Description";
    public static final String CS_COPYRIGHT_HOLDER_TITLE = "Copyright Holder";
    public static final String CS_COPYRIGHT_YEAR_TITLE = "Copyright Year";
    public static final String CS_VERSION_TITLE = "Version";
    public static final String CS_BRAND_TITLE = "Brand";
    public static final String CS_BUG_LINKS_TITLE = "Bug Links";
    public static final String CS_BUGZILLA_PRODUCT_TITLE = "BZProduct";
    public static final String CS_BUGZILLA_COMPONENT_TITLE = "BZComponent";
    public static final String CS_BUGZILLA_VERSION_TITLE = "BZVersion";
    public static final String CS_BUGZILLA_KEYWORDS_TITLE = "BZKeywords";
    public static final String CS_BUGZILLA_SERVER_TITLE = "BZServer";
    public static final String CS_BUGZILLA_URL_TITLE = "BZURL";
    public static final String CS_BOOK_TYPE_TITLE = "Type";
    public static final String CS_BRAND_LOGO_TITLE = "Brand Logo";
    public static final String CS_PUBLICAN_CFG_TITLE = "publican.cfg";
    public static final String CS_INLINE_INJECTION_TITLE = "Inline Injection";
    public static final String CS_DTD_TITLE = "DTD";
    public static final String CS_REV_HISTORY_TITLE = "Revision History";
    public static final String CS_FEEDBACK_TITLE = "Feedback";
    public static final String CS_LEGAL_NOTICE_TITLE = "Legal Notice";
    public static final String CS_AUTHOR_GROUP_TITLE = "Author Group";
    public static final String CS_BUGZILLA_ASSIGNEE_TITLE = "BZ Assignee";
    public static final String CS_MAVEN_GROUP_ID_TITLE = "GroupId";
    public static final String CS_MAVEN_ARTIFACT_ID_TITLE = "ArtifactId";
    public static final String CS_MAVEN_POM_VERSION_TITLE = "POM Version";
    public static final String CS_FILE_TITLE = "Additional Files";
    public static final String CS_FILE_SHORT_TITLE = "Files";
    public static final String CS_JIRA_PROJECT_TITLE = "JIRAProject";
    public static final String CS_JIRA_COMPONENT_TITLE = "JIRAComponent";
    public static final String CS_JIRA_VERSION_TITLE = "JIRAVersion";
    public static final String CS_JIRA_LABELS_TITLE = "JIRALabels";
    public static final String CS_JIRA_SERVER_TITLE = "JIRAServer";
    public static final String CS_ENTITIES_TITLE = "Entities";
    public static final String CS_SPACES_TITLE = "spaces";
    public static final String CS_DEFAULT_PUBLICAN_CFG_TITLE = "Default " + CS_PUBLICAN_CFG_TITLE;
    public static final String CS_INDEX_TITLE = "Index";

    /**
     * The number of characters that make up a SHA 256 hash
     */
    public static final int SHA256_LENGTH = 64;

    /**
     * The name of the SHA 256 algorithm
     */
    public static final String SHA256_NAME = "SHA-256";
}
