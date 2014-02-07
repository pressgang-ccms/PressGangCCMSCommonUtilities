package org.jboss.pressgang.ccms.utils.structures;

import org.jboss.pressgang.ccms.utils.constants.CommonConstants;

public enum DocBookVersion {
    DOCBOOK_45(CommonConstants.DOCBOOK_45_TITLE), DOCBOOK_50(CommonConstants.DOCBOOK_50_TITLE);

    private final String title;

    DocBookVersion(final String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
