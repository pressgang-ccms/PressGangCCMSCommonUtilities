/*
  Copyright 2011-2014 Red Hat, Inc

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

package org.jboss.pressgang.ccms.utils.sort;

import java.util.Comparator;

import org.jboss.pressgang.ccms.utils.structures.EntitySubstitutionBoundaryData;

public class EntitySubstitutionBoundaryDataBoundaryStartSort implements Comparator<EntitySubstitutionBoundaryData>
{
	public int compare(final EntitySubstitutionBoundaryData o1, final EntitySubstitutionBoundaryData o2)
	{
		if (o1 == null && o2 == null)
			return 0;
		if (o1 == null)
			return -1;
		if (o2 == null)
			return 1;
		
		if (o1.getBoundary() == null && o2.getBoundary() == null)
			return 0;
		if (o1.getBoundary() == null)
			return -1;
		if (o2.getBoundary() == null)
			return 1;
		
		if (o1.getBoundary().getFirst() == null && o2.getBoundary().getFirst() == null)
			return 0;
		if (o1.getBoundary().getFirst() == null)
			return -1;
		if (o2.getBoundary().getFirst() == null)
			return 1;
		
		return o1.getBoundary().getFirst().compareTo(o2.getBoundary().getFirst());
	}

}
