package org.jboss.pressgang.ccms.utils.constants;

public class CommonFilterConstants {
    /**
     * The Filter logic keyword to use when two conditions need to be and'ed
     */
    public static final String AND_LOGIC = "And";
    /**
     * The Filter logic keyword to use when two conditions need to be or'ed
     */
    public static final String OR_LOGIC = "Or";
    /**
     * The url variable prefix to indicate that a tag needs to be matched
     */
    public static final String MATCH_TAG = "tag";
    /**
     * The url variable prefix to indicate that a tag will be used for grouping
     */
    public static final String GROUP_TAG = "grouptab";
    /**
     * The url variable prefix to indicate that a locale needs to be matched
     */
    public static final String MATCH_LOCALE = "locale";
    /**
     * The url variable prefix to indicate that a locale will be used for grouping
     */
    public static final String GROUP_LOCALE = "grouplocale";
    /**
     * The URL variable prefix to indicate the internal logic of a category (and
     * optionally also specify a project)
     */
    public static final String CATEORY_INTERNAL_LOGIC = "catint";
    /**
     * The URL variable prefix to indicate the external logic of a category (and
     * optionally also specify a project)
     */
    public static final String CATEORY_EXTERNAL_LOGIC = "catext";
    /**
     * The URL variable the indicates the filter to be used
     */
    public static final String FILTER_ID = "filterId";
    /**
     * The value (as used in the FilterTag database TagState field) the
     * indicates that a tag should be matched
     */
    public static final int MATCH_TAG_STATE = 1;
    /**
     * The value (as used in the FilterTag database TagState field) the
     * indicates that a tag should be excluded
     */
    public static final int NOT_MATCH_TAG_STATE = 0;
    /**
     * The value (as used in the FilterTag database TagState field) the
     * indicates that a tag should be excluded
     */
    public static final int GROUP_TAG_STATE = 2;

    /**
     * The value (as used in the FilterLocale database LocaleState field) the
     * indicates that a tag should be matched
     */
    public static final int MATCH_LOCALE_STATE = 1;
    /**
     * The value (as used in the FilterLocale database LocaleState field) the
     * indicates that a locale should be included
     */
    public static final int NOT_MATCH_LOCALE_STATE = 0;
    /**
     * The value (as used in the FilterLocale database LocaleState field) the
     * indicates that a locale should be grouped
     */
    public static final int GROUP_LOCALE_STATE = 2;

    /**
     * The value (as used in the FilterCategory database CategoryState field)
     * the indicates that a category has an internal "and" state
     */
    public static final int CATEGORY_INTERNAL_AND_STATE = 0;
    /**
     * The value (as used in the FilterCategory database CategoryState field)
     * the indicates that a category has an internal "or" state
     */
    public static final int CATEGORY_INTERNAL_OR_STATE = 1;
    /**
     * The value (as used in the FilterCategory database CategoryState field)
     * the indicates that a category has an external "and" state
     */
    public static final int CATEGORY_EXTERNAL_AND_STATE = 2;
    /**
     * The value (as used in the FilterCategory database CategoryState field)
     * the indicates that a category has an external "or" state
     */
    public static final int CATEGORY_EXTERNAL_OR_STATE = 3;

    public static final String STRING_MATCHES_SUFFIX = "-matches";
    public static final String STRING_CONTAINS_SUFFIX = "-contains";

    /**
     * The URL variable that defines the topic text search field
     */
    public static final String TOPIC_TEXT_SEARCH_FILTER_VAR = "topicTextSearch";
    /**
     * The description of the topic text search field
     */
    public static final String TOPIC_TEXT_SEARCH_FILTER_VAR_DESC = "Topic Text Search";
    /**
     * The URL variable that defines the topic IDs search field
     */
    public static final String TOPIC_IDS_FILTER_VAR = "topicIds";
    /**
     * The description of the topic IDs search field
     */
    public static final String TOPIC_IDS_FILTER_VAR_DESC = "Topic IDs";
    /**
     * The URL variable that defines the not topic IDs search field
     */
    public static final String TOPIC_IDS_NOT_FILTER_VAR = "topicNotIds";
    /**
     * The description of the not topic IDs search field
     */
    public static final String TOPIC_IDS_NOT_FILTER_VAR_DESC = "Not Topic IDs";
    /**
     * The URL variable that defines the topic title search field
     */
    public static final String TOPIC_TITLE_FILTER_VAR = "topicTitle";
    public static final String TOPIC_TITLE_MATCHES_FILTER_VAR = TOPIC_TITLE_FILTER_VAR + STRING_MATCHES_SUFFIX;
    /**
     * The description of the topic title search field
     */
    public static final String TOPIC_TITLE_FILTER_VAR_DESC = "Title";
    /**
     * The URL variable that defines the not topic title search field
     */
    public static final String TOPIC_TITLE_NOT_FILTER_VAR = "topicNotTitle";
    public static final String TOPIC_TITLE_NOT_MATCHES_FILTER_VAR = TOPIC_TITLE_NOT_FILTER_VAR + STRING_MATCHES_SUFFIX;
    /**
     * The description of the not topic title search field
     */
    public static final String TOPIC_TITLE_NOT_FILTER_VAR_DESC = "Not Title";
    /**
     * The URL variable that defines the topic description search field
     */
    public static final String TOPIC_DESCRIPTION_FILTER_VAR = "topicText";
    public static final String TOPIC_DESCRIPTION_MATCHES_FILTER_VAR = TOPIC_DESCRIPTION_FILTER_VAR + STRING_MATCHES_SUFFIX;
    /**
     * The description of the topic description search field
     */
    public static final String TOPIC_DESCRIPTION_FILTER_VAR_DESC = "Description";
    /**
     * The URL variable that defines the not topic description search field
     */
    public static final String TOPIC_DESCRIPTION_NOT_FILTER_VAR = "topicNotText";
    public static final String TOPIC_DESCRIPTION_NOT_MATCHES_FILTER_VAR = TOPIC_DESCRIPTION_NOT_FILTER_VAR + STRING_MATCHES_SUFFIX;
    /**
     * The description of the not topic description search field
     */
    public static final String TOPIC_DESCRIPTION_NOT_FILTER_VAR_DESC = "Not Description";
    /**
     * The URL variable that defines the topic xml search field
     */
    public static final String TOPIC_XML_FILTER_VAR = "topicXml";
    public static final String TOPIC_XML_MATCHES_FILTER_VAR = TOPIC_XML_FILTER_VAR + STRING_MATCHES_SUFFIX;
    /**
     * The description of the topic xml search field
     */
    public static final String TOPIC_XML_FILTER_VAR_DESC = "XML";
    /**
     * The URL variable that defines the not topic xml search field
     */
    public static final String TOPIC_XML_NOT_FILTER_VAR = "topicNotXml";
    public static final String TOPIC_XML_NOT_MATCHES_FILTER_VAR = TOPIC_XML_NOT_FILTER_VAR + STRING_MATCHES_SUFFIX;
    /**
     * The description of the not topic xml search field
     */
    public static final String TOPIC_XML_NOT_FILTER_VAR_DESC = "Not XML";
    /**
     * The URL variable that defines the start range for the topic create date
     * search field
     */
    public static final String STARTDATE_FILTER_VAR = "startDate";
    /**
     * The description of the start range for the topic create date search field
     */
    public static final String STARTDATE_FILTER_VAR_DESC = "Min Creation Date";
    /**
     * The URL variable that defines the end range for the topic create date
     * search field
     */
    public static final String ENDDATE_FILTER_VAR = "endDate";
    /**
     * The description of the end range for the topic create date search field
     */
    public static final String ENDDATE_FILTER_VAR_DESC = "Max Creation Date";
    /**
     * The URL variable that defines the start edit range for the topic create
     * date search field
     */
    public static final String STARTEDITDATE_FILTER_VAR = "startEditDate";
    /**
     * The description of the start edit range for the topic create date search
     * field
     */
    public static final String STARTEDITDATE_FILTER_VAR_DESC = "Min Edited Date";
    /**
     * The URL variable that defines the end edit range for the topic create
     * date search field
     */
    public static final String ENDEDITDATE_FILTER_VAR = "endEditDate";
    /**
     * The description of the end edit range for the topic create date search
     * field
     */
    public static final String ENDEDITDATE_FILTER_VAR_DESC = "Max Edited Date";
    /**
     * The URL variable that defines the logic to be applied to the search
     * fields
     */
    public static final String LOGIC_FILTER_VAR = "logic";
    /**
     * The description the logic to be applied to the search fields
     */
    public static final String LOGIC_FILTER_VAR_DESC = "Field Logic";

    /**
     * The URL variable that defines the has related from search field
     */
    public static final String EDITED_IN_LAST_DAYS = "editedInLastDays";
    /**
     * The description of the has related from search field
     */
    public static final String EDITED_IN_LAST_DAYS_DESC = "Edited In Last Days";
    /**
     * The URL variable that defines the has related from search field
     */
    public static final String NOT_EDITED_IN_LAST_DAYS = "notEditedInLastDays";
    /**
     * The description of the has related from search field
     */
    public static final String NOT_EDITED_IN_LAST_DAYS_DESC = "Not Edited In Last Days";
    public static final String EDITED_IN_LAST_MINUTES = "editedInLastMins";
    public static final String EDITED_IN_LAST_MINUTES_DESC = "Edited In Last Minutes";
    public static final String NOT_EDITED_IN_LAST_MINUTES = "notEditedInLastMins";
    public static final String NOT_EDITED_IN_LAST_MINUTES_DESC = "Not Edited In Last Minutes";
    /**
     * The URL variable that defines the has relationships search field
     */
    public static final String TOPIC_HAS_OPEN_BUGZILLA_BUGS = "topicHasOpenBugzillaBugs";
    /**
     * The description of the has relationships search field
     */
    public static final String TOPIC_HAS_OPEN_BUGZILLA_BUGS_DESC = "Has Open Bugzilla Bugs";
    /**
     * The URL variable that defines the has relationships search field
     */
    public static final String TOPIC_HAS_NOT_OPEN_BUGZILLA_BUGS = "topicHasNotOpenBugzillaBugs";
    /**
     * The description of the has relationships search field
     */
    public static final String TOPIC_HAS_NOT_OPEN_BUGZILLA_BUGS_DESC = "Doesn't have Open Bugzilla Bugs";
    /**
     * The URL variable that defines the has relationships search field
     */
    public static final String TOPIC_HAS_BUGZILLA_BUGS = "topicHasBugzillaBugs";
    /**
     * The description of the has relationships search field
     */
    public static final String TOPIC_HAS_BUGZILLA_BUGS_DESC = "Has Bugzilla Bugs";
    /**
     * The URL variable that defines the has relationships search field
     */
    public static final String TOPIC_HAS_NOT_BUGZILLA_BUGS = "topicHasNotBugzillaBugs";
    /**
     * The description of the has relationships search field
     */
    public static final String TOPIC_HAS_NOT_BUGZILLA_BUGS_DESC = "Doesn't have Bugzilla Bugs";
    /**
     * The URL variable that defines the has relationships search field
     */
    public static final String TOPIC_HAS_RELATIONSHIPS = "topicHasRelationships";
    /**
     * The description of the has relationships search field
     */
    public static final String TOPIC_HAS_RELATIONSHIPS_DESC = "Has Relationships";
    /**
     * The URL variable that defines the has relationships search field
     */
    public static final String TOPIC_HAS_NOT_RELATIONSHIPS = "topicHasNotRelationships";
    /**
     * The description of the has relationships search field
     */
    public static final String TOPIC_HAS_NOT_RELATIONSHIPS_DESC = "Doesn't have Relationships";
    /**
     * The URL variable that defines the has incoming relationships search field
     */
    public static final String TOPIC_HAS_INCOMING_RELATIONSHIPS = "topicHasIncomingRelationships";
    /**
     * The description of the has incoming relationships search field
     */
    public static final String TOPIC_HAS_INCOMING_RELATIONSHIPS_DESC = "Has Incoming Relationships";
    /**
     * The URL variable that defines the has incoming relationships search field
     */
    public static final String TOPIC_HAS_NOT_INCOMING_RELATIONSHIPS = "topicHasNotIncomingRelationships";
    /**
     * The description of the has incoming relationships search field
     */
    public static final String TOPIC_HAS_NOT_INCOMING_RELATIONSHIPS_DESC = "Doesn't Have Incoming Relationships";
    /**
     * The URL variable that defines the has related to search field
     */
    public static final String TOPIC_RELATED_TO = "topicRelatedTo";
    /**
     * The description of the has related to search field
     */
    public static final String TOPIC_RELATED_TO_DESC = "Related To";
    /**
     * The URL variable that defines the has related from search field
     */
    public static final String TOPIC_RELATED_FROM = "topicRelatedFrom";
    /**
     * The description of the has related from search field
     */
    public static final String TOPIC_RELATED_FROM_DESC = "Related From";
    /**
     * The URL variable that defines the has related to search field
     */
    public static final String TOPIC_NOT_RELATED_TO = "topicNotRelatedTo";
    /**
     * The description of the has related to search field
     */
    public static final String TOPIC_NOT_RELATED_TO_DESC = "Not Related To";
    /**
     * The URL variable that defines the has related from search field
     */
    public static final String TOPIC_NOT_RELATED_FROM = "topicNotRelatedFrom";
    /**
     * The description of the has related from search field
     */
    public static final String TOPIC_NOT_RELATED_FROM_DESC = "NotRelated From";
    /**
     * The URL variable that defines the has related from search field
     */
    public static final String TOPIC_HAS_XML_ERRORS = "topicHasXMLErrors";
    /**
     * The description of the has related from search field
     */
    public static final String TOPIC_HAS_XML_ERRORS_DESC = "Topic Has XML Errors";
    /**
     * The URL variable that defines the has related from search field
     */
    public static final String TOPIC_HAS_NOT_XML_ERRORS = "topicHasNotXMLErrors";
    /**
     * The description of the has related from search field
     */
    public static final String TOPIC_HAS_NOT_XML_ERRORS_DESC = "Topic doesn't have XML Errors";
    /**
     * The URL variable that defines the has related from search field
     */
    public static final String TOPIC_EDITED_IN_LAST_DAYS = "topicEditedInLastDays";
    /**
     * The description of the has related from search field
     */
    public static final String TOPIC_EDITED_IN_LAST_DAYS_DESC = "Topic Edited In Last Days";
    /**
     * The URL variable that defines the has related from search field
     */
    public static final String TOPIC_NOT_EDITED_IN_LAST_DAYS = "topicNotEditedInLastDays";
    /**
     * The description of the has related from search field
     */
    public static final String TOPIC_NOT_EDITED_IN_LAST_DAYS_DESC = "Topic Not Edited In Last Days";
    public static final String TOPIC_EDITED_IN_LAST_MINUTES = "topicEditedInLastMins";
    public static final String TOPIC_EDITED_IN_LAST_MINUTES_DESC = "Topic Edited In Last Minutes";
    public static final String TOPIC_NOT_EDITED_IN_LAST_MINUTES = "topicNotEditedInLastMins";
    public static final String TOPIC_NOT_EDITED_IN_LAST_MINUTES_DESC = "Topic Not Edited In Last Minutes";
    /**
     * The URL variable that defines the topic property tag
     */
    public static final String PROPERTY_TAG = "propertyTag";
    /**
     * The description of the property tag search field
     */
    public static final String PROPERTY_TAG_DESC = "Property Tag";
    /**
     * The URL variable that defines the topic property tag
     */
    public static final String PROPERTY_TAG_EXISTS = "propertyTagExists";
    /**
     * The description of the property tag exists search field
     */
    public static final String PROPERTY_TAG_EXISTS_DESC = "Property Tag Exists";
    /**
     * The URL variable that defines the topic property tag
     */
    public static final String PROPERTY_TAG_NOT_EXISTS = "propertyTagNotExists";
    /**
     * The description of the property tag exists search field
     */
    public static final String PROPERTY_TAG_NOT_EXISTS_DESC = "Property Tag Not Exists";
    /**
     * The URL variable that defines the has relationships search field
     */
    public static final String TOPIC_IS_INCLUDED_IN_SPEC = "topicIncludedInSpec";
    /**
     * The description of the has relationships search field
     */
    public static final String TOPIC_IS_INCLUDED_IN_SPEC_DESC = "Topics Included In Spec";
    /**
     * The URL variable that defines the has relationships search field
     */
    public static final String TOPIC_IS_NOT_INCLUDED_IN_SPEC = "topicNotIncludedInSpec";
    /**
     * The description of the has relationships search field
     */
    public static final String TOPIC_IS_NOT_INCLUDED_IN_SPEC_DESC = "Topics Not Included In Spec";

    /**
     * The URL variable that defines if translated topics should be filter to only include the latest copy
     */
    public static final String TOPIC_LATEST_TRANSLATIONS_FILTER_VAR = "latestTranslations";
    /**
     * The description of the latest translated topic search field
     */
    public static final String TOPIC_LATEST_TRANSLATIONS_FILTER_VAR_DESC = "Latest Translations";
    /**
     * The URL variable that defines if translated topics should be filter to only include the latest finished copy
     */
    public static final String TOPIC_LATEST_COMPLETED_TRANSLATIONS_FILTER_VAR = "latestCompletedTranslations";
    /**
     * The description of the latest completed translated topic search field
     */
    public static final String TOPIC_LATEST_COMPLETED_TRANSLATIONS_FILTER_VAR_DESC = "Latest Completed Translations";
    /**
     * The URL variable that defines if translated topics should be filter to only include the latest copy
     */
    public static final String TOPIC_NOT_LATEST_TRANSLATIONS_FILTER_VAR = "notLatestTranslations";
    /**
     * The description of the latest translated topic search field
     */
    public static final String TOPIC_NOT_LATEST_TRANSLATIONS_FILTER_VAR_DESC = "Not Latest Translations";
    /**
     * The URL variable that defines if translated topics should be filter to only include the latest finished copy
     */
    public static final String TOPIC_NOT_LATEST_COMPLETED_TRANSLATIONS_FILTER_VAR = "notLatestCompletedTranslations";
    /**
     * The description of the latest completed translated topic search field
     */
    public static final String TOPIC_NOT_LATEST_COMPLETED_TRANSLATIONS_FILTER_VAR_DESC = "Not Latest Completed Translations";
    /**
     * The URL variable that defines the topics min hash
     */
    public static final String TOPIC_MIN_HASH_VAR = "minHash";
    /**
     * The description of the topic min hash search field
     */
    public static final String TOPIC_MIN_HASH_VAR_DESC = "Topic Minimum Hash";
    /**
     * The URL variable that defines the created by search field
     */
    public static final String CREATED_BY_VAR = "createdBy";
    /**
     * The description of the created by search field
     */
    public static final String CREATED_BY_VAR_DESC = "Created By";
    /**
     * The URL variable that defines the created by search field
     */
    public static final String NOT_CREATED_BY_VAR = "notCreatedBy";
    /**
     * The description of the created by search field
     */
    public static final String NOT_CREATED_BY_VAR_DESC = "Not Created By";
    /**
     * The URL variable that defines the edited by search field
     */
    public static final String EDITED_BY_VAR = "editedBy";
    /**
     * The description of the edited by search field
     */
    public static final String EDITED_BY_VAR_DESC = "Edited By";

    /**
     * The URL variable that defines the edited by search field
     */
    public static final String NOT_EDITED_BY_VAR = "notEditedBy";
    /**
     * The description of the edited by search field
     */
    public static final String NOT_EDITED_BY_VAR_DESC = "Not Edited By";

    /**
     * The URL variable that defines the topic IDs search field
     */
    public static final String ZANATA_IDS_FILTER_VAR = "zanataIds";
    /**
     * The description of the topic IDs search field
     */
    public static final String ZANATA_IDS_FILTER_VAR_DESC = "Zanata IDs";
    /**
     * The URL variable that defines the not topic IDs search field
     */
    public static final String ZANATA_IDS_NOT_FILTER_VAR = "zanataNotIds";
    /**
     * The description of the not topic IDs search field
     */
    public static final String ZANATA_IDS_NOT_FILTER_VAR_DESC = "Not Zanata IDs";

    /* Tag Filter Constants */
    public static final String TAG_IDS_FILTER_VAR = "tagIds";
    public static final String TAG_IDS_FILTER_VAR_DESC = "Tag IDs";

    public static final String TAG_NAME_FILTER_VAR = "tagName";
    public static final String TAG_NAME_MATCHES_FILTER_VAR = TAG_NAME_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String TAG_NAME_FILTER_VAR_DESC = "Tag Name";

    public static final String TAG_DESCRIPTION_FILTER_VAR = "tagDesc";
    public static final String TAG_DESCRIPTION_MATCHES_FILTER_VAR = TAG_DESCRIPTION_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String TAG_DESCRIPTION_FILTER_VAR_DESC = "Tag Description";

    /* Category Filter Constants */
    public static final String CATEGORY_IDS_FILTER_VAR = "catIds";
    public static final String CATEGORY_IDS_FILTER_VAR_DESC = "Category IDs";

    public static final String CATEGORY_NAME_FILTER_VAR = "catName";
    public static final String CATEGORY_NAME_MATCHES_FILTER_VAR = CATEGORY_NAME_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String CATEGORY_NAME_FILTER_VAR_DESC = "Category Name";

    public static final String CATEGORY_DESCRIPTION_FILTER_VAR = "catDesc";
    public static final String CATEGORY_DESCRIPTION_MATCHES_FILTER_VAR = CATEGORY_DESCRIPTION_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String CATEGORY_DESCRIPTION_FILTER_VAR_DESC = "Category Description";

    public static final String CATEGORY_IS_MUTUALLY_EXCLUSIVE_VAR = "isMutuallyExclusive";
    public static final String CATEGORY_IS_MUTUALLY_EXCLUSIVE_VAR_DESC = "Is Mutually Exclusive";
    public static final String CATEGORY_IS_NOT_MUTUALLY_EXCLUSIVE_VAR = "notMutuallyExclusive";
    public static final String CATEGORY_IS_NOT_MUTUALLY_EXCLUSIVE_VAR_DESC = "Is Not Mutually Exclusive";

    /* User Filter Constants */
    public static final String USER_IDS_FILTER_VAR = "userIds";
    public static final String USER_IDS_FILTER_VAR_DESC = "User IDs";

    public static final String USER_NAME_FILTER_VAR = "username";
    public static final String USER_NAME_MATCHES_FILTER_VAR = USER_NAME_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String USER_NAME_FILTER_VAR_DESC = "Username";

    public static final String USER_DESCRIPTION_FILTER_VAR = "userDesc";
    public static final String USER_DESCRIPTION_MATCHES_FILTER_VAR = USER_DESCRIPTION_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String USER_DESCRIPTION_FILTER_VAR_DESC = "User Description";

    /* Image Filter Constants */
    public static final String IMAGE_IDS_FILTER_VAR = "imageIds";
    public static final String IMAGE_IDS_FILTER_VAR_DESC = "Image IDs";

    public static final String IMAGE_DESCRIPTION_FILTER_VAR = "imageDesc";
    public static final String IMAGE_DESCRIPTION_MATCHES_FILTER_VAR = IMAGE_DESCRIPTION_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String IMAGE_DESCRIPTION_FILTER_VAR_DESC = "Image Description";

    public static final String IMAGE_ORIGINAL_FILENAME_FILTER_VAR = "imageOrigName";
    public static final String IMAGE_ORIGINAL_FILENAME_MATCHES_FILTER_VAR = IMAGE_ORIGINAL_FILENAME_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String IMAGE_ORIGINAL_FILENAME_FILTER_VAR_DESC = "Image Original Filename";

    /* Project Filter Constant */
    public static final String PROJECT_IDS_FILTER_VAR = "projectIds";
    public static final String PROJECT_IDS_FILTER_VAR_DESC = "Project IDs";

    public static final String PROJECT_NAME_FILTER_VAR = "projectName";
    public static final String PROJECT_NAME_MATCHES_FILTER_VAR = PROJECT_NAME_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String PROJECT_NAME_FILTER_VAR_DESC = "Project Name";

    public static final String PROJECT_DESCRIPTION_FILTER_VAR = "projectDesc";
    public static final String PROJECT_DESCRIPTION_MATCHES_FILTER_VAR = PROJECT_DESCRIPTION_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String PROJECT_DESCRIPTION_FILTER_VAR_DESC = "Project Description";

    /* Filter Constants */
    public static final String FILTER_IDS_FILTER_VAR = "filterIds";
    public static final String FILTER_IDS_FILTER_VAR_DESC = "Filter IDs";

    public static final String FILTER_NAME_FILTER_VAR = "filterName";
    public static final String FILTER_NAME_MATCHES_FILTER_VAR = FILTER_NAME_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String FILTER_NAME_FILTER_VAR_DESC = "Filter Name";

    public static final String FILTER_DESCRIPTION_FILTER_VAR = "filterDesc";
    public static final String FILTER_DESCRIPTION_MATCHES_FILTER_VAR = FILTER_DESCRIPTION_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String FILTER_DESCRIPTION_FILTER_VAR_DESC = "Filter Description";

    public static final String FILTER_TYPE_FILTER_VAR = "filterType";
    public static final String FILTER_TYPE_FILTER_VAR_DESC = "Filter Type";

    /* PropertyTag Constants */
    public static final String PROP_TAG_IDS_FILTER_VAR = "propTagIds";
    public static final String PROP_TAG_IDS_FILTER_VAR_DESC = "Property Tag IDs";

    public static final String PROP_TAG_NAME_FILTER_VAR = "propTagName";
    public static final String PROP_TAG_NAME_MATCHES_FILTER_VAR = PROP_TAG_NAME_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String PROP_TAG_NAME_FILTER_VAR_DESC = "Property Tag Name";

    public static final String PROP_TAG_DESCRIPTION_FILTER_VAR = "propTagDesc";
    public static final String PROP_TAG_DESCRIPTION_MATCHES_FILTER_VAR = PROP_TAG_DESCRIPTION_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String PROP_TAG_DESCRIPTION_FILTER_VAR_DESC = "Property Tag Description";

    /* BlobConstant Constants */
    public static final String BLOB_CONSTANT_IDS_FILTER_VAR = "blobConstantIds";
    public static final String BLOB_CONSTANT_IDS_FILTER_VAR_DESC = "Blob Constant IDs";

    public static final String BLOB_CONSTANT_NAME_FILTER_VAR = "blobConstantName";
    public static final String BLOB_CONSTANT_NAME_MATCHES_FILTER_VAR = BLOB_CONSTANT_NAME_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String BLOB_CONSTANT_NAME_FILTER_VAR_DESC = "Blob Constant Name";

    /* StringConstant Constants */
    public static final String STRING_CONSTANT_IDS_FILTER_VAR = "stringConstantIds";
    public static final String STRING_CONSTANT_IDS_FILTER_VAR_DESC = "String Constant IDs";

    public static final String STRING_CONSTANT_NAME_FILTER_VAR = "stringConstantName";
    public static final String STRING_CONSTANT_NAME_MATCHES_FILTER_VAR = STRING_CONSTANT_NAME_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String STRING_CONSTANT_NAME_FILTER_VAR_DESC = "String Constant Name";

    public static final String STRING_CONSTANT_VALUE_FILTER_VAR = "stringConstantValue";
    public static final String STRING_CONSTANT_VALUE_MATCHES_FILTER_VAR = STRING_CONSTANT_VALUE_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String STRING_CONSTANT_VALUE_FILTER_VAR_DESC = "String Constant Value";

    /* Role Constants */
    public static final String ROLE_IDS_FILTER_VAR = "roleIds";
    public static final String ROLE_IDS_FILTER_VAR_DESC = "Role IDs";

    public static final String ROLE_NAME_FILTER_VAR = "roleName";
    public static final String ROLE_NAME_MATCHES_FILTER_VAR = ROLE_NAME_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String ROLE_NAME_FILTER_VAR_DESC = "Role Name";

    public static final String ROLE_DESCRIPTION_FILTER_VAR = "roleDesc";
    public static final String ROLE_DESCRIPTION_MATCHES_FILTER_VAR = ROLE_DESCRIPTION_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String ROLE_DESCRIPTION_FILTER_VAR_DESC = "Role Description";

    /* IntegerConstant Constants */
    public static final String INTEGER_CONSTANT_IDS_FILTER_VAR = "integerConstantIds";
    public static final String INTEGER_CONSTANT_IDS_FILTER_VAR_DESC = "Integer Constant IDs";

    public static final String INTEGER_CONSTANT_NAME_FILTER_VAR = "integerConstantName";
    public static final String INTEGER_CONSTANT_NAME_MATCHES_FILTER_VAR = INTEGER_CONSTANT_NAME_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String INTEGER_CONSTANT_NAME_FILTER_VAR_DESC = "Integer Constant Name";

    public static final String INTEGER_CONSTANT_VALUE_FILTER_VAR = "integerConstantValue";
    public static final String INTEGER_CONSTANT_VALUE_FILTER_VAR_DESC = "Integer Constant Value";

    /* PropertyTagCategory Constants */
    public static final String PROP_CATEGORY_IDS_FILTER_VAR = "propCategoryIds";
    public static final String PROP_CATEGORY_IDS_FILTER_VAR_DESC = "Property Tag Category IDs";

    public static final String PROP_CATEGORY_NAME_FILTER_VAR = "propCategoryName";
    public static final String PROP_CATEGORY_NAME_MATCHES_FILTER_VAR = PROP_CATEGORY_NAME_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String PROP_CATEGORY_NAME_FILTER_VAR_DESC = "Property Tag Category Name";

    public static final String PROP_CATEGORY_DESCRIPTION_FILTER_VAR = "propCategoryDesc";
    public static final String PROP_CATEGORY_DESCRIPTION_MATCHES_FILTER_VAR = PROP_CATEGORY_DESCRIPTION_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String PROP_CATEGORY_DESCRIPTION_FILTER_VAR_DESC = "Property Tag Category Description";

    /* ContentSpec Constants */
    public static final String CONTENT_SPEC_IDS_FILTER_VAR = "contentSpecIds";
    public static final String CONTENT_SPEC_IDS_FILTER_VAR_DESC = "Content Specification IDs";

    public static final String CONTENT_SPEC_TYPE_FILTER_VAR = "contentSpecType";
    public static final String CONTENT_SPEC_TYPE_FILTER_VAR_DESC = "Content Specification Type";

    public static final String CONTENT_SPEC_TITLE_FILTER_VAR = "contentSpecTitle";
    public static final String CONTENT_SPEC_TITLE_MATCHES_FILTER_VAR = CONTENT_SPEC_TITLE_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String CONTENT_SPEC_TITLE_FILTER_VAR_DESC = "Content Specification Title";

    public static final String CONTENT_SPEC_SUBTITLE_FILTER_VAR = "contentSpecSubtitle";
    public static final String CONTENT_SPEC_SUBTITLE_MATCHES_FILTER_VAR = CONTENT_SPEC_SUBTITLE_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String CONTENT_SPEC_SUBTITLE_FILTER_VAR_DESC = "Content Specification Subtitle";

    public static final String CONTENT_SPEC_PRODUCT_FILTER_VAR = "contentSpecProduct";
    public static final String CONTENT_SPEC_PRODUCT_MATCHES_FILTER_VAR = CONTENT_SPEC_PRODUCT_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String CONTENT_SPEC_PRODUCT_FILTER_VAR_DESC = "Content Specification Product";

    public static final String CONTENT_SPEC_VERSION_FILTER_VAR = "contentSpecVersion";
    public static final String CONTENT_SPEC_VERSION_MATCHES_FILTER_VAR = CONTENT_SPEC_VERSION_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String CONTENT_SPEC_VERSION_FILTER_VAR_DESC = "Content Specification Product Version";

    public static final String CONTENT_SPEC_EDITION_FILTER_VAR = "contentSpecEdition";
    public static final String CONTENT_SPEC_EDITION_MATCHES_FILTER_VAR = CONTENT_SPEC_EDITION_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String CONTENT_SPEC_EDITION_FILTER_VAR_DESC = "Content Specification Edition";

    public static final String CONTENT_SPEC_BOOK_VERSION_FILTER_VAR = "contentSpecBookVersion";
    public static final String CONTENT_SPEC_BOOK_VERSION_MATCHES_FILTER_VAR = CONTENT_SPEC_BOOK_VERSION_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String CONTENT_SPEC_BOOK_VERSION_FILTER_VAR_DESC = "Content Specification Book Version";

    public static final String CONTENT_SPEC_PUBSNUMBER_FILTER_VAR = "contentSpecPubsnumber";
    public static final String CONTENT_SPEC_PUBSNUMBER_MATCHES_FILTER_VAR = CONTENT_SPEC_PUBSNUMBER_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String CONTENT_SPEC_PUBSNUMBER_FILTER_VAR_DESC = "Content Specification Pubsnumber";

    public static final String CONTENT_SPEC_ABSTRACT_FILTER_VAR = "contentSpecAbstract";
    public static final String CONTENT_SPEC_ABSTRACT_MATCHES_FILTER_VAR = CONTENT_SPEC_ABSTRACT_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String CONTENT_SPEC_ABSTRACT_FILTER_VAR_DESC = "Content Specification Abstract";

    public static final String CONTENT_SPEC_BRAND_FILTER_VAR = "contentSpecBrand";
    public static final String CONTENT_SPEC_BRAND_MATCHES_FILTER_VAR = CONTENT_SPEC_BRAND_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String CONTENT_SPEC_BRAND_FILTER_VAR_DESC = "Content Specification Brand";

    public static final String CONTENT_SPEC_COPYRIGHT_HOLDER_FILTER_VAR = "contentSpecCopyrightHolder";
    public static final String CONTENT_SPEC_COPYRIGHT_HOLDER_MATCHES_FILTER_VAR = CONTENT_SPEC_COPYRIGHT_HOLDER_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String CONTENT_SPEC_COPYRIGHT_HOLDER_FILTER_VAR_DESC = "Content Specification Copyright Holder";

    public static final String CONTENT_SPEC_COPYRIGHT_YEAR_FILTER_VAR = "contentSpecCopyrightYear";
    public static final String CONTENT_SPEC_COPYRIGHT_YEAR_MATCHES_FILTER_VAR = CONTENT_SPEC_COPYRIGHT_YEAR_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String CONTENT_SPEC_COPYRIGHT_YEAR_FILTER_VAR_DESC = "Content Specification Copyright Year";

    public static final String CONTENT_SPEC_PUBLICAN_CFG_FILTER_VAR = "contentSpecPublicanCfg";
    public static final String CONTENT_SPEC_PUBLICAN_CFG_MATCHES_FILTER_VAR = CONTENT_SPEC_PUBLICAN_CFG_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String CONTENT_SPEC_PUBLICAN_CFG_FILTER_VAR_DESC = "Content Specification publican.cfg";

    public static final String CONTENT_SPEC_DTD_FILTER_VAR = "contentSpecDTD";
    public static final String CONTENT_SPEC_DTD_MATCHES_FILTER_VAR = CONTENT_SPEC_DTD_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String CONTENT_SPEC_DTD_FILTER_VAR_DESC = "Content Specification DTD";

    public static final String HAS_ERRORS_FILTER_VAR = "hasErrors";
    public static final String HAS_ERRORS_FILTER_VAR_DESC = "Has Errors";

    /* CSNode Constants */
    public static final String CONTENT_SPEC_NODE_IDS_FILTER_VAR = "csNodeIds";
    public static final String CONTENT_SPEC_NODE_IDS_FILTER_VAR_DESC = "Content Specification Node IDs";

    public static final String CONTENT_SPEC_NODE_TITLE_FILTER_VAR = "csNodeTitle";
    public static final String CONTENT_SPEC_NODE_TITLE_MATCHES_FILTER_VAR = CONTENT_SPEC_NODE_TITLE_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String CONTENT_SPEC_NODE_TITLE_FILTER_VAR_DESC = "Content Specification Node Title";

    public static final String CONTENT_SPEC_NODE_TYPE_FILTER_VAR = "csNodeType";
    public static final String CONTENT_SPEC_NODE_TYPE_FILTER_VAR_DESC = "Content Specification Node Type";

    public static final String CONTENT_SPEC_NODE_ENTITY_ID_FILTER_VAR = "csNodeEntityId";
    public static final String CONTENT_SPEC_NODE_ENTITY_ID_FILTER_VAR_DESC = "Content Specification Node Entity ID";

    public static final String CONTENT_SPEC_NODE_ENTITY_REVISION_FILTER_VAR = "csNodeEntityRev";
    public static final String CONTENT_SPEC_NODE_ENTITY_REVISION_FILTER_VAR_DESC = "Content Specification Node Entity Revision";

    /* Translated ContentSpec Constants */
    public static final String TRANSLATED_CONTENT_SPEC_IDS_FILTER_VAR = "translatedContentSpecIds";
    public static final String TRANSLATED_CONTENT_SPEC_IDS_FILTER_VAR_DESC = "Translated Content Specification IDs";

    /* TranslatedCSNode Constants */
    public static final String CONTENT_SPEC_TRANSLATED_NODE_IDS_FILTER_VAR = "translatedCSNodeIds";
    public static final String CONTENT_SPEC_TRANSLATED_NODE_IDS_FILTER_VAR_DESC = "Translated Content Specification Node IDs";

    /* File Filter Constants */
    public static final String FILE_IDS_FILTER_VAR = "fileIds";
    public static final String FILE_IDS_FILTER_VAR_DESC = "File IDs";

    public static final String FILE_DESCRIPTION_FILTER_VAR = "fileDesc";
    public static final String FILE_DESCRIPTION_MATCHES_FILTER_VAR = FILE_DESCRIPTION_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String FILE_DESCRIPTION_FILTER_VAR_DESC = "File Description";

    public static final String FILE_NAME_FILTER_VAR = "fileName";
    public static final String FILE_NAME_MATCHES_FILTER_VAR = FILE_NAME_FILTER_VAR + STRING_MATCHES_SUFFIX;
    public static final String FILE_NAME_FILTER_VAR_DESC = "File Name";
}
