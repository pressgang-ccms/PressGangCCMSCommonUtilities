package org.jboss.pressgang.ccms.utils.structures;

import org.jboss.pressgang.ccms.utils.constants.CommonConstants;

public enum DocBookVersion {
    DOCBOOK_45(CommonConstants.DOCBOOK_45, CommonConstants.DOCBOOK_45_TITLE),
    DOCBOOK_50(CommonConstants.DOCBOOK_50, CommonConstants.DOCBOOK_50_TITLE);

    private final Integer id;
    private final String title;

    DocBookVersion(final Integer id, final String title) {
        this.id = id;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public Integer getId() {
        return id;
    }

    public static DocBookVersion getVersionFromId(final Integer id) {
        switch (id) {
            case CommonConstants.DOCBOOK_45:
                return DOCBOOK_45;
            case CommonConstants.DOCBOOK_50:
                return DOCBOOK_50;
            default:
                return null;
        }
    }
}
