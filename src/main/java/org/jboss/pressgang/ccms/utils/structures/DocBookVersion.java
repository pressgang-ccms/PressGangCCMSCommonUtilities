/*
  Copyright 2011-2014 Red Hat, Inc, Inc

  This file is part of PressGang CCMS.

  PressGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PressGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PressGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

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
