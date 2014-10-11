package org.sfm.csv.parser;

enum CsvParserState {
	IN_QUOTE, QUOTE, NONE, IN_CR
}