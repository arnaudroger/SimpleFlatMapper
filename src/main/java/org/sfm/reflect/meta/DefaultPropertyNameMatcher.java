package org.sfm.reflect.meta;

public class DefaultPropertyNameMatcher implements PropertyNameMatcher {
	private final String column;
	private final int from;

	public DefaultPropertyNameMatcher(final String column) {
		this(column, 0);
	}

	public DefaultPropertyNameMatcher(final String column, final int from) {
		this.column = column;
		this.from = from;
	}

	@Override
	public boolean matches(final String property) {
		return _partialMatch(property) == column.length();
	}

	@Override
	public IndexedColumn matchesIndex() {
		int index = -1;

		int listIndexStart = from;
		while(listIndexStart < column.length() &&  !Character.isDigit(column.charAt(listIndexStart))) {
			listIndexStart++;
		}

		int listIndexEnd = listIndexStart;
		while(listIndexEnd < column.length() &&  Character.isDigit(column.charAt(listIndexEnd))) {
			listIndexEnd++;
		}
		if (listIndexStart != listIndexEnd) {
			index = Integer.parseInt(column.substring(listIndexStart, listIndexEnd));
		}

		if (index == -1) {
			return null;
		}

		String indexName = column.substring(0, listIndexEnd);


		PropertyNameMatcher subPropertyNameMatcher = null;


		if (listIndexEnd < column.length()) {
			subPropertyNameMatcher = new DefaultPropertyNameMatcher(column, listIndexEnd );
		}

		return new IndexedColumn(indexName, index, subPropertyNameMatcher);
	}

	private int _partialMatch(final String property) {
		int indexColumn = from;
		int indexProperty = 0;

		do {
			if (indexProperty < property.length()) {
				char charProperty = property.charAt(indexProperty);
				
				if (indexColumn < column.length()) {
					char charColumn = column.charAt(indexColumn);
					indexColumn ++;
					
					if (ignoreCharacter(charColumn)) {
						if (ignoreCharacter(charProperty)) {
							indexProperty++;
						}
					} else if (Character.toLowerCase(charProperty) != Character.toLowerCase(charColumn)) {
						return -1;
					} else {
						indexProperty++;
					}
				} else {
					return -1;
				}
			} else {
				// partial match
				return indexColumn;
			}
		}
		while(true);
	}

	private boolean ignoreCharacter(final char charColumn) {
		return charColumn == '_' || charColumn == ' ' || charColumn == '.';
	}
	
	@Override
	public PropertyNameMatcher partialMatch(final String property) {
		int index = _partialMatch(property);
		if (index != -1) {
			return new DefaultPropertyNameMatcher(column, index);
		} else {
			return null;
		}
	}
}
