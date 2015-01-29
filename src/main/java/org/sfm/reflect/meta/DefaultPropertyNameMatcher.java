package org.sfm.reflect.meta;

public final class DefaultPropertyNameMatcher implements PropertyNameMatcher {
	private final String column;
	private final int from;

	private final boolean exactMatch;
	private final boolean caseSensitive;

	public DefaultPropertyNameMatcher(String column, int from, boolean exactMatch, boolean caseSensitive) {
		this.column = column;
		this.from = from;
		this.exactMatch = exactMatch;
		this.caseSensitive = caseSensitive;
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
			subPropertyNameMatcher = new DefaultPropertyNameMatcher(column, listIndexEnd, exactMatch, caseSensitive );
		}

		return new IndexedColumn(index, subPropertyNameMatcher);
	}

	private int _partialMatch(final String property) {
		int indexColumn = from;
		int indexProperty = 0;
		boolean nextToUpperCase = false;
		do {
			if (indexProperty < property.length()) {
				char charProperty = property.charAt(indexProperty);
				
				if (indexColumn < column.length()) {
					char charColumn = column.charAt(indexColumn);
					if (nextToUpperCase) {
						charColumn = Character.toUpperCase(charColumn);
						nextToUpperCase = false;
					}
					indexColumn ++;
					
					if (ignoreCharacter(charColumn)) {
						if (ignoreCharacter(charProperty)) {
							indexProperty++;
						}
						if (caseSensitive) {
							nextToUpperCase = true;
						}
					} else if (areDifferentCharacters(charProperty, charColumn)) {
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

	private boolean areDifferentCharacters(char c1, char c2) {
		if (caseSensitive) {
			return c1 != c2;
		} else {
			return Character.toLowerCase(c1) != Character.toLowerCase(c2);
		}
	}

	private boolean ignoreCharacter(final char charColumn) {
		return !exactMatch && (charColumn == '_' || charColumn == ' ' || charColumn == '.');
	}
	
	@Override
	public PropertyNameMatcher partialMatch(final String property) {
		int index = _partialMatch(property);
		if (index != -1) {
			return new DefaultPropertyNameMatcher(column, index, exactMatch, caseSensitive);
		} else {
			return null;
		}
	}

	@Override
	public PropertyNameMatcher newMatcher(String name) {
		return new DefaultPropertyNameMatcher(name, 0, exactMatch, caseSensitive);
	}
}
