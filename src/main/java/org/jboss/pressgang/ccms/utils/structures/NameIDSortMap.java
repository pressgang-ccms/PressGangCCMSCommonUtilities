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

import org.jboss.pressgang.ccms.utils.common.CollectionUtilities;

/**
 * A simple utility class used to store the essential details of an entity that
 * has an integer primary key, a name, and a sorting order
 */
public class NameIDSortMap implements Comparable<NameIDSortMap>
{
	protected Integer id;
	protected String name;
	protected Integer sort;

	public String getName()
	{
		return name;
	}

	public void setName(final String value)
	{
		name = value;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(final Integer value)
	{
		id = value;
	}

	public Integer getSort()
	{
		return sort;
	}

	public void setSort(final Integer value)
	{
		this.sort = value;
	}

	public NameIDSortMap(final String name, final Integer id, final Integer sort)
	{
		this.name = name;
		this.id = id;
		this.sort = sort;
	}

	public int compareTo(final NameIDSortMap other)
	{
		if (this.equals(other))
			return 0;

		final int sortOrder = CollectionUtilities.getSortOrder(this.sort, other.sort);
		if (sortOrder != 0)
			return sortOrder;

		final int nameOrder = CollectionUtilities.getSortOrder(this.name, other.name);
		if (nameOrder != 0)
			return nameOrder;

		final int idOrder = CollectionUtilities.getSortOrder(this.id, other.id);
		return idOrder;
	}

	@Override
	public boolean equals(final Object other)
	{
		if (!(other instanceof NameIDSortMap))
			return false;

		final NameIDSortMap otherNameIDSortMap = (NameIDSortMap) other;

		return CollectionUtilities.isEqual(this.name, otherNameIDSortMap.name) && 
				CollectionUtilities.isEqual(this.id, otherNameIDSortMap.id) && 
				CollectionUtilities.isEqual(this.sort, otherNameIDSortMap.sort);
	}

	@Override
	public int hashCode()
	{
		int hash = 1;
		hash = hash * 31 + (this.id == null ? 0 : this.id.hashCode());
		hash = hash * 31 + (this.name == null ? 0 : this.name.hashCode());
		hash = hash * 31 + (this.sort == null ? 0 : this.sort.hashCode());
		return hash;
	}
}
